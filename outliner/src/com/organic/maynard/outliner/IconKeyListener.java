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

public class IconKeyListener implements KeyListener, MouseListener {

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
 		//JoeTree tree = textArea.node.getTree();
		tree.setEditingNode(textArea.node);
		tree.setComponentFocus(OutlineLayoutManager.ICON);
		
		// Store the node since it may get lost by the time we want to throw the new mouse event.
		//Node node = textArea.node;
		
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
	
		// Create some short names for convienence
		JoeTree tree = textArea.node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		switch(e.getKeyCode()) {
			case KeyEvent.VK_PAGE_DOWN:
				toggleExpansion(tree,layout, e.isShiftDown());
				break;

			case KeyEvent.VK_PAGE_UP:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearComment(tree, layout);
					} else {
						toggleCommentInheritance(tree,layout);
					}
				} else if (e.isShiftDown()) {
					toggleComment(tree,layout);
				} else {
					toggleCommentAndClear(tree, layout);
				}
				break;

			case KeyEvent.VK_F11:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearEditable(tree, layout);
					} else {
						toggleEditableInheritance(tree,layout);
					}
				} else if (e.isShiftDown()) {
					toggleEditable(tree,layout);
				} else {
					toggleEditableAndClear(tree, layout);
				}
				break;

			case KeyEvent.VK_F12:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						clearMoveable(tree, layout);
					} else {
						toggleMoveableInheritance(tree,layout);
					}
				} else if (e.isShiftDown()) {
					toggleMoveable(tree,layout);
				} else {
					toggleMoveableAndClear(tree, layout);
				}
				break;			
			case KeyEvent.VK_DELETE:
				delete(tree,layout,true);
				break;

			case KeyEvent.VK_BACK_SPACE:
				delete(tree,layout,false);
				break;
				
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

			case KeyEvent.VK_ENTER:
				if (e.isShiftDown()) {
					insertAbove(tree,layout);								
				} else {
					insert(tree,layout);				
				}
				break;

			case KeyEvent.VK_INSERT:
				if (e.isShiftDown()) {
					changeToParent(tree, layout);
				} else {
					changeFocusToTextArea(tree, layout, POSITION_CURRENT);
				}
				break;

			case KeyEvent.VK_HOME:
				if (tree.getSelectedNodes().size() > 1) {
					changeSelectionToNode(tree, layout, POSITION_FIRST);
				} else {
					changeFocusToTextArea(tree, layout, POSITION_FIRST);
				}
				break;

			case KeyEvent.VK_END:
				if (tree.getSelectedNodes().size() > 1) {
					changeSelectionToNode(tree, layout, POSITION_LAST);
				} else {
					changeFocusToTextArea(tree, layout, POSITION_LAST);
				}
				break;

			case KeyEvent.VK_TAB:
				if (e.isShiftDown()) {
					promote(tree,layout);
				} else {
					demote(tree,layout);
				}
				break;

			case KeyEvent.VK_C:
				if (e.isControlDown()) {
					copy(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_X:
				if (e.isControlDown()) {
					cut(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_V:
				if (e.isControlDown()) {
					paste(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_I:
				if (e.isControlDown()) {
					selectInverse(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_A:
				if (e.isControlDown() && !e.isShiftDown()) {
					selectAll(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_D:
				if (e.isControlDown() && !e.isShiftDown()) {
					selectNone(tree,layout);
					break;
				} else {
					return;
				}

			case KeyEvent.VK_M:
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						merge(tree,layout,true);
					} else {
						merge(tree,layout,false);
					}
					break;
				} else {
					return;
				}

			default:
				return;
		}
		
		e.consume();
		return;
	}
	
	public void keyTyped(KeyEvent e) {
		recordRenderer(e.getComponent());

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
	private void toggleExpansion(JoeTree tree, OutlineLayoutManager layout, boolean shiftDown) {
		Node currentNode = textArea.node;
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);
			if (node.isExpanded()) {
				node.setExpanded(false, !shiftDown);
			} else {
				if (shiftDown) {
					node.ExpandAllSubheads();
				} else {
					node.setExpanded(true, true);
				}
			}
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	// Comments
	private void clearComment(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearCommentForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void clearCommentForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		
		if (oldValue != Node.COMMENT_INHERITED) {
			node.setCommentState(Node.COMMENT_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearCommentForSingleNode(node.getChild(i), undoable);
		}
	}
	
	private void toggleCommentAndClear(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleCommentAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleCommentAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleCommentForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearCommentForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	private void toggleComment(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleCommentForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleCommentForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		boolean isComment = node.isComment();
		
		if (oldValue == Node.COMMENT_FALSE) {
			node.setCommentState(Node.COMMENT_TRUE);
			newValue = Node.COMMENT_TRUE;
					
		} else if (oldValue == Node.COMMENT_TRUE) {
			node.setCommentState(Node.COMMENT_FALSE);
			newValue = Node.COMMENT_FALSE;
		
		} else {
			if (isComment) {
				node.setCommentState(Node.COMMENT_FALSE);
				newValue = Node.COMMENT_FALSE;
			} else {
				node.setCommentState(Node.COMMENT_TRUE);
				newValue = Node.COMMENT_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
	}
	
	private void toggleCommentInheritance(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0; i < nodeList.size(); i++) {
			toggleCommentInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	protected static void toggleCommentInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getCommentState();
		int newValue = Node.COMMENT_INHERITED;
		boolean isComment = node.isComment();
		
		if (oldValue == Node.COMMENT_INHERITED) {
			if (isComment) {
				node.setCommentState(Node.COMMENT_TRUE);
				newValue = Node.COMMENT_TRUE;
			} else {
				node.setCommentState(Node.COMMENT_FALSE);
				newValue = Node.COMMENT_FALSE;
			}
								
		} else {
			node.setCommentState(Node.COMMENT_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableCommentChange(node, oldValue, newValue));
	}
	
	private void changeFocusToTextArea(JoeTree tree, OutlineLayoutManager layout, int positionType) {
		Node currentNode = textArea.node;
		
		if (positionType == POSITION_FIRST) {
			tree.setCursorPosition(0);
			tree.getDocument().setPreferredCaretPosition(0);
		} else if (positionType == POSITION_LAST) {
			int index = textArea.getText().length();
			tree.setCursorPosition(index);
			tree.getDocument().setPreferredCaretPosition(index);		
		}
		
		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		tree.clearSelection();
		layout.draw(currentNode,OutlineLayoutManager.TEXT);
	}

	private void changeSelectionToNode(JoeTree tree, OutlineLayoutManager layout, int positionType) {
		Node selectedNode = null;
		
		if (positionType == POSITION_FIRST) {
			selectedNode = tree.getYoungestInSelection();
		} else if (positionType == POSITION_LAST) {
			selectedNode = tree.getOldestInSelection();
		}
		
		// Update Selection
		tree.clearSelection();
		tree.addNodeToSelection(selectedNode);

		// Record State
		tree.setEditingNode(selectedNode);
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);
		
		// Redraw and Set Focus	
		layout.draw(selectedNode, OutlineLayoutManager.ICON);
	}

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

	private void insertAbove(JoeTree tree, OutlineLayoutManager layout) {
		Node node = tree.getOldestInSelection();

		// Abort if node is not editable
		if (!node.isEditable()) {
			return;
		}
		
		tree.clearSelection();
		
		TextKeyListener.doInsertAbove(node, tree, layout);
	}

	private void insert(JoeTree tree, OutlineLayoutManager layout) {
		Node node = tree.getOldestInSelection();

		// Abort if node is not editable
		if (!node.isEditable()) {
			return;
		}
		
		tree.clearSelection();

		Node newNode = new NodeImpl(tree,"");
		int newNodeIndex = node.currentIndex() + 1;
		Node newNodeParent = node.getParent();

		newNode.setDepth(node.getDepth());
		newNodeParent.insertChild(newNode, newNodeIndex);

		//int visibleIndex = tree.insertNodeAfter(node, newNode);
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(newNodeParent);
		undoable.addPrimitive(new PrimitiveUndoableInsert(newNodeParent, newNode, newNodeIndex));
		tree.getDocument().undoQueue.add(undoable);
		
		// Redraw and Set Focus
		layout.draw(newNode, OutlineLayoutManager.TEXT);	
	}

	private void promote(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		Node parent = currentNode.getParent();
		
		if (parent.isRoot()) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		Node targetNode = parent.getParent();
		int targetIndex = parent.currentIndex() + 1;
		
		CompoundUndoableMove undoable = new CompoundUndoableMove(parent, targetNode);

		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}

	private void demote(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		if (tree.getYoungestInSelection().isFirstChild()) {
			return;
		}
	
		// Put the Undoable onto the UndoQueue
		Node targetNode = tree.getYoungestInSelection().prevSibling();

		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
		
		int existingChildren = targetNode.numOfChildren();
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = nodeList.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = nodeList.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}

			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), existingChildren));
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
	}

	private void copy(JoeTree tree, OutlineLayoutManager layout) {
		NodeSet nodeSet = new NodeSet();
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i).cloneClean();
			node.setDepthRecursively(0);
			nodeSet.addNode(node);
		}
		
		// [md] This conditional is here since StringSelection subclassing seems to be broken in Java 1.3.1.
		if (PlatformCompatibility.isJava1_3_1()) {
			java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(nodeSet.toString()), null);
		} else {
			Outliner.clipboard.setContents(new NodeSetTransferable(nodeSet), Outliner.outliner);
		}
	}

	private void cut(JoeTree tree, OutlineLayoutManager layout) {
		NodeSet nodeSet = new NodeSet();
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);
			
			// Abort if node is not editable
			if (!node.isEditable()) {
				continue;
			}
			
			Node newNode = node.cloneClean();
			newNode.setDepthRecursively(0);	
			nodeSet.addNode(newNode);
		}
		
		if (!nodeSet.isEmpty()) {
			// [md] This conditional is here since StringSelection subclassing seems to be broken in Java 1.3.1.
			if (PlatformCompatibility.isJava1_3_1()) {
				java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(nodeSet.toString()), null);
			} else {
				Outliner.clipboard.setContents(new NodeSetTransferable(nodeSet), Outliner.outliner);
			}
		}
		
		// Delete selection
		delete(tree,layout,false);
	}

	private void paste(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Abort if node is not editable
		if (!tree.getOldestInSelection().isEditable()) {
			return;
		}
			
		// Get the text from the clipboard and turn it into a tree
		boolean isNodeSet = false;
		String text = "";
		NodeSet nodeSet = new NodeSet();
		try {
			Transferable selection = (Transferable) Outliner.clipboard.getContents(this);
			if (selection != null) {
				if (selection instanceof NodeSetTransferable) {
					nodeSet = (NodeSet) selection.getTransferData(NodeSetTransferable.nsFlavor);
					isNodeSet = true;
				} else {
					text = (String) selection.getTransferData(DataFlavor.stringFlavor);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Figure out where to do the insert
		Node oldestNode = tree.getOldestInSelection();
		Node parentForNewNode = oldestNode.getParent();
		int indexForNewNode = oldestNode.currentIndex() + 1;
		int depth = oldestNode.getDepth();

		tree.clearSelection();
		tree.setSelectedNodesParent(parentForNewNode);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(parentForNewNode);
		
		if (isNodeSet) {
			for (int i = nodeSet.getSize() - 1; i >= 0; i--) {
				Node node = nodeSet.getNode(i);
				node.setTree(tree, true);
				parentForNewNode.insertChild(node, indexForNewNode);
				node.setDepthRecursively(depth);
				tree.insertNode(node);

				// Record the Insert in the undoable
				int index = node.currentIndex() + i;
				undoable.addPrimitive(new PrimitiveUndoableInsert(parentForNewNode, node, index));

				tree.addNodeToSelection(node);
			}
		} else {
			Node tempRoot = PadSelection.pad(text, tree, depth, Preferences.LINE_END_STRING);
		
			for (int i = tempRoot.numOfChildren() - 1; i >= 0; i--) {
				Node node = tempRoot.getChild(i);
				parentForNewNode.insertChild(node, indexForNewNode);
				tree.insertNode(node);

				// Record the Insert in the undoable
				int index = node.currentIndex() + i;
				undoable.addPrimitive(new PrimitiveUndoableInsert(parentForNewNode, node, index));

				tree.addNodeToSelection(node);
			}
		}

		tree.getDocument().undoQueue.add(undoable);

		Node nodeThatMustBeVisible = tree.getYoungestInSelection();

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(nodeThatMustBeVisible);

		// Redraw and Set Focus
		layout.draw(nodeThatMustBeVisible, OutlineLayoutManager.ICON);
	}

	private void selectAll(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// select all siblings
		Node parent = currentNode.getParent();
		
		tree.clearSelection();
		tree.addNodeToSelection(parent.getChild(0));
		tree.selectRangeFromMostRecentNodeTouched(parent.getChild(parent.numOfChildren() - 1));

		// Redraw and Set Focus
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	private void selectInverse(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// select all siblings
		Node parent = currentNode.getParent();
		
		for (int i = 0, limit = parent.numOfChildren(); i < limit; i++) {
			Node child = parent.getChild(i);
			
			if (child.isSelected()) {
				tree.removeNodeFromSelection(child);
			} else {
				tree.addNodeToSelection(child);
			}
		}
		
		if (tree.getNumberOfSelectedNodes() == 0) {
			// Change to text node if all nodes were deselected.
			changeFocusToTextArea(tree, layout, POSITION_FIRST);
		} else {
			// Redraw and Set Focus
			layout.draw(currentNode, OutlineLayoutManager.ICON);
		}
	}

	private void selectNone(JoeTree tree, OutlineLayoutManager layout) {
		changeFocusToTextArea(tree, layout, POSITION_FIRST);
	}

	private void changeToParent(JoeTree tree, OutlineLayoutManager layout) {
		Node newSelectedNode = textArea.node.getParent();
		if (newSelectedNode.isRoot()) {return;}
		
		tree.setSelectedNodesParent(newSelectedNode.getParent());
		tree.addNodeToSelection(newSelectedNode);
		
		tree.setEditingNode(newSelectedNode);
		
		// Redraw and Set Focus
		layout.draw(newSelectedNode, OutlineLayoutManager.ICON);		
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
	
	protected void delete (JoeTree tree, OutlineLayoutManager layout, boolean deleteMode) {
		Node youngestNode = tree.getYoungestInSelection();
		Node parent = youngestNode.getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent, deleteMode);

		int startDeleting = 0;
		if (tree.isWholeDocumentSelected()) {
			// Abort if the doc is empty.
			if (tree.isDocumentEmpty()) {
				return;
			}
			
			// Swap in a new node for the first node since a doc always has at least one child of root.
			Node newNode = new NodeImpl(tree,"");
			newNode.setDepth(0);
			undoable.addPrimitive(new PrimitiveUndoableReplace(parent, youngestNode, newNode));
			
			startDeleting++;
		}

		// Iterate over the remaining selected nodes deleting each one
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = startDeleting, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);

			// Abort if node is not editable
			if (!node.isEditable()) {
				continue;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableReplace(parent, node, null));
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();
		}
		
		return;
	}

	private void merge(JoeTree tree, OutlineLayoutManager layout, boolean withSpaces) {
		JoeNodeList nodeList = tree.getSelectedNodes();

		// Get merged text
		StringBuffer buf = new StringBuffer();
		boolean didMerge = false;
		
		if (withSpaces) {
			for (int i = 0, limit = nodeList.size(); i < limit; i++) {
				Node node = nodeList.get(i);
				
				// Abort if node is not editable
				if (!node.isEditable()) {
					continue;
				}
				
				didMerge = true;
				node.getMergedValueWithSpaces(buf);
			}
		} else {
			for (int i = 0, limit = nodeList.size(); i < limit; i++) {
				Node node = nodeList.get(i);
				
				// Abort if node is not editable
				if (!node.isEditable()) {
					continue;
				}
				
				didMerge = true;
				node.getMergedValue(buf);
			}		
		}
		
		// It's possible all nodes were read-only. If so then abort.
		if (!didMerge) {
			return;
		}

		Node youngestNode = tree.getYoungestInSelection();
		Node parent = youngestNode.getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent);

		Node newNode = new NodeImpl(tree, buf.toString());
		newNode.setDepth(youngestNode.getDepth());
		newNode.setCommentState(youngestNode.getCommentState());
		
		undoable.addPrimitive(new PrimitiveUndoableReplace(parent, youngestNode, newNode));

		// Iterate over the remaining selected nodes deleting each one
		for (int i = 1, limit = nodeList.size(); i < limit; i++) {
			Node node = nodeList.get(i);
			
			// Abort if node is not editable
			if (!node.isEditable()) {
				continue;
			}

			undoable.addPrimitive(new PrimitiveUndoableReplace(parent,node,null));
		}

		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
			undoable.redo();		
		}
		
		return;
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


	// Editable
	private void clearEditable(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearEditableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void clearEditableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		
		if (oldValue != Node.EDITABLE_INHERITED) {
			node.setEditableState(Node.EDITABLE_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearEditableForSingleNode(node.getChild(i), undoable);
		}
	}
	
	private void toggleEditableAndClear(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleEditableAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleEditableForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearEditableForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	private void toggleEditable(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleEditableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		boolean isEditable = node.isEditable();
		
		if (oldValue == Node.EDITABLE_FALSE) {
			node.setEditableState(Node.EDITABLE_TRUE);
			newValue = Node.EDITABLE_TRUE;
					
		} else if (oldValue == Node.EDITABLE_TRUE) {
			node.setEditableState(Node.EDITABLE_FALSE);
			newValue = Node.EDITABLE_FALSE;
		
		} else {
			if (isEditable) {
				node.setEditableState(Node.EDITABLE_FALSE);
				newValue = Node.EDITABLE_FALSE;
			} else {
				node.setEditableState(Node.EDITABLE_TRUE);
				newValue = Node.EDITABLE_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
	}
	
	private void toggleEditableInheritance(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleEditableInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		tree.getDocument().attPanel.update();
		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	protected static void toggleEditableInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getEditableState();
		int newValue = Node.EDITABLE_INHERITED;
		boolean isEditable = node.isEditable();
		
		if (oldValue == Node.EDITABLE_INHERITED) {
			if (isEditable) {
				node.setEditableState(Node.EDITABLE_TRUE);
				newValue = Node.EDITABLE_TRUE;
			} else {
				node.setEditableState(Node.EDITABLE_FALSE);
				newValue = Node.EDITABLE_FALSE;
			}
								
		} else {
			node.setEditableState(Node.EDITABLE_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableEditableChange(node, oldValue, newValue));
	}


	// Moveable
	private void clearMoveable(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			clearMoveableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void clearMoveableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		
		if (oldValue != Node.MOVEABLE_INHERITED) {
			node.setMoveableState(Node.MOVEABLE_INHERITED);
			undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
		}
				
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearMoveableForSingleNode(node.getChild(i), undoable);
		}
	}
	
	private void toggleMoveableAndClear(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableAndClearForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleMoveableAndClearForSingleNode(Node node, CompoundUndoable undoable) {
		toggleMoveableForSingleNode(node, undoable);
		
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			clearMoveableForSingleNode(node.getChild(i), undoable);
		}		
	}
			
	private void toggleMoveable(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}

	protected static void toggleMoveableForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		boolean isMoveable = node.isMoveable();
		
		if (oldValue == Node.MOVEABLE_FALSE) {
			node.setMoveableState(Node.MOVEABLE_TRUE);
			newValue = Node.MOVEABLE_TRUE;
					
		} else if (oldValue == Node.MOVEABLE_TRUE) {
			node.setMoveableState(Node.MOVEABLE_FALSE);
			newValue = Node.MOVEABLE_FALSE;
		
		} else {
			if (isMoveable) {
				node.setMoveableState(Node.MOVEABLE_FALSE);
				newValue = Node.MOVEABLE_FALSE;
			} else {
				node.setMoveableState(Node.MOVEABLE_TRUE);
				newValue = Node.MOVEABLE_TRUE;
			}
		}
				
		undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
	}
	
	private void toggleMoveableInheritance(JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		
		JoeNodeList nodeList = tree.getSelectedNodes();
		for (int i = 0, limit = nodeList.size(); i < limit; i++) {
			toggleMoveableInheritanceForSingleNode(nodeList.get(i), undoable);
		}
		
		if (!undoable.isEmpty()) {
			tree.getDocument().undoQueue.add(undoable);
		}

		layout.draw(currentNode, OutlineLayoutManager.ICON);
	}
	
	protected static void toggleMoveableInheritanceForSingleNode(Node node, CompoundUndoable undoable) {
		int oldValue = node.getMoveableState();
		int newValue = Node.MOVEABLE_INHERITED;
		boolean isMoveable = node.isMoveable();
		
		if (oldValue == Node.MOVEABLE_INHERITED) {
			if (isMoveable) {
				node.setMoveableState(Node.MOVEABLE_TRUE);
				newValue = Node.MOVEABLE_TRUE;
			} else {
				node.setMoveableState(Node.MOVEABLE_FALSE);
				newValue = Node.MOVEABLE_FALSE;
			}
								
		} else {
			node.setMoveableState(Node.MOVEABLE_INHERITED);
		}
				
		undoable.addPrimitive(new PrimitiveUndoableMoveableChange(node, oldValue, newValue));
	}
}