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
import java.awt.*;

public class NodeImpl implements Node {
	
	// Instance Fields		
	private TreeContext tree = null;
	private Node parent = null;
	private String value = null;
	private ArrayList children = new ArrayList(10);

	private int depth = -1; // -1 so that children of root will be depth 0.
	
	private boolean expanded = false;
	private boolean visible = false;
	private boolean partiallyVisible = false;
	private boolean selected = false;
	

	// The Constructors
	public NodeImpl(TreeContext tree, String value) {
		this.tree = tree;
		this.value = value;
	}

	public void destroy() {
		for (int i = 0; i < children.size(); i++) {
			Node child = (Node) children.get(i);
			child.destroy();
		}
		
		tree = null;
		parent = null;
		value = null;
		children = null;
	}

	// Explicit Cloning Method
	public Node cloneClean() {
		NodeImpl nodeImpl = new NodeImpl(tree,value);
		nodeImpl.setParent(parent);
		nodeImpl.setDepth(depth);
		
		// And clone the children
		for (int i = 0; i < numOfChildren(); i++) {
			nodeImpl.insertChild(getChild(i).cloneClean(),i);
		}
		
		return nodeImpl;
	}
	

	// Statistics Methods
	public int getDecendantCount() {
		if (isLeaf()) {
			return 0;
		} else {
			int count = 0;
			for (int i = 0; i < numOfChildren(); i++) {
				count++;
				count += getChild(i).getDecendantCount();
			}
			return count;
		}
	}
	
	public int getDecendantCharCount() {
		if (isLeaf()) {
			return 0;
		} else {
			int count = 0;
			for (int i = 0; i < numOfChildren(); i++) {
				count += getChild(i).getValue().length();
				count += getChild(i).getDecendantCharCount();
			}
			return count;
		}
	}
	
	// Parent Methods
	public void setParent(Node node) {this.parent = node;}
	public Node getParent() {return parent;}
	
	// Child Methods
	public int numOfChildren() {return children.size();}
	
	public void appendChild(Node node) {
		children.add(node);
		node.setParent(this);
		
		// Set the childs Depth
		node.setDepth(getDepth() + 1);
		
		// Insert the new child into the list of visible nodes if we are expanded.
		if (isExpanded()) {
			tree.insertNodeAfter(this, node);
		}
	}
	
	public void removeChild(Node node) {
		node.setParent(null);
		children.remove(node);
	}
	
	public Node getChild(int i) {
		try {
			return (Node) children.get(i);
		} catch (IndexOutOfBoundsException iofbe) {
			return null;
		}
	}

	public Node getFirstChild() {
		if (isLeaf()) {
			return null;
		} else {
			return (Node) children.get(0);
		}
	}
	
	public Node getLastChild() {
		if (isLeaf()) {
			return null;
		} else {
			return (Node) children.get(children.size() - 1);
		}
	}
	
	public Node getLastDecendent() {
		Node node = getLastChild();
		if (node == null) {
			return this;
		} else {
			return node.getLastDecendent();
		}
	}

	public Node getLastViewableDecendent() {
		if (isExpanded()) {
			Node node = getLastChild();
			if (node == null) {
				return this;
			} else {
				return node.getLastViewableDecendent();
			}
		} else {
			return this;
		}
	}
	
	public Node getYoungestVisibleAncestor() {
		if (getParent().isRoot()) {
			return null;
		}
		
		if (tree.visibleNodes.contains(getParent())) {
			return getParent();
		} else {
			return getParent().getYoungestVisibleAncestor();
		}
	}
	
	// This method could be optomized better by first finding the range and then doing a batch insert.
	public int insertChildrenIntoVisibleNodesCache(TreeContext tree, int index) {
		if (isExpanded()) {
			for (int i = 0; i < this.numOfChildren(); i++) {
				index++;
				Node child = getChild(i);
				if (!tree.visibleNodes.contains(child)) {
					tree.visibleNodes.add(index,child);
				}
				index = child.insertChildrenIntoVisibleNodesCache(tree,index);
			}		
		}
		return index;
	}
	
	// This method could be optomized better by first finding the range and then doing a batch removal.
	public void removeFromVisibleNodesCache(TreeContext tree) {
		int index = tree.visibleNodes.indexOf(this);
		if (index != -1) {
			tree.visibleNodes.remove(index);
			if (isExpanded()) {
				for (int i = 0; i < this.numOfChildren(); i++) {
					getChild(i).removeFromVisibleNodesCache(tree);
				}		
			}
		}
	}
	
	public void insertChild(Node node, int i) {
		children.add(i,node);
		node.setParent(this);
	}
	
	public int getChildIndex(Node node) {
		return children.indexOf(node);
	}
	
