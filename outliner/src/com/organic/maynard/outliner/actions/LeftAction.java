/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner.actions;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.organic.maynard.util.string.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class LeftAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("LeftAction");
		
		OutlinerCellRendererImpl textArea  = null;
		boolean isIconFocused = true;
		Component c = (Component) e.getSource();
		if (c instanceof OutlineButton) {
			textArea = ((OutlineButton) c).renderer;
		} else if (c instanceof OutlineLineNumber) {
			textArea = ((OutlineLineNumber) c).renderer;
		} else if (c instanceof OutlineCommentIndicator) {
			textArea = ((OutlineCommentIndicator) c).renderer;
		} else if (c instanceof OutlinerCellRendererImpl) {
			textArea = (OutlinerCellRendererImpl) c;
			isIconFocused = false;
		}
		
		// Shorthand
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		//System.out.println(e.getModifiers());
		switch (e.getModifiers()) {
			case 0:
				if (isIconFocused) {
					UpAction.navigate(tree, layout, UpAction.LEFT);
				} else {
					moveLeftText(textArea, tree, layout);
				}
				break;
			case 1:
				if (isIconFocused) {
					UpAction.select(tree, layout, UpAction.LEFT);
				} else {
					
				}
				break;
			case 2:
				if (isIconFocused) {
					moveLeft(textArea, tree,layout);
				} else {
					
				}
				break;
			case 3:
				if (isIconFocused) {
					UpAction.deselect(tree, layout, UpAction.LEFT);
				} else {
					
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void moveLeftText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		if (textArea.getCaretPosition() == 0) {
			// Get Prev Node
			Node prevNode = tree.getPrevNode(currentNode);
			if (prevNode == null) {
				tree.setCursorPosition(tree.getCursorPosition()); // Makes sure we reset the mark
				return;
			}
			
			// Update Preferred Caret Position
			int newLength = prevNode.getValue().length();
			tree.getDocument().setPreferredCaretPosition(newLength);
	
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(prevNode);
			tree.setCursorPosition(newLength);
	
			// Clear Text Selection
			textArea.setCaretPosition(0);
			textArea.moveCaretPosition(0);
	
			// Redraw and Set Focus
			if (prevNode.isVisible()) {
				layout.setFocus(prevNode,OutlineLayoutManager.TEXT);
			} else {
				layout.draw(prevNode,OutlineLayoutManager.TEXT);
			}
		} else {
			// Update Preferred Caret Position
			tree.getDocument().setPreferredCaretPosition(textArea.getCaretPosition() - 1);
	
			// Record the CursorPosition only since the EditingNode should not have changed
			int newCaretPosition = textArea.getCaretPosition() - 1;
			textArea.setCaretPosition(newCaretPosition);
			textArea.moveCaretPosition(newCaretPosition);
			tree.setCursorPosition(newCaretPosition);
	
			// Redraw and Set Focus if this node is currently offscreen
			if (!currentNode.isVisible()) {
				layout.draw(currentNode,OutlineLayoutManager.TEXT);
			}
		}

		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);
	}


	// IconFocusedMethods
	public static void moveLeft(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		Node youngestNode = tree.getYoungestInSelection();
		Node node = tree.getPrevNode(youngestNode);
		if (node == null) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(), node.getParent());
		int targetIndex = node.currentIndex();
		int currentIndexAdj = 0;
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			int currentIndex = nodeToMove.currentIndex() + currentIndexAdj;
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, currentIndex, targetIndex));
			
			if (nodeToMove.getParent() != node.getParent()) {
				currentIndexAdj--;
			}
			targetIndex++;
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().getUndoQueue().add(undoable);
			undoable.redo();
		}		
	}
}