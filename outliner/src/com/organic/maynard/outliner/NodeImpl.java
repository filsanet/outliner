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
	private HashMap attributes = null;
	// IMPROVE: Could children be initially null and do lazy instantiation. This could save memory.
	private ArrayList children = new ArrayList(10);

	private int depth = -1; // -1 so that children of root will be depth 0.
	
	private boolean expanded = false;
	private boolean visible = false;
	private boolean partiallyVisible = false;
	private boolean selected = false;
	//private boolean comment = false;
	private int commentState = Node.COMMENT_INHERITED;
	private boolean hoisted = false;
	
	private int decendantCount = 0;
	private int decendantCharCount = 0;
	

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
	
	public int getLineNumber() {
		return getLineNumber(-2);
	}
	
	public int getLineNumber(int key) {
		if (key == lineNumberUpdateKey) {
			return lineNumber;
		} else {
		
			Node next = prevSiblingOrParent();
			
			if (next == this) {
				lineNumber = 1;
			} else if (this.getParent() == next) {
				lineNumber = 1 + next.getLineNumber(key);
			} else {
				lineNumber = 1 + next.getDecendantCount() + next.getLineNumber(key);
			}
			
			lineNumberUpdateKey = key;
			
			return lineNumber;
		}
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
		
		// Adjust Counts
		adjustDecendantCount(node.getDecendantCount() + 1);
	}
	
	public void removeChild(Node node) {
		node.setParent(null);
		children.remove(node);

		// Adjust Counts
		adjustDecendantCount(-(node.getDecendantCount() + 1));
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

		// Adjust Counts
		adjustDecendantCount(node.getDecendantCount() + 1);
		//adjustDecendantCharCount(node.getDecendantCharCount() + node.getValue().length());
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
	public void setCommentState(int commentState) {
		this.commentState = commentState;
	}

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

	public void setAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new HashMap();
		}
		attributes.put(key, value);
	}

	public void removeAttribute(String key) {
		if (attributes != null && key != null) {
			attributes.remove(key);
		}
	}
	
	public Object getAttribute(String key) {
		if (attributes != null && key != null) {
			return attributes.get(key);
		}
		return null;
	}
	
	public int getAttributeCount() {
		if (attributes != null) {
			return attributes.size();
		}
		return 0;
	}
	
	public Iterator getAttributeKeys() {
		if (attributes != null) {
			return attributes.keySet().iterator();
		}
		return null;
	}


	// String Representation Methods	
	public void depthPaddedValue(StringBuffer buf, String lineEndString) {
		//StringBuffer retVal = new StringBuffer();
		
		if (!isRoot()) {
			for (int i = 0; i < this.depth; i++) {
				buf.append(Preferences.DEPTH_PAD_STRING);
			}
			buf.append(getValue()).append(lineEndString);
		}
		
		// Recursive Part
		for (int i = 0; i < this.numOfChildren(); i++) {
			this.getChild(i).depthPaddedValue(buf, lineEndString);
		}
		
		//return retVal.toString();
	}
	
	public void getMergedValue(StringBuffer buf) {
		buf.append(getValue());
		
		// Recursive Part
		for (int i = 0; i < this.numOfChildren(); i++) {
			this.getChild(i).getMergedValue(buf);
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
		for (int i = 0; i < this.numOfChildren(); i++) {
			this.getChild(i).getMergedValue(buf);
		}
	}
}