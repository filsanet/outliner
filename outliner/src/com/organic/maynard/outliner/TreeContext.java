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

public class TreeContext extends AttributeContainerImpl {

	// Instance Variables
	public OutlinerDocument doc = null;
	
	public NodeList visibleNodes = new NodeList(1000);
	public NodeList selectedNodes = new NodeList(100);
	public Node rootNode = null;

	private HashMap attributes = null;


	// The Constructors
	public TreeContext(OutlinerDocument doc) {
		this();
		
		this.doc = doc;
		doc.panel.layout.setNodeToDrawFrom(getEditingNode(),0);
	}

	public TreeContext() {
		// Create an empty Tree
		setRootNode(new NodeImpl(this,"ROOT"));
		rootNode.setHoisted(true);

		NodeImpl child = new NodeImpl(this,"");
		child.setDepth(0);
		rootNode.insertChild(child, 0);
		insertNode(child);
		
		// Record the current location
		setEditingNode(child, false);
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
	
	
	// Comments
	private boolean comment = false;

	public void setRootNodeCommentState(boolean comment) {this.comment = comment;}
	public boolean getRootNodeCommentState() {return this.comment;}

	// Editablity
	private boolean editable = true;

	public void setRootNodeEditableState(boolean editable) {this.editable = editable;}
	public boolean getRootNodeEditableState() {return this.editable;}

	// Moveability
	private boolean moveable = true;

	public void setRootNodeMoveableState(boolean moveable) {this.moveable = moveable;}
	public boolean getRootNodeMoveableState() {return this.moveable;}


	// Line Count Control
	private int lineCountKey = 0;
	
	public int getLineCountKey() {
		return lineCountKey;
	}
	
	public void incrementLineCountKey() {
		// Lets not grow forever since it could be possible to 
		// exceed max int although very very very unlikely, but still, better to be safe.
		if (lineCountKey > 1000000) {
			lineCountKey = 0;
		} else {
			lineCountKey++;
		}
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
	private int componentFocus = OutlineLayoutManager.TEXT;

	public void setEditingNode(Node editingNode) {
		setEditingNode(editingNode, true);
	}

	public void setEditingNode(Node editingNode, boolean updateAttPanel) {
		this.editingNode = editingNode;
		
		if (updateAttPanel) {
			doc.attPanel.update();
		}
	}
	
	public Node getEditingNode() {
		return editingNode;
	}

	public void setCursorMarkPosition(int cursorMarkPosition) {
		this.cursorMarkPosition = cursorMarkPosition;
	}
	public int getCursorMarkPosition() {
		return cursorMarkPosition;
	}

	public void setComponentFocus(int componentFocus) {
		this.componentFocus = componentFocus;
		updateEditMenu();
	}
	
	public int getComponentFocus() {
		return componentFocus;
	}

	public void setCursorPosition(int cursorPosition) {
		setCursorPosition(cursorPosition,true);
	}
	
	public void setCursorPosition(int cursorPosition, boolean setMark) {
		this.cursorPosition = cursorPosition;
		if (setMark) {
			setCursorMarkPosition(cursorPosition);
		}
		updateEditMenu();
	}
	
	public int getCursorPosition() {
		return cursorPosition;
	}

	private static JMenuItem cutItem = null;
	private static JMenuItem copyItem = null;
	private static JMenuItem deleteItem = null;
	private static JMenuItem selectInverseItem = null;

	public void updateEditMenu() {
		if (cutItem == null) {
			cutItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.CUT_MENU_ITEM);
			copyItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.COPY_MENU_ITEM);
			deleteItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.DELETE_MENU_ITEM);
			selectInverseItem = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SELECT_INVERSE_MENU_ITEM);
		}

		if (getComponentFocus() == OutlineLayoutManager.TEXT) {
			selectInverseItem.setEnabled(false);
			
			if (getCursorPosition() == getCursorMarkPosition()) {
				copyItem.setEnabled(false);
				cutItem.setEnabled(false);
				deleteItem.setEnabled(false);
			} else {
				copyItem.setEnabled(true);
				cutItem.setEnabled(true);
				deleteItem.setEnabled(true);
			}
		} else if (getComponentFocus() == OutlineLayoutManager.ICON) {
			selectInverseItem.setEnabled(true);
			
			if (selectedNodes.size() == 0) {
				copyItem.setEnabled(false);
				cutItem.setEnabled(false);
				deleteItem.setEnabled(false);
			} else {
				copyItem.setEnabled(true);
				cutItem.setEnabled(true);
				deleteItem.setEnabled(true);
			}			
		}	
	}


	// Tree Methods
	public Node getPrevNode(Node existingNode) {
		int prevNodeIndex = visibleNodes.indexOf(existingNode) - 1;
		if (prevNodeIndex < 0) {
			return null;
		}
		return visibleNodes.get(prevNodeIndex);
	}

	public Node getNextNode(Node existingNode) {
		int nextNodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nextNodeIndex >= visibleNodes.size()) {
			return null;
		}
		return visibleNodes.get(nextNodeIndex);
	}
	
	public void addNode(Node node) {
		visibleNodes.add(node);
	}
	
	public void removeNode(Node node) {
		int index = visibleNodes.indexOf(node);
		
		if (index != -1) {
			int lastIndex = visibleNodes.indexOf(node.getLastViewableDecendent());
			visibleNodes.removeRange(index, lastIndex + 1);
		}
	}

	public void insertNode(Node node) {
		// Find the first Ancestor that is in the cache or Root
		Node ancestor = node.getYoungestVisibleAncestor();
		
		// Expand all nodes in the path down to the node
		node.expandAllAncestors();
		
		// Walk the tree Downwards inserting all expanded nodes and their children
		ancestor.insertChildrenIntoVisibleNodesCache(visibleNodes.indexOf(ancestor));
	}

	public int insertNodeAfter(Node existingNode, Node newNode) {
		int nodeIndex = visibleNodes.indexOf(existingNode) + 1;
		if (nodeIndex >= 0) {
			visibleNodes.add(nodeIndex, newNode);
		}
		return nodeIndex;
	}

	public void insertNode(Node node, int index) {
		visibleNodes.add(index, node);
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
		for (int i = selectedNodes.size() - 1; i >= 0; i--) {
			selectedNodes.get(i).setSelected(false);
		}
		
		selectedNodes.clear();
	}
	
	public void addNodeToSelection(Node node) {
		if (node.isSelected()) {
			return; // Don't add a node if it is already selected.
		} else if (node.getParent() == getSelectedNodesParent()) {			
			node.setSelected(true);
			mostRecentNodeTouched = node;
			
			// Maintain the selected nodes in order from youngest to oldest
			if (selectedNodes.size() > 0) {
				int nodeIndex = node.currentIndex();
				
				NodeImpl parent = (NodeImpl) selectedNodesParent;
				int searchStartIndex = 0;
				int childCount = parent.children.size() - 1;
				
				for (int i = 0; i < selectedNodes.size(); i++) {
					searchStartIndex = parent.children.indexOf(selectedNodes.get(i), searchStartIndex, childCount);
					if (searchStartIndex > nodeIndex) {
						selectedNodes.add(i, node);
						return;
					}
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
		try {
			return selectedNodes.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public Node getOldestInSelection() {
		try {
			return selectedNodes.get(selectedNodes.size() - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}


	// Tree Manipulation
	public void promoteNode(Node currentNode, int currentNodeIndex) {
		if (currentNode.getParent().isRoot()) {
			// Our parent is root. Since we can't be promoted to root level, Abort.
			return;
		}

		Node targetNode = currentNode.getParent().getParent();
		int insertIndex = currentNode.getParent().currentIndex() + 1;
		
		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode, currentNodeIndex);
			
		// Append the selected node to the target node.
		targetNode.insertChild(currentNode, insertIndex);
		currentNode.setDepthRecursively(targetNode.getDepth() + 1);
		
		// Now Remove and Insert into the VisibleNodes Cache
		removeNode(currentNode);
		insertNode(currentNode);
	}
	
	public void demoteNode(Node currentNode, Node targetNode, int currentNodeIndex) {
		if (targetNode == currentNode) {
			// We have no previous sibling, so Abort.
			return;
		}

		// Remove the selected node from the current parent node.
		currentNode.getParent().removeChild(currentNode, currentNodeIndex);
			
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