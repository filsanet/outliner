/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class TextKeyListener implements KeyListener, MouseListener, FocusListener {
	
	private OutlinerCellRendererImpl textArea = null;


	// The Constructors
	public TextKeyListener() {}

	public void destroy() {
		textArea = null;
	}
	
	
	// FocusListener Interface
	public void focusGained(FocusEvent e) {
		textArea = (OutlinerCellRendererImpl) e.getComponent();
		textArea.hasFocus = true;
	}

	public void focusLost(FocusEvent e) {
		textArea = (OutlinerCellRendererImpl) e.getComponent();
		textArea.hasFocus = false;
	}
	

	// MouseListener Interface
 	public void mouseEntered(MouseEvent e) {}
 	public void mouseExited(MouseEvent e) {}
 	
 	public void mousePressed(MouseEvent e) {
 		textArea = (OutlinerCellRendererImpl) e.getComponent();
 		
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
			
 		// Clear the selection
 		int selectionSize = tree.getSelectedNodes().size();
		tree.clearSelection();
		
		// Clear any text selection in the node that was being edited
		if ((selectionSize == 0) && (currentNode != tree.getEditingNode())) {
			OutlinerCellRendererImpl renderer = layout.getUIComponent(tree.getEditingNode());
			// [srk] bug trap
			// renderer being null has caused a crash here
			if (renderer != null) {
				renderer.setCaretPosition(0);
				renderer.moveCaretPosition(0);
			}
		}
		
		// Store the preferred caret position
		tree.getDocument().setPreferredCaretPosition(textArea.getCaretPosition());
		
		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(currentNode);
		tree.setCursorPosition(textArea.getCaretPosition());
		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		
		// Redraw only if there is a current selection
		if (selectionSize > 0) {
			 // This doesn't use redraw() becuase it's important to do a full draw if the node 
			 // is partially visible.
			tree.getDocument().panel.layout.draw(currentNode,OutlineLayoutManager.TEXT);
		}
		
		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);
 	}
 	
 	public void mouseReleased(MouseEvent e) {
  		// Catch for Solaris/Mac if they did the popup trigger.
 		if (e.isConsumed()) {
 			return;
 		}
 		
 		textArea = (OutlinerCellRendererImpl) e.getComponent();
 		
		// Shorthand
		Node currentNode = textArea.node;
 		JoeTree tree = currentNode.getTree();

		// Set the Mark
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(textArea.getCaretPosition(),false);

		// This is detection for Windows
		if (e.isPopupTrigger() && (currentNode.isAncestorSelected() || (tree.getEditingNode() == currentNode))) {
			Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			return;
		}
	}

 	public void mouseClicked(MouseEvent e) {
		// Catch for Solaris/Mac if they did the popup trigger.
		if (e.isConsumed()) {
 			return;
 		}
 		
 		textArea = (OutlinerCellRendererImpl) e.getComponent();
 		
		// Shorthand
		Node currentNode = textArea.node;
 		JoeTree tree = currentNode.getTree();
		
		// Set the Mark
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(textArea.getCaretPosition(),false);

		// Lets record changes to the selection state into the current undoable if it is an UndoableEdit
		UndoableEdit undoable = tree.getDocument().undoQueue.getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode)) {
			undoable.setNewPosition(textArea.getCaretPosition());
			undoable.setNewMarkPosition(textArea.getCaret().getMark());
		}	
 	}
	
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
 		textArea = (OutlinerCellRendererImpl) e.getComponent();
		
 		if (!textArea.hasFocus) {
 			return;
 		}
 
		//System.out.println("keyPressed");
		
		// Shorthand
		Node currentNode = textArea.node;
		JoeTree tree = currentNode.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				moveUp(tree, layout);
				break;

			case KeyEvent.VK_DOWN:
				moveDown(tree, layout);
				break;

			case KeyEvent.VK_LEFT:
				if (textArea.getCaretPosition() == 0) {
					moveLeftToPrevNode(tree, layout);
					e.consume();
				} else {
					moveLeft(tree, layout);
				}

				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(currentNode);

				return;

			case KeyEvent.VK_RIGHT:
				if (textArea.getCaretPosition() == textArea.getText().length()) {
					moveRightToNextNode(tree, layout);
					e.consume();
				} else {
					moveRight(tree, layout);
				}
				
				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(currentNode);

				return;
				
			default:
				// If we're read-only then abort
				if (!currentNode.isEditable()) {
					if (!e.isControlDown() && !e.isAltDown()) {
						Outliner.outliner.getToolkit().beep();
					}
				}
				return;
		}
		
		e.consume();
		return;
	}
	
	public void keyTyped(KeyEvent e) {
		if(e.paramString().indexOf("Backspace") != -1) {
			// Need to consume backspace here since the action we
			// added doesn't seem to supress the built-in backspace code.
			e.consume();
		}
	}
	
	public void keyReleased(KeyEvent e) {
 		textArea = (OutlinerCellRendererImpl) e.getComponent();

 		if (!textArea.hasFocus) {
 			return;
 		}
 
		int keyCode = e.getKeyCode();
		
		// Create some short names for convienence
		Node currentNode = textArea.node;
		JoeTree tree = currentNode.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		// If we're read-only then abort
		if (!currentNode.isEditable()) {
			return;
		}
		
		// Keep any meta keys from effecting undoability.
		if (e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown()) {
			return;
		}
		
		// Keep unwanted keystrokes from effecting undoability.
		boolean doUndo = true;
		switch(keyCode) {
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_CAPS_LOCK:
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_NUM_LOCK:
			case KeyEvent.VK_SCROLL_LOCK:
			case KeyEvent.VK_F1:
			case KeyEvent.VK_F2:
			case KeyEvent.VK_F3:
			case KeyEvent.VK_F4:
			case KeyEvent.VK_F5:
			case KeyEvent.VK_F6:
			case KeyEvent.VK_F7:
			case KeyEvent.VK_F8:
			case KeyEvent.VK_F9:
			case KeyEvent.VK_F10:
			case KeyEvent.VK_F11:
			case KeyEvent.VK_F12:
			case KeyEvent.VK_F13:
			case KeyEvent.VK_F14:
			case KeyEvent.VK_F15:
			case KeyEvent.VK_F16:
			case KeyEvent.VK_F17:
			case KeyEvent.VK_F18:
			case KeyEvent.VK_F19:
			case KeyEvent.VK_F20:
			case KeyEvent.VK_F21:
			case KeyEvent.VK_F22:
			case KeyEvent.VK_F23:
			case KeyEvent.VK_F24:
			case KeyEvent.VK_PRINTSCREEN:
			case KeyEvent.VK_INSERT:
			case KeyEvent.VK_HELP:
			case KeyEvent.VK_META:
				return;
				
			// These keystrokes should not effect undoablility, but do 
			// effect cursor position and redraw.
			case KeyEvent.VK_BACK_SPACE: // Undoability already recorded in keyTyped.
			case KeyEvent.VK_DELETE: // Undoability already recorded in keyTyped.
			case KeyEvent.VK_HOME:
			case KeyEvent.VK_END:
				doUndo = false;
				break;
		}

		// Record some Values
		int caretPosition = textArea.getCaretPosition();

		if (doUndo) {
			// Update the value in the node
			String oldText = currentNode.getValue();
			String newText = textArea.getText();
			currentNode.setValue(newText);
	
			// Put the Undoable onto the UndoQueue
			UndoableEdit undoable = tree.getDocument().undoQueue.getIfEdit();
			if ((undoable != null) && (undoable.getNode() == currentNode) && (!undoable.isFrozen())) {
				undoable.setNewText(newText);
				undoable.setNewPosition(caretPosition);
				undoable.setNewMarkPosition(caretPosition);
			} else {
				tree.getDocument().undoQueue.add(new UndoableEdit(currentNode, oldText, newText, tree.getCursorPosition(), caretPosition, tree.getCursorMarkPosition(), caretPosition));
			}
		}

		// Record the EditingNode, Mark and CursorPosition
		tree.setEditingNode(currentNode);
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(caretPosition, false);
		tree.getDocument().setPreferredCaretPosition(caretPosition);

		// Do the Redraw if we have wrapped or if we are currently off screen.
		if (textArea.getPreferredSize().height != textArea.height || !currentNode.isVisible()) {
			layout.draw(currentNode, OutlineLayoutManager.TEXT);
		}
	}


	// Key Handlers
	private void moveUp(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Get Prev Node
		Node prevNode = tree.getPrevNode(currentNode);
		if (prevNode == null) {
			return;
		}

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(prevNode);
		tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.getDocument().getPreferredCaretPosition(), prevNode));
			
		// Clear Text Selection
		textArea.setCaretPosition(0);
		textArea.moveCaretPosition(0);

		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);

		// Redraw and Set Focus
		if (prevNode.isVisible()) {
			layout.setFocus(prevNode,OutlineLayoutManager.TEXT);
		} else {
			layout.draw(prevNode,OutlineLayoutManager.TEXT);
		}
	}

	private void moveDown(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Get Prev Node
		Node nextNode = tree.getNextNode(currentNode);
		if (nextNode == null) {
			return;
		}

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(nextNode);
		tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.getDocument().getPreferredCaretPosition(), nextNode));
		
		// Clear Text Selection
		textArea.setCaretPosition(0);
		textArea.moveCaretPosition(0);

		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);

		// Redraw and Set Focus
		if (nextNode.isVisible()) {
			layout.setFocus(nextNode,OutlineLayoutManager.TEXT);
		} else {
			layout.draw(nextNode,OutlineLayoutManager.TEXT);
		}
	}

	private void moveLeft(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Update Preferred Caret Position
		tree.getDocument().setPreferredCaretPosition(textArea.getCaretPosition() - 1);

		// Record the CursorPosition only since the EditingNode should not have changed
		tree.setCursorPosition(textArea.getCaretPosition() - 1);

		// Redraw and Set Focus if this node is currently offscreen
		if (!currentNode.isVisible()) {
			layout.draw(currentNode,OutlineLayoutManager.TEXT);
		}
	}

	private void moveLeftToPrevNode(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

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
	}

	private void moveRight(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Update Preferred Caret Position
		tree.getDocument().setPreferredCaretPosition(textArea.getCaretPosition() + 1);

		// Record the CursorPosition only since the EditingNode should not have changed
		tree.setCursorPosition(textArea.getCaretPosition() + 1);

		// Redraw and Set Focus if this node is currently offscreen
		if (!currentNode.isVisible()) {
			layout.draw(currentNode,OutlineLayoutManager.TEXT);
		}
	}

	private void moveRightToNextNode(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Get Prev Node
		Node nextNode = tree.getNextNode(currentNode);
		if (nextNode == null) {
			tree.setCursorPosition(tree.getCursorPosition()); // Makes sure we reset the mark
			return;
		}
	
		// Update Preferred Caret Position
		int newLength = nextNode.getValue().length();
		tree.getDocument().setPreferredCaretPosition(newLength);

		// Record the EditingNode and CursorPosition
		tree.setEditingNode(nextNode);
		tree.setCursorPosition(0);

		// Clear Text Selection
		textArea.setCaretPosition(0);
		textArea.moveCaretPosition(0);

		// Redraw and Set Focus
		if (nextNode.isVisible()) {
			layout.setFocus(nextNode,OutlineLayoutManager.TEXT);
		} else {
			layout.draw(nextNode,OutlineLayoutManager.TEXT);
		}
	}

	// Additional Outline Methods
	public static void hoist(Node currentNode) {
		currentNode.getTree().getDocument().hoistStack.hoist(new HoistStackItem(currentNode));
		return;
	}

	public static void dehoist(Node currentNode) {
		currentNode.getTree().getDocument().hoistStack.dehoist();
		return;
	}

	public static void dehoist_all(Node currentNode) {
		currentNode.getTree().getDocument().hoistStack.dehoistAll();
		return;
	}

	public static void expandAllSubheads(Node currentNode) {
		currentNode.ExpandAllSubheads();
		currentNode.getTree().getDocument().panel.layout.redraw();
		return;
	}

	public static void expandEverything(JoeTree tree) {
		tree.getRootNode().ExpandAllSubheads();
		tree.getDocument().panel.layout.redraw();
		return;
	}

	public static void collapseToParent(Node currentNode) {
		// Shorthand
		JoeTree tree = currentNode.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;
		
		Node parent = currentNode.getParent();
		if (parent.isRoot()) {
			// Collapse
			currentNode.CollapseAllSubheads();
		
			// Redraw and Set Focus
			layout.draw(currentNode,OutlineLayoutManager.ICON);
		} else {
			// Collapse
			parent.CollapseAllSubheads();
			
			// Record the EditingNode, Mark and CursorPosition
			tree.setEditingNode(parent);
			tree.setComponentFocus(OutlineLayoutManager.ICON);
	
			// Update Selection
			tree.setSelectedNodesParent(parent.getParent());
			tree.addNodeToSelection(parent);
			
			// Redraw and Set Focus
			layout.draw(parent,OutlineLayoutManager.ICON);
		}
		return;
	}

	public static void collapseEverything(JoeTree tree) {
		int limit = tree.getRootNode().numOfChildren();
		for (int i = 0; i < limit; i++) {
			tree.getRootNode().getChild(i).CollapseAllSubheads();
		}

		// Record the EditingNode, Mark and CursorPosition
		Node firstNode = tree.getRootNode().getFirstChild();
		tree.setEditingNode(firstNode);
		tree.setComponentFocus(OutlineLayoutManager.ICON);

		// Update Selection
		tree.setSelectedNodesParent(tree.getRootNode());
		tree.addNodeToSelection(firstNode);
		
		// Redraw and Set Focus
		tree.getDocument().panel.layout.draw(firstNode,OutlineLayoutManager.ICON);
		
		return;
	}
}