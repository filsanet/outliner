/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner;

import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class IconKeyListener implements KeyListener, MouseListener, FocusListener {

	// Expand/Collapse Mode Constants
	public static final int MODE_EXPAND_DOUBLE_CLICK = 0;
	public static final int MODE_EXPAND_SINGLE_CLICK = 1;
	
	// Constants for setting cursor position.
	private static final int POSITION_FIRST = 0;
	private static final int POSITION_CURRENT = 1;
	private static final int POSITION_LAST = 2;


	// Instance Fields
	private OutlinerCellRendererImpl textArea = null;
	public static int expand_mode = MODE_EXPAND_DOUBLE_CLICK;


	// The Constructors
	public IconKeyListener() {}
	
	public void destroy() {
		textArea = null;
	}

	
	private void recordRenderer(Component c) {
		if (c instanceof OutlineButton) {
			textArea = ((OutlineButton) c).renderer;
		} else if (c instanceof OutlineLineNumber) {
			textArea = ((OutlineLineNumber) c).renderer;
		} else if (c instanceof OutlineCommentIndicator) {
			textArea = ((OutlineCommentIndicator) c).renderer;
		}
	}


	// FocusListener Interface
	public void focusGained(FocusEvent e) {
		recordRenderer(e.getComponent());
		textArea.hasFocus = true;
	}

	public void focusLost(FocusEvent e) {
		recordRenderer(e.getComponent());
		textArea.hasFocus = false;
	}

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		recordRenderer(e.getComponent());

 		// Shorthand
 		Node currentNode = textArea.node;
 		JoeTree tree = currentNode.getTree();
 		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		// This is detection for Solaris, I think mac does this too.
		if (e.isPopupTrigger() && (currentNode.isAncestorSelected() || (tree.getEditingNode() == currentNode))) {
			Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			e.consume();
			return;
		}

		// This is to block clicks when a right click is generated in windows.
		if ((PlatformCompatibility.isWindows()) && e.getModifiers() == InputEvent.BUTTON3_MASK) {
			return;
		}
				
		// Handle clicks. The modulo is to deal with rapid clicks that would register as a triple click or more.
		if ((e.getClickCount() % 2) == 1) {
			processSingleClick(e);
			if (MODE_EXPAND_SINGLE_CLICK == expand_mode) {
				processDoubleClick(e);
			}
		} else if ((e.getClickCount() % 2) == 0){
			processDoubleClick(e);
		}
		
		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(textArea.node);
		tree.setComponentFocus(OutlineLayoutManager.ICON);
		
		// Redraw and set focus
		layout.redraw();
		
		// Consume the current event and then propogate a new event to
		// the DnD listener since if a drawUp() happened, the old event
		// will most likely have an invalid component.
		e.consume();

		MouseEvent eNew = new MouseEvent(
			tree.getDocument().panel.layout.getUIComponent(currentNode).button, 
			e.getID(), 
			e.getWhen(), 
			e.getModifiers(), 
			e.getX(), 
			e.getY(), 
			e.getClickCount(), 
			e.isPopupTrigger()
		);
		tree.getDocument().panel.layout.dndListener.mousePressed(eNew);
	}
	
	public void mouseReleased(MouseEvent e) {
 		// Catch for Solaris/Mac if they did the popup trigger.
 		if (e.isConsumed()) {
 			return;
 		}
 
 		recordRenderer(e.getComponent());

		// Shorthand
		Node currentNode = textArea.node;
 		JoeTree tree = currentNode.getTree();

		// This is detection for Windows
		if (e.isPopupTrigger() && (currentNode.isAncestorSelected() || (tree.getEditingNode() == currentNode))) {
			Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			return;
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	    
	protected void processSingleClick(MouseEvent e) {
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		
		if (e.isShiftDown()) {
			tree.selectRangeFromMostRecentNodeTouched(node);
		
		} else if (e.isControlDown()) {
			if (node.isSelected() && (tree.getSelectedNodes().size() != 1)) {
				tree.removeNodeFromSelection(node);
			} else if (tree.getSelectedNodesParent() == node.getParent()) {
				tree.addNodeToSelection(node);
			}
			
		} else if (!node.isSelected()) {
			tree.setSelectedNodesParent(node.getParent());
			tree.addNodeToSelection(node);
		}	
	}
	
	protected void processDoubleClick(MouseEvent e) {
		if (MODE_EXPAND_DOUBLE_CLICK == expand_mode) {
			textArea.node.getTree().setSelectedNodesParent(textArea.node.getParent());
			textArea.node.getTree().addNodeToSelection(textArea.node);

			if (textArea.node.isExpanded()) {
				if (e.isShiftDown()) {
					textArea.node.setExpanded(false, false);
				} else {
					textArea.node.setExpanded(false, true);
				}
			} else {
				if (e.isShiftDown()) {
					textArea.node.ExpandAllSubheads();
				} else {
					textArea.node.setExpanded(true, true);
				}
			}			
		} else {
			// MODE_EXPAND_SINGLE_CLICK
			if (textArea.node.isExpanded()) {
				textArea.node.setExpanded(false, true);
			} else {
				textArea.node.setExpanded(true, true);
			}	
		}
	}
	
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {
 		recordRenderer(e.getComponent());
 		
  		if (!textArea.hasFocus) {
 			return;
 		}
 		
		// Create some short names for convienence
		Node currentNode = textArea.node;
		JoeTree tree = currentNode.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;
		Node youngestNode = tree.getYoungestInSelection();

		// If we're read-only then abort
		if (!currentNode.isEditable()) {
			return;
		}
				
		// Catch any unwanted chars that slip through
		if (e.isControlDown() ||
			(e.getKeyChar() == KeyEvent.VK_BACK_SPACE) ||
			(e.getKeyChar() == KeyEvent.VK_TAB) ||
			(e.getKeyChar() == KeyEvent.VK_ENTER) ||
			(e.getKeyChar() == KeyEvent.VK_INSERT)
		) {
			return;
		}

		// Clear the selection since focus will change to the textarea.
		tree.clearSelection();
		
		// Replace the text with the character that was typed
		String oldText = youngestNode.getValue();
		String newText = String.valueOf(e.getKeyChar());
		youngestNode.setValue(newText);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(youngestNode);
		tree.setCursorPosition(1);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		tree.getDocument().undoQueue.add(new UndoableEdit(youngestNode, oldText, newText, 0, 1, 0, 1));
		
		// Redraw and Set Focus
		layout.draw(youngestNode, OutlineLayoutManager.TEXT);

		e.consume();
		return;
	}
	
	public void keyReleased(KeyEvent e) {}


	// Additional Outline Methods
	public static void hoist(JoeTree tree) {
		if (tree.getSelectedNodes().size() != 1) {
			return;
		}
		
		TextKeyListener.hoist(tree.getYoungestInSelection());
		return;
	}

	public static void expandAllSubheads(JoeTree tree) {
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			nodeList.get(i).ExpandAllSubheads();
		}
		tree.getDocument().panel.layout.redraw();
		return;
	}

	public static void expandEverything(JoeTree tree) {
		TextKeyListener.expandEverything(tree);
		return;
	}

	public static void collapseToParent(JoeTree tree) {
		TextKeyListener.collapseToParent(tree.getEditingNode());
		return;
	}

	public static void collapseEverything(JoeTree tree) {
		TextKeyListener.collapseEverything(tree);
		return;
	}
}