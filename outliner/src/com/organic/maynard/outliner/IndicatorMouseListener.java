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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

public class IndicatorMouseListener implements MouseListener {

	// Instance Fields
	private OutlinerCellRendererImpl textArea = null;

	// The Constructors
	public IndicatorMouseListener() {}
	
	public void destroy() {
		textArea = null;
	}	

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		Component c = e.getComponent();
		if (c instanceof OutlineCommentIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineCommentIndicator.TRUE_WIDTH) && (p.y <= OutlineCommentIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineCommentIndicator) c).renderer;
				Node node = textArea.node;
	 			TreeContext tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearComment(tree);
					} else {
						toggleCommentInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleComment(tree);
				} else {
					toggleCommentAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.doc.panel.layout.draw();
				tree.doc.panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		} else if (c instanceof OutlineEditableIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineEditableIndicator.TRUE_WIDTH) && (p.y <= OutlineEditableIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineEditableIndicator) c).renderer;
				Node node = textArea.node;
	 			TreeContext tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearEditable(tree);
					} else {
						toggleEditableInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleEditable(tree);
				} else {
					toggleEditableAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.doc.panel.layout.draw();
				tree.doc.panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		} else if (c instanceof OutlineMoveableIndicator) {
			// Make sure it's in the icon, not just the JLabel.
			Point p = e.getPoint();
			if ((p.x <= OutlineMoveableIndicator.TRUE_WIDTH) && (p.y <= OutlineMoveableIndicator.BUTTON_HEIGHT)) {
				textArea = ((OutlineMoveableIndicator) c).renderer;
				Node node = textArea.node;
	 			TreeContext tree = textArea.node.getTree();
				
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearMoveable(tree);
					} else {
						toggleMoveableInheritance(tree);
					}
				} else if (e.isShiftDown()) {
					toggleMoveable(tree);
				} else {
					toggleMoveableAndClear(tree);
				}			
	
				// Redraw and set focus
				tree.doc.panel.layout.draw();
				tree.doc.panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());
			}
		}
	}
	
	// Comments
	private void clearComment(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.clearCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleCommentAndClear(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleComment(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleCommentInheritance(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	// Editable
	private void clearEditable(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.clearEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleEditableAndClear(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleEditableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleEditable(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleEditableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleEditableInheritance(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleEditableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	// Moveable
	private void clearMoveable(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.clearMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleMoveableAndClear(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleMoveableAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleMoveable(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleMoveableForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleMoveableInheritance(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleMoveableInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}
}