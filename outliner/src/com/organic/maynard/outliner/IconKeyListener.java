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
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

public class IconKeyListener implements KeyListener, MouseListener {
	
	private OutlinerCellRendererImpl textArea = null;
		
	// The Constructors
	public IconKeyListener(OutlinerCellRendererImpl textArea) {
		this.textArea = textArea;
	}
	
	public void destroy() {
		textArea = null;
	}

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {
		//System.out.println("ICON Mouse Entered: " + e.paramString());
	}
	
	public void mouseExited(MouseEvent e) {
		//System.out.println("ICON Mouse Exited: " + e.paramString());
	}
	
	public void mousePressed(MouseEvent e) {
		//System.out.println("ICON Mouse Pressed: " + e.paramString());
		
		// This is detection for Solaris
		if (e.isPopupTrigger() && textArea.node.isSelected()) {
			Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
		}
		
		// Handle clicks. The modulo is to deal with rapid clicks that would register as a triple click or more.
		if ((e.getClickCount() % 2) == 1) {
			processSingleClick(e);
		} else if ((e.getClickCount() % 2) == 0){
			processDoubleClick(e);
		}
		
		// Record the EditingNode and CursorPosition and ComponentFocus
 		TreeContext tree = textArea.node.getTree();
		tree.setEditingNode(textArea.node);
		tree.setComponentFocus(outlineLayoutManager.ICON);
		
		// Redraw and set focus
		textArea.button.requestFocus();
		tree.doc.panel.layout.draw();
	}
	
