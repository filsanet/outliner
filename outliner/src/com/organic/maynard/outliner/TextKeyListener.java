/**
 * Copyright (C) 2000 Maynard Demmon, maynard@organic.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
package com.organic.maynard.outliner;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

public class TextKeyListener implements KeyListener, MouseListener {
	
	private OutlinerCellRendererImpl textArea = null;
		
	// The Constructors
	public TextKeyListener(OutlinerCellRendererImpl textArea) {
		this.textArea = textArea;
	}

	public void destroy() {
		textArea = null;
	}

	// MouseListener Interface
 	public void mouseEntered(MouseEvent e) {
		//System.out.println("TEXT Mouse Entered: " + e.paramString()); 	
 	}
 	
 	public void mouseExited(MouseEvent e) {
		//System.out.println("TEXT Mouse Exited: " + e.paramString()); 	
 	
 	}
 	
 	public void mousePressed(MouseEvent e) {
		//System.out.println("TEXT Mouse Pressed: " + e.paramString()); 	
 		//System.out.println("Pressed: " + textArea.node.getValue() + " " + e.paramString());
 		
 		// Shorthand
 		Node currentNode = textArea.node;
 		TreeContext tree = currentNode.getTree();
 		outlineLayoutManager layout = tree.doc.panel.layout;

		// This is detection for Solaris
		if (e.isPopupTrigger()) {
			if (textArea.node.isSelected()) {
				Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			} else if (tree.getEditingNode() == textArea.node) {
				Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	
 		// Clear the selection
 		int selectionSize = tree.selectedNodes.size();
		tree.clearSelection();
		
		// Clear any text selection in the node that was being edited
		if ((selectionSize == 0) && (currentNode != tree.getEditingNode())) {
			OutlinerCellRendererImpl renderer = layout.getUIComponent(tree.getEditingNode());
			renderer.setCaretPosition(0);
			renderer.moveCaretPosition(0);
		}
		
		// Store the preferred caret position
		tree.doc.setPreferredCaretPosition(textArea.getCaretPosition());
		
		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(currentNode);
		tree.setCursorPosition(textArea.getCaretPosition());
		tree.setComponentFocus(outlineLayoutManager.TEXT);
		
		// Redraw only if there is a current selection
		if (selectionSize > 0) {
			tree.doc.panel.layout.draw(currentNode,outlineLayoutManager.TEXT);
		}
		
		// Freeze Undo Editing
		UndoableEdit.freezeUndoEdit(currentNode);
 	}
 	
 	public void mouseReleased(MouseEvent e) {
		//System.out.println("TEXT Mouse Released: " + e.paramString()); 	
 		//System.out.println("Released: " + textArea.node.getValue() + " " + e.paramString());
		
		// Shorthand
		Node currentNode = textArea.node;
 		TreeContext tree = currentNode.getTree();

		// Set the Mark
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(textArea.getCaretPosition(),false);

		// Lets record changes to the selection state into the current undoable if it is an UndoableEdit
		UndoableEdit undoable = tree.doc.undoQueue.getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode)) {
			undoable.setNewPosition(textArea.getCaretPosition());
			undoable.setNewMarkPosition(textArea.getCaret().getMark());
		}

		// This is detection for Windows
		if (e.isPopupTrigger()) {
			if (textArea.node.isSelected()) {
				Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			} else if (tree.getEditingNode() == textArea.node) {
				Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
			}
		}
	}

 	public void mouseClicked(MouseEvent e) {
 		//System.out.println("Clicked: " + textArea.node.getValue() + " " + e.paramString());
 	}
	
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		//System.out.println("TEXTAREA KEY PRESSED: " + e.paramString());
		
		// Shorthand
		TreeContext tree = textArea.node.getTree();
		Node currentNode = textArea.node;
		outlineLayoutManager layout = textArea.node.getTree().doc.panel.layout;

		if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
			if (currentNode.isExpanded()) {
				currentNode.setExpanded(false);
			} else {
				currentNode.setExpanded(true);
			}

			// Redraw
			layout.draw();
			layout.setFocus(currentNode, outlineLayoutManager.TEXT);

			e.consume();
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			// Get Prev Node
			Node prevNode = tree.getPrevNode(currentNode);
			if (prevNode == null) {return;}

			// Record the EditingNode and CursorPosition
			tree.setEditingNode(prevNode);
			tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.doc.getPreferredCaretPosition(), prevNode));
				
			// Clear Text Selection
			textArea.setCaretPosition(0);
			textArea.moveCaretPosition(0);

			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(currentNode);

			// Redraw and Set Focus
			if (prevNode.isVisible()) {
				layout.setFocus(prevNode,outlineLayoutManager.TEXT);
			} else {
				layout.draw(prevNode,outlineLayoutManager.TEXT);
			}
			
			e.consume();
			return;			
		}

		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			// Get Prev Node
			Node nextNode = tree.getNextNode(currentNode);
			if (nextNode == null) {return;}

			// Record the EditingNode and CursorPosition
			tree.setEditingNode(nextNode);
			tree.setCursorPosition(OutlinerDocument.findNearestCaretPosition(textArea.getCaretPosition(), tree.doc.getPreferredCaretPosition(), nextNode));
			
			// Clear Text Selection
			textArea.setCaretPosition(0);
			textArea.moveCaretPosition(0);

			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(currentNode);

			// Redraw and Set Focus
			if (nextNode.isVisible()) {
				layout.setFocus(nextNode,outlineLayoutManager.TEXT);
			} else {
				layout.draw(nextNode,outlineLayoutManager.TEXT);
			}

			e.consume();
			return;			
		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (textArea.getCaretPosition() == 0) {
				// Get Prev Node
				Node prevNode = tree.getPrevNode(currentNode);
				if (prevNode == null) {
					tree.setCursorPosition(tree.getCursorPosition()); // Makes sure we reset the mark
					return;
				}
				
				// Update Preferred Caret Position
				int newLength = prevNode.getValue().length();
				tree.doc.setPreferredCaretPosition(newLength);

				// Record the EditingNode and CursorPosition
				tree.setEditingNode(prevNode);
				tree.setCursorPosition(newLength);

				// Clear Text Selection
				textArea.setCaretPosition(0);
				textArea.moveCaretPosition(0);

				// Redraw and Set Focus
				if (prevNode.isVisible()) {
					layout.setFocus(prevNode,outlineLayoutManager.TEXT);
				} else {
					layout.draw(prevNode,outlineLayoutManager.TEXT);
				}
				
				e.consume();
			} else {
				// Update Preferred Caret Position
				tree.doc.setPreferredCaretPosition(textArea.getCaretPosition() - 1);

				// Record the CursorPosition only since the EditingNode should not have changed
				tree.setCursorPosition(textArea.getCaretPosition() - 1);

				// Redraw and Set Focus if this node is currently offscreen
				if (!currentNode.isVisible()) {
					layout.draw(currentNode,outlineLayoutManager.TEXT);
				}		
			}

			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(currentNode);
			
			return;			
		}

		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (textArea.getCaretPosition() == textArea.getText().length()) {
				// Get Prev Node
				Node nextNode = tree.getNextNode(currentNode);
				if (nextNode == null) {
					tree.setCursorPosition(tree.getCursorPosition()); // Makes sure we reset the mark
					return;
				}
			
				// Update Preferred Caret Position
				int newLength = nextNode.getValue().length();
				tree.doc.setPreferredCaretPosition(newLength);

				// Record the EditingNode and CursorPosition
				tree.setEditingNode(nextNode);
				tree.setCursorPosition(0);

				// Clear Text Selection
				textArea.setCaretPosition(0);
				textArea.moveCaretPosition(0);

				// Redraw and Set Focus
				if (nextNode.isVisible()) {
					layout.setFocus(nextNode,outlineLayoutManager.TEXT);
				} else {
					layout.draw(nextNode,outlineLayoutManager.TEXT);
				}

				e.consume();
			} else {			
				// Update Preferred Caret Position
				tree.doc.setPreferredCaretPosition(textArea.getCaretPosition() + 1);

				// Record the CursorPosition only since the EditingNode should not have changed
				tree.setCursorPosition(textArea.getCaretPosition() + 1);

				// Redraw and Set Focus if this node is currently offscreen
				if (!currentNode.isVisible()) {
					layout.draw(currentNode,outlineLayoutManager.TEXT);
				}
			}

			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(currentNode);

			return;			
		}

		if (e.getKeyChar() == KeyEvent.VK_ENTER) {			
			if (e.isShiftDown()) {
				tree.setSelectedNodesParent(currentNode.getParent());
				tree.addNodeToSelection(currentNode);

				// Record the EditingNode and CursorPosition
				tree.setComponentFocus(outlineLayoutManager.ICON);

				// Redraw and Set Focus
				layout.draw(currentNode,outlineLayoutManager.ICON);
			} else {
				// Create a new node and insert it as a sibling immediatly after this node, unless
				// the current node is expanded and has children. Then, we should insert it as the first child of the
				// current node.
				Node newNode = new NodeImpl(currentNode.getTree(),"");
				
				if ((!currentNode.isLeaf()) && (currentNode.isExpanded())) {
					newNode.setDepth(currentNode.getDepth() + 1);
					currentNode.insertChild(newNode,0);				
				} else {
					newNode.setDepth(currentNode.getDepth());
					currentNode.getParent().insertChild(newNode,currentNode.currentIndex() + 1);
				}
				
				tree.insertNode(newNode);

				// Record the EditingNode and CursorPosition
				tree.setEditingNode(newNode);
				tree.setCursorPosition(0);

				// Update Preferred Caret Position
				tree.doc.setPreferredCaretPosition(0);

				// Put the Undoable onto the UndoQueue
				CompoundUndoableInsert undoable = new CompoundUndoableInsert(currentNode.getParent());
				undoable.addPrimitive(new PrimitiveUndoableInsert(newNode.getParent(),newNode,newNode.currentIndex()));
				tree.doc.undoQueue.add(undoable);

				// Redraw and Set Focus
				layout.draw(newNode,outlineLayoutManager.TEXT);
			}
			
			e.consume();
			return;
		}

		if (e.getKeyChar() == KeyEvent.VK_TAB) {
			if (e.isShiftDown()) {
				// Put the Undoable onto the UndoQueue
				Node targetNode = currentNode.getParent().getParent();
				int targetIndex = currentNode.getParent().currentIndex() + 1;
				if (currentNode.getParent().isRoot()) {
					// Our parent is root. Since we can't be promoted to root level, Abort.
					e.consume();
					return;
				}
				
				CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
				tree.doc.undoQueue.add(undoable);

				// Record the Insert in the undoable
				int index = currentNode.currentIndex();
				undoable.addPrimitive(new PrimitiveUndoableMove(undoable,currentNode,index,targetIndex));

				tree.promoteNode(currentNode);
			} else {		
				if (currentNode.isFirstChild()) {
					e.consume();
					return;
				}
				// Put the Undoable onto the UndoQueue
				Node targetNode = currentNode.prevSibling();

				CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
				tree.doc.undoQueue.add(undoable);
				
				// Record the Insert in the undoable
				int index = currentNode.currentIndex();
				int targetIndex = targetNode.numOfChildren();
				undoable.addPrimitive(new PrimitiveUndoableMove(undoable,currentNode,index,targetIndex));

				tree.demoteNode(currentNode,targetNode);
			}

			// Redraw and Set Focus
			layout.draw(currentNode,outlineLayoutManager.TEXT);
			
			e.consume();
			return;
		}

		if ((e.getKeyCode() == KeyEvent.VK_V) && e.isControlDown()) {
			inlinePaste = true;
			
			// Get the text from the clipboard and turn it into a tree
			StringSelection selection = (StringSelection) Outliner.clipboard.getContents(this);
			String text = "";
			try {
				text = (String) selection.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
			
			}
			
			// Need to make a check for inline pastes
			if ((text.indexOf(Preferences.LINE_END_STRING) == -1) && (text.indexOf(Preferences.DEPTH_PAD_STRING) == -1)) {
				return;
			} else {
				inlinePaste = false;
			}

			// Put the Undoable onto the UndoQueue
			CompoundUndoableInsert undoable = new CompoundUndoableInsert(currentNode.getParent());
			tree.doc.undoQueue.add(undoable);
			
			Node tempRoot = PadSelection.pad(text, tree, currentNode.getDepth(),Preferences.LINE_END_UNIX);
			
			Node parentForNewNode = currentNode.getParent();
			int indexForNewNode = parentForNewNode.getChildIndex(currentNode);
			for (int i = tempRoot.numOfChildren() - 1; i >= 0; i--) {
				Node node = tempRoot.getChild(i);
				parentForNewNode.insertChild(node, indexForNewNode + 1);
				tree.insertNode(node);

				// Record the Insert in the undoable
				int index = node.currentIndex() + i;
				undoable.addPrimitive(new PrimitiveUndoableInsert(parentForNewNode,node,index));
			}
			
			tree.setSelectedNodesParent(parentForNewNode);

			for (int i = tempRoot.numOfChildren() - 1; i >= 0; i--) {
				Node node = tempRoot.getChild(i);
				tree.addNodeToSelection(node);
			}
			
			Node nodeThatMustBeVisible = tree.getYoungestInSelection();

			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(nodeThatMustBeVisible);
			tree.setCursorPosition(0);
			tree.setComponentFocus(outlineLayoutManager.ICON);

			// Redraw and Set Focus
			layout.draw(nodeThatMustBeVisible,outlineLayoutManager.ICON);
			
			e.consume();
			return;
		}
	}
	
	public void keyTyped(KeyEvent e) { 
		//System.out.println("KeyTyped: " + e.paramString());
	}
	
	private boolean inlinePaste = true;
	
	public void keyReleased(KeyEvent e) {
		// Catch any unwanted chars that slip through
		// Numbers are used for Control, Shift, VK_PAGE_UP, VK_PAGE_DOWN, VK_UP, VK_DOWN, VK_LEFT, VK_RIGHT since
		// the characters don't seem to work here for some reason.
		
		// Let control-x and control-v slip through
		if (e.isControlDown()) {
			if ((e.getKeyCode() == KeyEvent.VK_X) || ((e.getKeyCode() == KeyEvent.VK_V) && inlinePaste)) {
				// Do Nothing
			} else {
				return;
			}
		}
		
		if (e.isControlDown() && e.isShiftDown()) {
			return;
		}
		
		if ((e.getKeyCode() == 16) ||
			(e.getKeyCode() == 17) ||
			(e.getKeyCode() == 33) ||
			(e.getKeyCode() == 34) ||
			(e.getKeyCode() == 37) ||
			(e.getKeyCode() == 38) ||
			(e.getKeyCode() == 39) ||
			(e.getKeyCode() == 40) ||
			(e.getKeyChar() == KeyEvent.VK_TAB) ||
			(e.getKeyChar() == KeyEvent.VK_ENTER)) {
			return;
		}

		// All other keys
		//System.out.println("KeyReleased: " + e.paramString());

		// Create some short names for convienence
		TreeContext tree = textArea.node.getTree();
		Node currentNode = textArea.node;
		outlineLayoutManager layout = textArea.node.getTree().doc.panel.layout;

		// Put the Undoable onto the UndoQueue
		UndoableEdit undoable = tree.doc.undoQueue.getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode) && (!undoable.isFrozen())) {
			if (e.isControlDown() && ((e.getKeyCode() == KeyEvent.VK_X) || (e.getKeyCode() == KeyEvent.VK_V))) {
				tree.doc.undoQueue.add(new UndoableEdit(currentNode,currentNode.getValue(),textArea.getText(),tree.getCursorPosition(),textArea.getCaretPosition(),tree.getCursorMarkPosition(),textArea.getCaretPosition()));
			} else {
				undoable.setNewText(textArea.getText());
				undoable.setNewPosition(textArea.getCaretPosition());
				undoable.setNewMarkPosition(textArea.getCaretPosition());
			}
		} else {
			tree.doc.undoQueue.add(new UndoableEdit(currentNode,currentNode.getValue(),textArea.getText(),tree.getCursorPosition(),textArea.getCaretPosition(),tree.getCursorMarkPosition(),textArea.getCaretPosition()));
		}
			
		// Update the value in the node
		currentNode.setValue(textArea.getText());

		// Record the EditingNode, Mark and CursorPosition
		tree.setEditingNode(currentNode);
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(textArea.getCaretPosition(),false);

		// Do the Redraw if we have wrapped.
		if (!textArea.getPreferredSize().equals(textArea.getCurrentTextAreaSize())) {
			textArea.setCurrentTextAreaSize(textArea.getPreferredSize());
			layout.draw(currentNode,outlineLayoutManager.TEXT);
		} else if (!currentNode.isVisible()) {
			layout.draw(currentNode,outlineLayoutManager.TEXT);
		}
	}

	
	// Additional Outline Methods
	public static void expandAllSubheads(Node currentNode) {
		currentNode.ExpandAllSubheads();
		currentNode.getTree().doc.panel.layout.draw();
		return;
	}

	public static void expandEverything(TreeContext tree) {
		tree.rootNode.ExpandAllSubheads();
		tree.doc.panel.layout.draw();
		return;
	}

	public static void collapseToParent(Node currentNode) {
		// Shorthand
		TreeContext tree = currentNode.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;
		
		Node parent = currentNode.getParent();
		if (parent.isRoot()) {
			// Collapse
			currentNode.CollapseAllSubheads();
		
			// Redraw and Set Focus
			layout.draw(currentNode,outlineLayoutManager.ICON);
		} else {
			// Collapse
			parent.CollapseAllSubheads();
			
			// Record the EditingNode, Mark and CursorPosition
			tree.setEditingNode(parent);
			tree.setComponentFocus(outlineLayoutManager.ICON);
	
			// Update Selection
			tree.setSelectedNodesParent(parent.getParent());
			tree.addNodeToSelection(parent);
			
			// Redraw and Set Focus
			layout.draw(parent,outlineLayoutManager.ICON);
		}
		return;
	}

	public static void collapseEverything(TreeContext tree) {
		for (int i = 0; i < tree.rootNode.numOfChildren(); i++) {
			((Node) tree.rootNode.getChild(i)).CollapseAllSubheads();
		}

		// Record the EditingNode, Mark and CursorPosition
		Node firstNode = tree.rootNode.getFirstChild();
		tree.setEditingNode(firstNode);
		tree.setComponentFocus(outlineLayoutManager.ICON);

		// Update Selection
		tree.setSelectedNodesParent(tree.rootNode);
		tree.addNodeToSelection(firstNode);
		
		// Redraw and Set Focus
		tree.doc.panel.layout.draw(firstNode,outlineLayoutManager.ICON);
		
		return;
	}

}