	public boolean isLeaf() {
		if (children.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRoot() {
		if (getParent() == null) {
			return true;
		} else {
			return false;
		}
	}

	// Tree Accessor Methods
	public TreeContext getTree() {return tree;}
	
	// Visibility Methods	
	public void setVisible(boolean visible) {this.visible = visible;}
	public boolean isVisible() {return visible;}

	// Selection Methods	
	public void setSelected(boolean selected) {this.selected = selected;}
	public boolean isSelected() {return selected;}

	public boolean isAncestorSelected() {
		if (isSelected()) {
			return true;
		} else if (isRoot()) {
			return false;
		} else {
			return getParent().isAncestorSelected();
		}
	}
	
	public boolean isDecendantOf(Node node) {
		if (this == node) {
			return true;
		} else if (node.isRoot()) {
			return false;
		} else {
			return getParent().isDecendantOf(node);
		}
	}

	// Depth Methods
	public void setDepth(int depth) {this.depth = depth;}
	public int getDepth() {return depth;}
	
	public void setDepthRecursively(int depth) {
		setDepth(depth);
		for (int i = 0; i < numOfChildren(); i++) {
			getChild(i).setDepthRecursively(depth + 1);
		}						
	}
	
	// Navigation Methods	
	public void setExpandedClean(boolean expanded) {
		this.expanded = expanded;
	}
	
	public void setExpanded(boolean expanded) {
		if (expanded == isExpanded()) {
			// Since we have not changed state, Abort.
			return;
		}
		
		this.expanded = expanded;
		
		if (isExpanded()) {
			for (int i = this.numOfChildren() - 1; i >= 0; i--) {
				tree.insertNodeAfter(this,getChild(i));
			}				
		} else {
			for (int i = 0; i < this.numOfChildren(); i++) {
				Node child = getChild(i);
				tree.removeNode(child);
				if (child.isExpanded()) {
					child.setExpanded(false);
				}
			}		
		}
	}
	
	public boolean isExpanded() {return this.expanded;}

	public void ExpandAllSubheads() {
		setExpanded(true);
		for (int i = 0; i < this.numOfChildren(); i++) {
			getChild(i).ExpandAllSubheads();
		}				
	}

	public void CollapseAllSubheads() {
		setExpanded(false);
		for (int i = 0; i < this.numOfChildren(); i++) {
			getChild(i).CollapseAllSubheads();
		}				
	}

	public void expandAllAncestors() {
		if (getParent().isRoot()) {
			return;
		}
		getParent().setExpandedClean(true);
		getParent().expandAllAncestors();
	}

	public int currentIndex() {
		if (getParent() == null) {
			return -1;
		} else {
			return getParent().getChildIndex(this);
		}
	}
	
	public boolean isFirstChild() {
		if (currentIndex() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isLastChild() {
		if (currentIndex() == (getParent().numOfChildren() - 1)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Node nextSibling() {
		if (getParent() == null) {
			return this;
		}
		
		Node node = getParent().getChild(currentIndex() + 1);
		if(node == null) {
			return this;
		} else {
			return node;
		}
	}
	
	public Node prevSibling() {
		if (getParent() == null) {
			return this;
		}

		Node node = getParent().getChild(currentIndex() - 1);
		if (node == null) {
			return this;
		} else {
			return node;
		}	
	}
	
	public Node next() {
		if (isExpanded() && !isLeaf()) {
			return getChild(0);
		} else {
			Node node = nextSibling();
			if (node == this) {
				return nextSiblingOfAnyParent(node);
			} else {
				return node;
			}
		}
	}

	public Node nextUnSelectedNode() {
		// This does not test the current node.
		Node node = next();
		if (node.isAncestorSelected()) {
			return node.nextUnSelectedNode();
		} else {
			return node;
		}
	}

	public Node nextNode() {
		if (!isLeaf()) {
			return getChild(0);
		} else {
			Node node = nextSibling();
			if (node == this) {
				return nextSiblingOfAnyParent(node);
			} else {
				return node;
			}
		}
	}
	
	private static Node nextSiblingOfAnyParent(Node node) {
		if (node.getParent() == null) {
			return node;
		} else {
			Node nextSiblingOfParent = node.getParent().nextSibling();
			if (node.getParent() == nextSiblingOfParent) {
				return nextSiblingOfAnyParent(node.getParent());
			} else {
				return nextSiblingOfParent;
			}
		}
	}
	
	public Node prev() {
		Node node = prevSibling();
		if (node == this) {
			if (getParent() == null) {
				return node;
			} else {
				return getParent();
			}
		} else {
			return node.getLastViewableDecendent();
		}
	}

	public Node prevUnSelectedNode() {
		// This does not test the current node.
		Node node = prev();
		if (node.isAncestorSelected()) {
			return node.prevUnSelectedNode();
		} else {
			return node;
		}
	}
	
	// Data Methods
	public void setValue(String value) {this.value = value;}
	public String getValue() {return value;}

	// String Representation Methods
	public String depthPaddedValue() {
		return depthPaddedValue(Preferences.platformToLineEnding(Preferences.LINE_END.cur));
	}
	
	public String depthPaddedValue(String lineEndString) {
		StringBuffer retVal = new StringBuffer();
		
		if (!isRoot()) {
			for (int i = 0; i < this.depth; i++) {
				retVal.append(Preferences.DEPTH_PAD_STRING);
			}
			retVal.append(getValue()).append(lineEndString);
		}
		
		// Recursive Part
		for (int i = 0; i < this.numOfChildren(); i++) {
			retVal.append(this.getChild(i).depthPaddedValue(lineEndString));
		}
		
		return retVal.toString();
	}
}