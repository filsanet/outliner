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

public class NodeImpl extends AttributeContainerImpl implements Node {
	
	// Instance Fields		
	private TreeContext tree = null;
	private Node parent = null;
	private String value = null;
	protected NodeList children = null;
	private static final int INITIAL_ARRAY_LIST_SIZE = 10;

	private int depth = -1; // -1 so that children of root will be depth 0.
	
	private boolean expanded = false;
	private boolean visible = false;
	private boolean partiallyVisible = false;
	private boolean selected = false;
	private boolean hoisted = false;

	private int commentState = Node.COMMENT_INHERITED;
	private int editableState = Node.EDITABLE_INHERITED;
	private int moveableState = Node.MOVEABLE_INHERITED;
	
	protected int decendantCount = 0;
	private int decendantCharCount = 0;
	

	// The Constructors
	public NodeImpl(TreeContext tree, String value) {
		this.tree = tree;
		this.value = value;
	}

	public void destroy() {
		if (children != null) {
			for (int i = 0; i < children.size(); i++) {
				Node child = children.get(i);
				child.destroy();
			}
		}
		
		tree = null;
		parent = null;
		value = null;
		children = null;
	}

	// Explicit Cloning Method
	public Node cloneClean() {
		NodeImpl nodeImpl = new NodeImpl(tree,value);
		
		nodeImpl.setDepth(depth);
		
		nodeImpl.setCommentState(commentState);
		
		// clone the attributes
		Iterator it = getAttributeKeys();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = getAttribute(key); // this should be a clone, but that's impossible!!! Eventually we'll need a copyable interface to make this work.
				nodeImpl.setAttribute(key, value);
			}
		}
		
		// And clone the children
		for (int i = 0; i < numOfChildren(); i++) {
			nodeImpl.insertChild(getChild(i).cloneClean(),i);
		}
		
		return nodeImpl;
	}
	

	// Statistics Methods
	private int lineNumber = -1;
	private int lineNumberUpdateKey = -1;
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setLineNumberKey(int lineNumberUpdateKey) {
		this.lineNumberUpdateKey = lineNumberUpdateKey;
	}
	
	public int getLineNumber() {
		// This for when we need to get a line number but we don't want the
		// key to get in the way. -2 will never be a normal key for things.
		return getLineNumber(-2);
	}
	
	public int getLineNumber(int key) {
		if (lineNumberUpdateKey == key) {
			return lineNumber;
		}
		
		Node next = tree.visibleNodes.get(0);
		int runningTotal = 0;
		
		int siblingCount = 0;
		
		while (true) {
			runningTotal++;

			next.setLineNumber(runningTotal);
			next.setLineNumberKey(key);
				
			if (this.isDecendantOf(next)) {
				if (next == this) {
					break;
				} else {
					siblingCount = 0;
					next = next.getChild(siblingCount);
				}
			} else {
				runningTotal += next.getDecendantCount();
				siblingCount++;
				next = next.getParent().getChild(siblingCount);
			}
		}
		
		return lineNumber;		
	}
	
	public void adjustDecendantCount(int amount) {
		decendantCount += amount;
		if (!isRoot()) {
			getParent().adjustDecendantCount(amount);
		}
	}
	
	public void adjustDecendantCharCount(int amount) {
		decendantCharCount += amount;
		if (!isRoot()) {
			getParent().adjustDecendantCharCount(amount);
		}
	}
	
	public int getDecendantCount() {
		return decendantCount;
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
	public int numOfChildren() {
		if (children != null) {
			return children.size();
		} else {
			return 0;
		}
	}
	
	public void appendChild(Node node) {
		if (children == null) {
			children = new NodeList(INITIAL_ARRAY_LIST_SIZE);
		}
		
		children.add(node);
		node.setParent(this);
		
		// Set the childs Depth
		node.setDepth(getDepth() + 1);
		
		// Insert the new child into the list of visible nodes if we are expanded.
		if (isExpanded()) {
			tree.insertNodeAfter(this, node);
		}
		
		// Adjust Counts
		adjustDecendantCount(node.getDecendantCount() + 1);
	}
	
	public void removeChild(Node node) {
		if (children == null) {
			return;
		}
		
		node.setParent(null);
		children.remove(children.indexOf(node));

		// Adjust Counts
		adjustDecendantCount(-(node.getDecendantCount() + 1));
	}

	public void removeChild(Node node, int index) {
		if (children == null) {
			return;
		}
		
		node.setParent(null);
		children.remove(index);

		// Adjust Counts
		adjustDecendantCount(-(node.getDecendantCount() + 1));
	}
		
	public Node getChild(int i) {
		if (children == null) {
			return null;
		}
		
		try {
			return children.get(i);
		} catch (IndexOutOfBoundsException iofbe) {
			return null;
		}
	}

	public Node getFirstChild() {
		if (children == null) {
			return null;
		}
	
		if (isLeaf()) {
			return null;
		} else {
			return children.get(0);
		}
	}
	
	public Node getLastChild() {
		if (children == null) {
			return null;
		}

		if (isLeaf()) {
			return null;
		} else {
			return children.get(children.size() - 1);
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
		Node parent = getParent();
		
		if (parent.isRoot()) {
			return parent;
		} else if (tree.visibleNodes.contains(parent)) {
			return parent;
		} else {
			return parent.getYoungestVisibleAncestor();
		}
	}
	
	// This method could be optomized better by first finding the range and then doing a batch insert.
	public int insertChildrenIntoVisibleNodesCache(int index) {
		if (isExpanded()) {
			int childrenCount = this.numOfChildren();
			for (int i = 0; i < childrenCount; i++) {
				index++;
				Node child = getChild(i);
				
				if (index < tree.visibleNodes.size()) {
					if (tree.visibleNodes.get(index) != child) {
						tree.visibleNodes.add(index,child);
					}
				} else {
					tree.visibleNodes.add(index,child);
				}
				index = child.insertChildrenIntoVisibleNodesCache(index);
			}		
		}
		return index;
	}
	
	public void insertChild(Node node, int i) {
		if (children == null) {
			children = new NodeList(INITIAL_ARRAY_LIST_SIZE);
		}

		children.add(i,node);
		node.setParent(this);

		// Adjust Counts
		adjustDecendantCount(node.getDecendantCount() + 1);
		//adjustDecendantCharCount(node.getDecendantCharCount() + node.getValue().length());
	}
	
	public int getChildIndex(Node node) {
		if (children == null) {
			return -1;
		}

		return children.indexOf(node);
	}
	
	public boolean isLeaf() {
		if (children == null) {
			return true;
		}

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
	
	public void setTree(TreeContext tree, boolean recursive) {
		this.tree = tree;
		if (recursive) {
			for (int i = 0; i < numOfChildren(); i++) {
				getChild(i).setTree(tree, true);
			}
		}
	}
	
	
	// Visibility Methods	
	public void setVisible(boolean visible) {this.visible = visible;}
	public boolean isVisible() {return visible;}

	// Comment Methods	
	public void setCommentState(int commentState) {this.commentState = commentState;}
	public int getCommentState() {return commentState;}
	
	public boolean isComment() {
		if (getCommentState() == Node.COMMENT_TRUE) {
			return true;
		} else if (getCommentState() == Node.COMMENT_FALSE) {
			return false;
		} else {
			if (isRoot()) {
				return getTree().getRootNodeCommentState();
			} else {
				return getParent().isComment();
			}
		}
	}

	// Editability Methods
	public void setEditableState(int editableState) {this.editableState = editableState;}
	public int getEditableState() {return editableState;}

	public boolean isEditable() {
		if (getEditableState() == Node.EDITABLE_TRUE) {
			return true;
		} else if (getEditableState() == Node.EDITABLE_FALSE) {
			return false;
		} else {
			if (isRoot()) {
				return getTree().getRootNodeEditableState();
			} else {
				return getParent().isEditable();
			}
		}
	}
	
	// Moveability Methods
	public void setMoveableState(int moveableState) {this.moveableState = moveableState;}
	public int getMoveableState() {return moveableState;}

	public boolean isMoveable() {
		if (getMoveableState() == Node.MOVEABLE_TRUE) {
			return true;
		} else if (getMoveableState() == Node.MOVEABLE_FALSE) {
			return false;
		} else {
			if (isRoot()) {
				return getTree().getRootNodeMoveableState();
			} else {
				return getParent().isMoveable();
			}
		}
	}
	
	// Hoisting Methods
	public void setHoisted(boolean hoisted) {this.hoisted = hoisted;}
	public boolean isHoisted() {return hoisted;}
	
	public Node getHoistedAncestorOrSelf() {
		if (isRoot()) {
			return null;
		} else if (isHoisted()) {
			return this;
		} else {
			return getParent().getHoistedAncestorOrSelf();
		}
	}

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
	
	// Is this a decendant of node?
	public boolean isDecendantOf(Node node) {
		if (this == node) {
			return true;
		} else if (isRoot()) {
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
		int childrenCount = numOfChildren();
		for (int i = 0; i < childrenCount; i++) {
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
		
		if (isLeaf()) {
			return;
		}
		
		if (isExpanded()) {
			int index = tree.visibleNodes.indexOf(this) + 1;
			for (int i = this.numOfChildren() - 1; i >= 0; i--) {
				//tree.insertNodeAfter(this,getChild(i));
				tree.insertNode(getChild(i), index);
			}				
		} else {
			int childCount = this.numOfChildren();
			for (int i = 0; i < childCount; i++) {
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
		Node parent = getParent();
		if (parent.isRoot()) {
			return;
		}
		parent.setExpandedClean(true);
		parent.expandAllAncestors();
	}

	public int currentIndex() {
		Node parent = getParent();
		if (parent == null) {
			return -1;
		} else {
			return parent.getChildIndex(this);
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
	
	public Node prevSiblingOrParent() {
		Node node = prevSibling();
		if (node == this) {
			node = getParent();
			if (node == null || node.isRoot()) {
				return this;
			}
		}
		return node;		
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
	public void depthPaddedValue(StringBuffer buf, String lineEndString) {
		if (!isRoot()) {
			for (int i = 0; i < this.depth; i++) {
				buf.append(Preferences.DEPTH_PAD_STRING);
			}
			buf.append(getValue()).append(lineEndString);
		}
		
		// Recursive Part
		for (int i = 0; i < numOfChildren(); i++) {
			getChild(i).depthPaddedValue(buf, lineEndString);
		}
	}

	public void getRecursiveValue(StringBuffer buf, String lineEndString, boolean includeComments) {

		if (includeComments || !isComment()) {
			buf.append(getValue()).append(lineEndString);
		}
		
		for (int i = 0; i < numOfChildren(); i++) {
			getChild(i).getRecursiveValue(buf, lineEndString, includeComments);
		}
	}
	
	public void getMergedValue(StringBuffer buf) {
		buf.append(getValue());
		
		// Recursive Part
		for (int i = 0; i < numOfChildren(); i++) {
			getChild(i).getMergedValue(buf);
		}
	}

	public void getMergedValueWithSpaces(StringBuffer buf) {
		if (buf.length() > 0) {
			if (buf.charAt(buf.length() - 1) != ' ') {
				buf.append(' ').append(getValue());
			} else {
				buf.append(getValue());
			}
		} else {
			buf.append(getValue());
		}
		
		// Recursive Part
		for (int i = 0; i < numOfChildren(); i++) {
			getChild(i).getMergedValue(buf);
		}
	}
}