	public void mouseReleased(MouseEvent e) {
		//System.out.println("ICON Mouse Released: " + e.paramString());
		
		// This is detection for Windows
		if (e.isPopupTrigger() && textArea.node.isSelected()) {
			Outliner.macroPopup.show(e.getComponent(),e.getX(), e.getY());
		}
	} 
	public void mouseClicked(MouseEvent e) {}
	
	    
	protected void processSingleClick(MouseEvent e) {
		Node node = textArea.node;
		TreeContext tree = node.getTree();
		
		if (e.isShiftDown()) {
			tree.selectRangeFromMostRecentNodeTouched(node);
		
		} else if (e.isControlDown()) {
			if (node.isSelected() && (tree.selectedNodes.size() != 1)) {
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
		if (textArea.node.isExpanded()) {
			textArea.node.setExpanded(false);
		} else {
			textArea.node.setExpanded(true);
		}
		
		if (textArea.node.isSelected()) {
			textArea.node.getTree().setSelectedNodesParent(textArea.node.getParent());
			textArea.node.getTree().addNodeToSelection(textArea.node);
		}		
	}
	
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		//System.out.println("ICON Pressed: " + e.paramString());

		// Create some short names for convienence
		TreeContext tree = textArea.node.getTree();
		outlineLayoutManager layout = tree.doc.panel.layout;

		switch(e.getKeyCode()) {
			case KeyEvent.VK_PAGE_DOWN:
				toggleExpansion(tree,layout);
				break;
			
			case KeyEvent.VK_DELETE:
				delete(tree,layout);
				break;

			case KeyEvent.VK_BACK_SPACE:
				delete(tree,layout);
				break;
				
			case KeyEvent.VK_UP:
				if (e.isShiftDown()) {
					moveUp(tree,layout);
				} else {
					navigate(tree, layout, UP);
				}
				break;

			case KeyEvent.VK_DOWN:
				if (e.isShiftDown()) {
					moveDown(tree,layout);
				} else {
					navigate(tree,layout, DOWN);
				}
				break;

			case KeyEvent.VK_LEFT:
				if (e.isShiftDown()) {
					moveLeft(tree,layout);
				} else {
					navigate(tree,layout, LEFT);
				}
				break;

			case KeyEvent.VK_RIGHT:
				if (e.isShiftDown()) {
					moveRight(tree,layout);
				} else {
					navigate(tree,layout, RIGHT);
				}
				break;

			case KeyEvent.VK_ENTER:
				if (e.isShiftDown()) {
					changeFocusToTextArea(tree,layout);
				} else {
					insert(tree,layout);
				}
				break;

			case KeyEvent.VK_TAB:
				if (e.isShiftDown()) {
					promote(tree,layout);
				} else {
					demote(tree,layout);
				}
				// Update the selection model
				Node youngestNode = tree.getYoungestInSelection();
				tree.setSelectedNodesParent(youngestNode.getParent(),false);
	
				// Redraw and Set Focus
				layout.draw(youngestNode,outlineLayoutManager.ICON);
				
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

			case KeyEvent.VK_A:
				if (e.isControlDown() && !e.isShiftDown()) {
					selectAll(tree,layout);
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
		// Catch any unwanted chars that slip through
		if (e.isControlDown() ||
			(e.getKeyChar() == KeyEvent.VK_BACK_SPACE) ||
			(e.getKeyChar() == KeyEvent.VK_TAB) ||
			(e.getKeyChar() == KeyEvent.VK_ENTER)) {
			return;
		}
				
		//System.out.println("ICON Typed: " + e.paramString());

		// Create some short names for convienence
		TreeContext tree = textArea.node.getTree();
		Node currentNode = textArea.node;
		outlineLayoutManager layout = textArea.node.getTree().doc.panel.layout;


		Node youngestNode = tree.getYoungestInSelection();

		// Clear the selection since focus will change to the textarea.
		tree.clearSelection();
		
		// Replace the text with the character that was typed
		youngestNode.setValue(String.valueOf(e.getKeyChar()));

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setCursorPosition(1);
		tree.setComponentFocus(outlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		OutlinerCellRendererImpl youngestTextArea = layout.getUIComponent(youngestNode);
		tree.doc.undoQueue.add(new UndoableEdit(youngestNode,youngestTextArea.getText(),youngestNode.getValue(),youngestTextArea.getCaretPosition(),tree.getCursorPosition(),youngestTextArea.getCaret().getMark(),tree.getCursorPosition()));
		
		// Redraw and Set Focus
		layout.draw(youngestNode,outlineLayoutManager.TEXT);

		e.consume();
		return;
	}
	public void keyReleased(KeyEvent e) {}



	// Key Handlers
	private void toggleExpansion(TreeContext tree, outlineLayoutManager layout) {
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			Node node = (Node) tree.selectedNodes.get(i);
			if (node.isExpanded()) {
				node.setExpanded(false);
			} else {
				node.setExpanded(true);
			}
		}

		layout.draw();
	}

	private void changeFocusToTextArea(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;
		tree.setComponentFocus(outlineLayoutManager.TEXT);
		tree.clearSelection();
		layout.draw(currentNode,outlineLayoutManager.TEXT);
	}

	private void moveUp(TreeContext tree, outlineLayoutManager layout) {
		Node youngestNode = tree.getYoungestInSelection();
		Node node = youngestNode.prevSibling();
		if (node == youngestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(),node.getParent());
		tree.doc.undoQueue.add(undoable);

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().prevUnSelectedNode();
	
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), node.currentIndex()));
			tree.moveNodeAboveAsSibling(nodeToMove,node);
		}

		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the bottom when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().next();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode > ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		} else {
			int ioYoungestNode = tree.visibleNodes.indexOf(youngestNode);
			if (layout.getIndexOfNodeToDrawFrom() == ioYoungestNode) {
				layout.setNodeToDrawFrom(youngestNode, ioYoungestNode);
			}
		}
		
		layout.draw(youngestNode,outlineLayoutManager.ICON);
	}
	
	private void moveDown(TreeContext tree, outlineLayoutManager layout) {
		Node oldestNode = tree.getOldestInSelection();
		Node node = oldestNode.nextSibling();
		if (node == oldestNode) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(node.getParent(),node.getParent());
		tree.doc.undoQueue.add(undoable);
	
		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();
		
		// Do the move
		for (int i = tree.selectedNodes.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), node.currentIndex()));
			tree.moveNodeBelowAsSibling(nodeToMove,node);
		}
		
		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().prev();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode < ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		} else {
			int ioOldestNode = tree.visibleNodes.indexOf(oldestNode.getLastViewableDecendent());
			if (layout.getIndexOfNodeToDrawFrom() == ioOldestNode) {
				layout.setNodeToDrawFrom(oldestNode.getLastViewableDecendent(), ioOldestNode);
			}
		}
		
		layout.draw(oldestNode.getLastViewableDecendent(), outlineLayoutManager.ICON);
	}

	private void moveLeft(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;
		Node youngestNode = tree.getYoungestInSelection();
		Node node = tree.getPrevNode(youngestNode);
		if (node == null) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(), node.getParent());
		tree.doc.undoQueue.add(undoable);

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().prevUnSelectedNode();
	
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), node.currentIndex()));
			tree.moveNodeAbove(nodeToMove,node);
		}

		// Update the selection model
		tree.setSelectedNodesParent(node.getParent(),false);

		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the bottom when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().next();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode > ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		} else {
			int ioYoungestNode = tree.visibleNodes.indexOf(youngestNode);
			if (layout.getIndexOfNodeToDrawFrom() == ioYoungestNode) {
				layout.setNodeToDrawFrom(youngestNode, ioYoungestNode);
			}
		}
		
		layout.draw(youngestNode,outlineLayoutManager.ICON);
	}


	private void moveRight(TreeContext tree, outlineLayoutManager layout) {
		Node oldestNode = tree.getOldestInSelection();
		Node node = tree.getNextNode(oldestNode.getLastViewableDecendent());
		if (node == null) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable;
		if ((!node.isLeaf() && node.isExpanded())) {
			undoable = new CompoundUndoableMove(oldestNode.getParent(),node);				
		} else {
			undoable = new CompoundUndoableMove(oldestNode.getParent(),node.getParent());
		}
		tree.doc.undoQueue.add(undoable);

		// Store nodeToDrawFrom if neccessary. Used when the selection is dissconnected.
		Node nodeToDrawFromTmp = layout.getNodeToDrawFrom().nextUnSelectedNode();
	
		for (int i = tree.selectedNodes.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			
			int targetIndex = 0;
			if ((!node.isLeaf() && node.isExpanded())) {
				// do nothing
			} else {
				targetIndex = node.currentIndex();
			}

			if (nodeToMove.getParent() != node.getParent()) {
				targetIndex++;
			}
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));

			tree.moveNodeBelow(nodeToMove,node);
		}

		// Update the selection model
		tree.setSelectedNodesParent(node.getParent(),false);

		// Redraw and Set Focus
		if (layout.getNodeToDrawFrom().isAncestorSelected()) { // Makes sure we dont' stick at the top when multiple nodes are selected.
			Node visNode = layout.getNodeToDrawFrom().prev();
			int ioVisNode = tree.visibleNodes.indexOf(visNode);
			int ioNodeToDrawFromTmp = tree.visibleNodes.indexOf(nodeToDrawFromTmp);
			if (ioVisNode < ioNodeToDrawFromTmp) {
				layout.setNodeToDrawFrom(visNode, ioVisNode);
			} else {
				layout.setNodeToDrawFrom(nodeToDrawFromTmp, ioNodeToDrawFromTmp);
			}
		} else {
			int ioOldestNode = tree.visibleNodes.indexOf(oldestNode.getLastViewableDecendent());
			if (layout.getIndexOfNodeToDrawFrom() == ioOldestNode) {
				layout.setNodeToDrawFrom(oldestNode.getLastViewableDecendent(), ioOldestNode);
			}
		}
		
		layout.draw(oldestNode.getLastViewableDecendent(), outlineLayoutManager.ICON);
	}

	private void insert(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Create a new node and insert it as a sibling immediatly after the last selected node.
		Node node = tree.getOldestInSelection();
		Node newNode = new NodeImpl(tree,"");
		
		if ((!node.isLeaf()) && (node.isExpanded())) {
			newNode.setDepth(node.getDepth() + 1);
			node.insertChild(newNode,0);				
		} else {
			newNode.setDepth(node.getDepth());
			node.getParent().insertChild(newNode,node.currentIndex() + 1);
		}
		
		tree.insertNode(newNode);

		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(newNode);
		tree.setCursorPosition(0);
		tree.setComponentFocus(outlineLayoutManager.TEXT);

		// Redraw and Set Focus
		tree.clearSelection();
		layout.draw(newNode,outlineLayoutManager.TEXT);

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(currentNode.getParent());
		undoable.addPrimitive(new PrimitiveUndoableInsert(newNode.getParent(),newNode,newNode.currentIndex()));
		tree.doc.undoQueue.add(undoable);
	}

	private void promote(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		if (currentNode.getParent().isRoot()) {
			return;
		}

		// Put the Undoable onto the UndoQueue
		Node targetNode = currentNode.getParent().getParent();
		int targetIndex = currentNode.getParent().currentIndex() + 1;
		
		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);

		for (int i = tree.selectedNodes.size() - 1; i >= 0; i--) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), targetIndex));
			tree.promoteNode(nodeToMove);
		}
		
		tree.doc.undoQueue.add(undoable);
	}

	private void demote(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;

		if (tree.getYoungestInSelection().isFirstChild()) {
			return;
		}
		
		// Put the Undoable onto the UndoQueue
		Node targetNode = tree.getYoungestInSelection().prevSibling();

		CompoundUndoableMove undoable = new CompoundUndoableMove(currentNode.getParent(),targetNode);
		
		int existingChildren = targetNode.numOfChildren();
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			// Record the Insert in the undoable
			Node nodeToMove = (Node) tree.selectedNodes.get(i);
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, nodeToMove.currentIndex(), (existingChildren + i)));
			tree.demoteNode(nodeToMove,targetNode);
		}
		
		tree.doc.undoQueue.add(undoable);
	}

	private void copy(TreeContext tree, outlineLayoutManager layout) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			buffer.append(((Node) tree.selectedNodes.get(i)).depthPaddedValue(Preferences.LINE_END_STRING));
		}
		
		// Put the text onto the clipboard
		Outliner.clipboard.setContents(new StringSelection(buffer.toString()), Outliner.outliner);
	}

	private void cut(TreeContext tree, outlineLayoutManager layout) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			buffer.append(((Node) tree.selectedNodes.get(i)).depthPaddedValue(Preferences.LINE_END_STRING));
		}
		
		// Put the text onto the clipboard
		Outliner.clipboard.setContents(new StringSelection(buffer.toString()), Outliner.outliner);
		
		delete(tree,layout);
	}

	private void paste(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// Put the Undoable onto the UndoQueue
		CompoundUndoableInsert undoable = new CompoundUndoableInsert(currentNode.getParent());
		tree.doc.undoQueue.add(undoable);

		// Get the text from the clipboard and turn it into a tree
		StringSelection selection = (StringSelection) Outliner.clipboard.getContents(this);
		String text = "";
		try {
			text = (String) selection.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Node oldestNode = tree.getOldestInSelection();
		
		Node tempRoot = PadSelection.pad(text, tree, oldestNode.getDepth(),Preferences.LINE_END_STRING);
		
		Node parentForNewNode = oldestNode.getParent();
		int indexForNewNode = parentForNewNode.getChildIndex(oldestNode);
		for (int i = tempRoot.numOfChildren() - 1; i >= 0; i--) {
			Node node = tempRoot.getChild(i);
			parentForNewNode.insertChild(node, indexForNewNode + 1);
			tree.insertNode(node);

			// Record the Insert in the undoable
			int index = node.currentIndex() + i;
			undoable.addPrimitive(new PrimitiveUndoableInsert(parentForNewNode,node,index));
		}
		
		tree.clearSelection();

		for (int i = tempRoot.numOfChildren() - 1; i >= 0; i--) {
			tree.addNodeToSelection(tempRoot.getChild(i));
		}


		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(tree.getYoungestInSelection());

		// Redraw and Set Focus
		layout.draw(tree.getYoungestInSelection(),outlineLayoutManager.ICON);
	}

	private void selectAll(TreeContext tree, outlineLayoutManager layout) {
		Node currentNode = textArea.node;

		// select all siblings
		Node parent = currentNode.getParent();
		
		tree.addNodeToSelection(parent.getChild(0));
		tree.selectRangeFromMostRecentNodeTouched(parent.getChild(parent.numOfChildren() - 1));

		// Redraw and Set Focus
		layout.draw(currentNode,outlineLayoutManager.ICON);
	}

	private static final int UP = 1;
	private static final int DOWN = 2;
	private static final int LEFT = 3;
	private static final int RIGHT = 4;
	
	private void navigate(TreeContext tree, outlineLayoutManager layout, int type) {
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
		layout.draw(node,outlineLayoutManager.ICON);
	}
	
	protected void delete (TreeContext tree, outlineLayoutManager layout) {
		Node youngestNode = tree.getYoungestInSelection();
		Node parent = tree.getEditingNode().getParent();
		CompoundUndoableReplace undoable = new CompoundUndoableReplace(parent);

		if (tree.isWholeDocumentSelected()) {
			if (tree.isDocumentEmpty()) {return;}
			
			Node newNode = new NodeImpl(tree,"");
			newNode.setDepth(0);
			undoable.addPrimitive(new PrimitiveUndoableReplace(parent,youngestNode,newNode));

			// Iterate over the remaining selected nodes deleting each one
			for (int i = 1; i < tree.getNumberOfSelectedNodes(); i++) {
				Node node = (Node) tree.selectedNodes.get(i);
				undoable.addPrimitive(new PrimitiveUndoableReplace(parent,node,null));
			}
		} else {
			// Iterate over the remaining selected nodes deleting each one
			for (int i = 0; i < tree.getNumberOfSelectedNodes(); i++) {
				Node node = (Node) tree.selectedNodes.get(i);
				undoable.addPrimitive(new PrimitiveUndoableReplace(parent,node,null));
			}
		}

		tree.doc.undoQueue.add(undoable);
		undoable.redo();
		return;
	}


	// Additional Outline Methods
	public static void expandAllSubheads(TreeContext tree) {
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			((Node) tree.selectedNodes.get(i)).ExpandAllSubheads();
		}
		tree.doc.panel.layout.draw();
		return;
	}

	public static void expandEverything(TreeContext tree) {
		TextKeyListener.expandEverything(tree);
		return;
	}

	public static void collapseToParent(TreeContext tree) {
		TextKeyListener.collapseToParent(tree.getEditingNode());
		return;
	}

	public static void collapseEverything(TreeContext tree) {
		TextKeyListener.collapseEverything(tree);
		return;
	}
}