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

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class TreeContext {

	// Instance Variables
	public OutlinerDocument doc = null;
	
	public ArrayList visibleNodes = new ArrayList(1000);
	public ArrayList selectedNodes = new ArrayList(100);
	public Node rootNode = null;


	// The Constructors
	public TreeContext(OutlinerDocument doc) {
		this();
		
		this.doc = doc;
		doc.panel.layout.setNodeToDrawFrom(getEditingNode(),0);
	}
	
	public void destroy() {
		visibleNodes = null;
		selectedNodes = null;
		rootNode.destroy();
		rootNode = null;
		editingNode = null;
		mostRecentNodeTouched = null;
		selectedNodesParent = null;
		doc = null;
	}

	public TreeContext() {
		// Create an empty Tree
		setRootNode(new NodeImpl(this,"ROOT"));
		NodeImpl child = new NodeImpl(this,"");
		rootNode.appendChild(child);
		rootNode.setHoisted(true);
		
		// Record the current location
		setEditingNode(child);	
	}
	
	// Accessors
	public Node getRootNode() {
		return rootNode;
	}
	
	public void setRootNode(Node node) {
		this.rootNode = node;
		rootNode.setExpandedClean(true);
	}


	// Statistics
	public int getLineCount() {
		return rootNode.getDecendantCount();
	}
	
	public int getCharCount() {
		return rootNode.getDecendantCharCount();
	}
	
	
	// Tracking the Editing Location
	private Node editingNode = null;
	private int cursorPosition = 0;
	private int cursorMarkPosition = 0;
	private int componentFocus = outlineLayoutManager.TEXT;

	public void setEditingNode(Node editingNode) {this.editingNode = editingNode;}
	public Node getEditingNode() {return editingNode;}

	public void setCursorMarkPosition(int cursorMarkPosition) {this.cursorMarkPosition = cursorMarkPosition;}
	public int getCursorMarkPosition() {return cursorMarkPosition;}

	public void setComponentFocus(int componentFocus) {
		this.componentFocus = componentFocus;
		updateForCopyAndCut();
	}
	
	public int getComponentFocus() {return componentFocus;}

	public void setCursorPosition(int cursorPosition) {
		setCursorPosition(cursorPosition,true);
	}
	
	public void setCursorPosition(int cursorPosition, boolean setMark) {
		this.cursorPosition = cursorPosition;
		if (setMark) {
			setCursorMarkPosition(cursorPosition);
		}
		updateForCopyAndCut();
	}
	
	public int getCursorPosition() {return cursorPosition;}

	private void updateForCopyAndCut() {
		if (getComponentFocus() == outlineLayoutManager.TEXT) {
			if (getCursorPosition() == getCursorMarkPosition()) {
				Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(false);
				Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(false);
			} else {
				Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(true);
				Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(true);
			}
		} else if (getComponentFocus() == outlineLayoutManager.ICON) {
			if (selectedNodes.size() == 0) {
				Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(false);
				Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(false);
			} else {
				Outliner.menuBar.editMenu.EDIT_COPY_ITEM.setEnabled(true);
				Outliner.menuBar.editMenu.EDIT_CUT_ITEM.setEnabled(true);
			}			
		}	
	}


	// Tree Methods
	public Node getPrevNode(Node existingNode) {
		int prevNodeIndex = visibleNodes.indexOf(existingNode) - 1;
		if (prevNodeIndex < 0) {
			return null;
		}
		return (Node) visibleNodes.get(prevNodeIndex);
	}

	public Node getNextNode(Node existingNode) {
		int nextNodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nextNodeIndex >= visibleNodes.size()) {
			return null;
		}
		return (Node) visibleNodes.get(nextNodeIndex);
	}
	
	public void addNode(Node node) {
		visibleNodes.add(node);
	}
	
	public void removeNode(Node node) {
		node.removeFromVisibleNodesCache(this);
	}

	public void insertNode(Node node) {
		// Find the first Ancestor that is in the cache or Root
		Node ancestor = node.getYoungestVisibleAncestor();
		if (ancestor == null) {
			ancestor = rootNode;
		}
		
		// Expand all nodes in the path down to the node
		node.expandAllAncestors();
		
		// Walk the tree Downwards inserting all expanded nodes and their children
		ancestor.insertChildrenIntoVisibleNodesCache(this,visibleNodes.indexOf(ancestor));
	}
		
	public void insertNodeBefore(Node existingNode, Node newNode) {
		int nodeIndex = visibleNodes.indexOf(existingNode);
		if (nodeIndex >= 0) {
			visibleNodes.add(nodeIndex,newNode);
		}
	}

	public void insertNodeAfter(Node existingNode, Node newNode) {
		int nodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nodeIndex >= 0) {
			visibleNodes.add(nodeIndex,newNode);
		}
	}

	
	// Handling Node Selection
	private Node mostRecentNodeTouched = null;
	private Node selectedNodesParent = null;
	
	public int getNumberOfSelectedNodes() {
		return selectedNodes.size();
	}
	
	public void setSelectedNodesParent(Node node) {
		setSelectedNodesParent(node,true);
	}
	
	public void setSelectedNodesParent(Node node, boolean doClear) {
		if (doClear) {
			clearSelection();
		}
		this.selectedNodesParent = node;	
	}
	
	public Node getSelectedNodesParent() {return selectedNodesParent;}
	
	public void clearSelection() {
		// Walking it backwards should give better performance.
		while (selectedNodes.size() > 0) {
			((Node) selectedNodes.get(selectedNodes.size() - 1)).setSelected(false);
			selectedNodes.remove(selectedNodes.size() - 1);
		}
	}
	
	public void addNodeToSelection(Node node) {
		if (node.isSelected()) {
			return; // Don't add a node if it is already selected.
		} else if (node.getParent() == getSelectedNodesParent()) {			
			node.setSelected(true);
			mostRecentNodeTouched = node;
			
			// Maintain the selected nodes in order from youngest to oldest
			for (int i = 0; i < selectedNodes.size(); i++) {
				if (((Node) selectedNodes.get(i)).currentIndex() > node.currentIndex()) {
					selectedNodes.add(i, node);
					return;
				}
			}
			
			selectedNodes.add(node);
		}
	}
	
	public void removeNodeFromSelection(Node node) {
		if (node.getParent() == getSelectedNodesParent()) {
			node.setSelected(false);
			int index = selectedNodes.indexOf(node);
			if (index != -1) {
				selectedNodes.remove(index);
			}
		}	
	}
	
	public void selectRangeFromMostRecentNodeTouched(Node node) {
		if (node.getParent() == getSelectedNodesParent()) {
			int indexA = mostRecentNodeTouched.currentIndex();
			int indexB = node.currentIndex();
			
			int start = Math.min(indexA,indexB);
			int end = Math.max(indexA,indexB);
			
			clearSelection();
			
			for (int i = start; i <= end; i++) {
				Node theNode = getSelectedNodesParent().getChild(i);
				theNode.setSelected(true);
				selectedNodes.add(theNode);
			}
		}	
	}

	public Node getYoungestInSelection() {
		return (Node) selectedNodes.get(0);
	}
	
	public Node getOldestInSelection() {
		return (Node) selectedNodes.get(selectedNodes.size() - 1);
	}


	// Tree Manipulation
	public void moveNodeAbove(Node currentNode, Node targetNode) {
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Append the selected node to the target nodes parent.
		targetNode.getParent().insertChild(currentNode,targetNode.currentIndex());
		currentNode.setDepthRecursively(targetNode.getDepth());
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);	
	}

	public void moveNodeBelow(Node currentNode, Node targetNode) {
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Append the selected node to the target nodes parent or the node if it has children and is expanded.
		if (!targetNode.isLeaf() && targetNode.isExpanded()) {
			targetNode.insertChild(currentNode,0);
			currentNode.setDepthRecursively(targetNode.getDepth() + 1);
		} else {
			targetNode.getParent().insertChild(currentNode,targetNode.currentIndex() + 1);	
			currentNode.setDepthRecursively(targetNode.getDepth());
		}
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);	
	}

	public void moveNodeAboveAsSibling(Node currentNode, Node targetNode) {
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Re-Insert the Node
		targetNode.getParent().insertChild(currentNode,targetNode.currentIndex());
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);	
	}

	public void moveNodeBelowAsSibling(Node currentNode, Node targetNode) {
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Re-Insert the Node
		targetNode.getParent().insertChild(currentNode,targetNode.currentIndex() + 1);	
		currentNode.setDepthRecursively(targetNode.getDepth());
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);	
	}
	
	public void promoteNode(Node currentNode) {
		Node targetNode = currentNode.getParent().getParent();
		int insertIndex = currentNode.getParent().currentIndex() + 1;
		if (currentNode.getParent().isRoot()) {
			// Our parent is root. Since we can't be promoted to root level, Abort.
			return;
		}
		
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Append the selected node to the target node.
		targetNode.insertChild(currentNode,insertIndex);
		currentNode.setDepthRecursively(targetNode.getDepth() + 1);
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);
	}
	
	public void demoteNode(Node currentNode) {
		demoteNode(currentNode,currentNode.prevSibling());
	}
	
	public void demoteNode(Node currentNode, Node targetNode) {
		if (targetNode == currentNode) {
			// We have no previous sibling, so Abort.
			return;
		}

		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode);
			
		// Append the selected node to the target node.
		targetNode.insertChild(currentNode,targetNode.numOfChildren());
		currentNode.setDepthRecursively(targetNode.getDepth() + 1);

		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);
	}
	
	
	// Misc Methods
	public boolean isWholeDocumentSelected() {
		if ((selectedNodesParent != null) &&
			selectedNodesParent.isRoot() && 
			(getNumberOfSelectedNodes() == selectedNodesParent.numOfChildren()))
		{
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDocumentEmpty() {
		if ((rootNode.numOfChildren() == 1) &&
			rootNode.getFirstChild().isLeaf() &&
			rootNode.getFirstChild().getValue().equals(""))
		{
			return true;
		} else {
			return false;
		}
	}
}