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
	public void keyPressed(KeyEvent e) {
  		recordRenderer(e.getComponent());
			
  		if (!textArea.hasFocus) {
 			return;
 		}
 		
		// Create some short names for convienence
		JoeTree tree = textArea.node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		switch(e.getKeyCode()) {
				
			case KeyEvent.VK_UP:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						deselect(tree, layout, UP);
					} else {
						moveUp(tree,layout);
					}
				} else if (e.isShiftDown()) {
					select(tree, layout, UP);
				} else {
					navigate(tree, layout, UP);
				}
				break;

			case KeyEvent.VK_DOWN:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						deselect(tree, layout, DOWN);
					} else {
						moveDown(tree,layout);
					}
				} else if (e.isShiftDown()) {
					select(tree, layout, DOWN);
				} else {
					navigate(tree,layout, DOWN);
				}
				break;

			case KeyEvent.VK_LEFT:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						deselect(tree, layout, LEFT);
					} else {
						moveLeft(tree,layout);
					}
				} else if (e.isShiftDown()) {
					select(tree, layout, LEFT);
				} else {
					navigate(tree,layout, LEFT);
				}
				break;

			case KeyEvent.VK_RIGHT:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						deselect(tree, layout, RIGHT);
					} else {
						moveRight(tree,layout);
					}
				} else if (e.isShiftDown()) {
					select(tree, layout, RIGHT);
				} else {
					navigate(tree,layout, RIGHT);
				}
				break;

			default:
				return;
		}
		
		e.consume();
		return;
	}
	
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



	// Key Handlers
	private void moveUp(JoeTree tree, OutlineLayoutManager layout) {
		Node youngestNode = tree.getYoungestInSelection();
		Node node = youngestNode.prevSibling();
		if (node == youngestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(), node.getParent());
		int targetIndex = node.currentIndex();
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			targetIndex++;
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
	private void moveDown(JoeTree tree, OutlineLayoutManager layout) {
		Node oldestNode = tree.getOldestInSelection();
		Node node = oldestNode.nextSibling();
		if (node == oldestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(), node.getParent());
		int targetIndex = node.currentIndex();
		
		// Do the move
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			targetIndex--;
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}

	private void moveLeft(JoeTree tree, OutlineLayoutManager layout) {
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
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}		
	}


	private void moveRight(JoeTree tree, OutlineLayoutManager layout) {
		Node oldestNode = tree.getOldestInSelection();
		Node node = tree.getNextNode(oldestNode.getLastViewableDecendent());
		if (node == null) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable;
		int targetIndex = -1;
		if ((!node.isLeaf() && node.isExpanded())) {
			undoable = new CompoundUndoableMove(oldestNode.getParent(),node);
			targetIndex = 0;
		} else if (tree.getSelectedNodesParent() != node.getParent()) {
			undoable = new CompoundUndoableMove(oldestNode.getParent(),node.getParent());
			targetIndex = node.currentIndex() + 1;
		} else {
			undoable = new CompoundUndoableMove(oldestNode.getParent(),node.getParent());
			targetIndex = node.currentIndex();
		}
	
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));

			if ((!node.isLeaf() && node.isExpanded()) || (nodeToMove.getParent() != node.getParent())) {
				// Do Nothing.
			} else {
				targetIndex--;
			}
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
	private static final int UP = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	private static final int RIGHT = 4;

	private void deselect(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.prevSelectedSibling();
				if ((node == null) || (node == oldestNode)) {return;}
				tree.removeNodeFromSelection(oldestNode);
				break;

			case DOWN:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.nextSelectedSibling();
				if ((node == null) || (node == youngestNode)) {return;}
				tree.removeNodeFromSelection(youngestNode);
				break;

			case LEFT:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.prevSibling();
				if ((node == null) || (node == oldestNode)) {return;}
				tree.removeNodeFromSelection(oldestNode);
				tree.addNodeToSelection(node);
				break;

			case RIGHT:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.nextSibling();
				if ((node == null) || (node == youngestNode)) {return;}
				tree.removeNodeFromSelection(youngestNode);
				tree.addNodeToSelection(node);
				break;
				
			default:
				return;
		}

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);		
	}
	
	private void select(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				break;

			case DOWN:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				break;

			case LEFT:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				tree.removeNodeFromSelection(youngestNode);
				break;

			case RIGHT:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				tree.removeNodeFromSelection(oldestNode);
				break;
				
			default:
				return;
		}
		
		tree.addNodeToSelection(node);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);		
	}
		
	private void navigate(JoeTree tree, OutlineLayoutManager layout, int type) {
		Node node = null;
		Node youngestNode = null;
		Node oldestNode = null;
		
		switch(type) {
			case UP:
				youngestNode = tree.getYoungestInSelection();
				node = youngestNode.prevSibling();
				if (node == youngestNode) {return;}
				tree.clearSelection();
				break;

			case DOWN:
				oldestNode = tree.getOldestInSelection();
				node = oldestNode.nextSibling();
				if (node == oldestNode) {return;}
				tree.clearSelection();
				break;

			case LEFT:
				youngestNode = tree.getYoungestInSelection();
				node = tree.getPrevNode(youngestNode);
				if (node == null) {return;}
				tree.setSelectedNodesParent(node.getParent());
				break;

			case RIGHT:
				oldestNode = tree.getOldestInSelection();
				node = tree.getNextNode(oldestNode);
				if (node == null) {return;}
				tree.setSelectedNodesParent(node.getParent());
				break;
				
			default:
				return;
		}
		
		tree.addNodeToSelection(node);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(node);

		// Redraw and Set Focus
		layout.draw(node,OutlineLayoutManager.ICON);
	}


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