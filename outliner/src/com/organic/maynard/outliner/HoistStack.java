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

public class HoistStack {

	// Instance Variables
	private OutlinerDocument doc = null;
	
	private Stack stack = new Stack();
	
	
	// The Constructor
	public HoistStack(OutlinerDocument doc) {
		this.doc = doc;
	}
	
	public void destroy() {
		stack = null;
	}
	
	
	// Methods
	public void clear() {
		stack.clear();
		
		// Update the MenuBar
		updateOutlinerMenuHoisting();
	}
	
	public int getHoistDepth() {
		return this.stack.size();
	}
	
	public boolean isHoisted() {
		if (getHoistDepth() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getLineCountOffset() {
		int offset = 0;
		for (int i = 0; i < stack.size(); i++) {
			offset += ((HoistStackItem) stack.get(i)).getLineCountOffset();
		}

		return offset;
	}
	
	protected synchronized void temporaryHoistAll() {
		for (int i = 0; i < stack.size(); i++) {
			((HoistStackItem) stack.get(i)).hoist();
		}
	}
	
	protected synchronized void temporaryDehoistAll() {
		for (int i = stack.size() - 1; i >= 0; i--) {
			((HoistStackItem) stack.get(i)).dehoist();
		}
	}
	
	public void hoist(HoistStackItem item) {
		Node currentNode = item.getNode();
		if (!currentNode.isLeaf() && !currentNode.isHoisted()) {

			// Shorthand
			TreeContext tree = currentNode.getTree();
			outlineLayoutManager layout = tree.doc.panel.layout;

			// Clear the undoQueue
			if (!doc.undoQueue.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(doc, "Hoisting requires that the undo queue be cleared.\nProceed anyway?","",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.undoQueue.clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}	

			// Do the hoist
			item.hoist();

			// Update Selection
			tree.setSelectedNodesParent(currentNode);
			tree.addNodeToSelection(currentNode.getFirstChild());
			
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(currentNode.getFirstChild());
			tree.setCursorPosition(0);
			tree.setComponentFocus(outlineLayoutManager.ICON);
		
			// Throw it onto the stack
			stack.push(item);

			// Redraw and Set Focus
			Node nodeToDrawFrom = currentNode.getFirstChild();
			int ioNodeToDrawFrom = tree.visibleNodes.indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
	
			layout.draw();
			layout.setFocus(nodeToDrawFrom, outlineLayoutManager.ICON);
			
			// Update the MenuBar
			updateOutlinerMenuHoisting();

		}
		return;
	}
	
	public void dehoist() {
		if (isHoisted()) {
			
			// Shorthand
			TreeContext tree = doc.tree;
			outlineLayoutManager layout = doc.panel.layout;

			// Clear the undoQueue
			if (!doc.undoQueue.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(doc, "De-Hoisting requires that the undo queue be cleared.\nProceed anyway?","",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.undoQueue.clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}		

			// Remove it from the stack
			HoistStackItem item = (HoistStackItem) stack.pop();

			// Do the dehoist
			item.dehoist();

			// Update Selection
			tree.setSelectedNodesParent(item.getNodeParent());
			tree.addNodeToSelection(item.getNode());
	
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(item.getNode());
			tree.setCursorPosition(0);
			tree.setComponentFocus(outlineLayoutManager.ICON);
	
			// Redraw and Set Focus
			Node nodeToDrawFrom = item.getNode();
			int ioNodeToDrawFrom = tree.visibleNodes.indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
	
			layout.draw();
			layout.setFocus(nodeToDrawFrom, outlineLayoutManager.ICON);
			
			// Update the MenuBar
			updateOutlinerMenuHoisting();
		}
	}
	
	public void dehoistAll() {
		if (isHoisted()) {
		
			// Shorthand
			TreeContext tree = doc.tree;
			outlineLayoutManager layout = doc.panel.layout;

			// Clear the undoQueue
			if (!doc.undoQueue.isEmpty()) {
				int result = JOptionPane.showConfirmDialog(doc, "De-Hoisting requires that the undo queue be cleared.\nProceed anyway?","",JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					doc.undoQueue.clear();
				} else if (result == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}
				
			HoistStackItem item = null;
			
			while (isHoisted()) {
				// Remove it from the stack
				item = (HoistStackItem) stack.pop();
	
				// Do the dehoist
				item.dehoist();
			}

			// Update Selection
			tree.setSelectedNodesParent(item.getNodeParent());
			tree.addNodeToSelection(item.getNode());
	
			// Record the EditingNode and CursorPosition and ComponentFocus
			tree.setEditingNode(item.getNode());
			tree.setCursorPosition(0);
			tree.setComponentFocus(outlineLayoutManager.ICON);
	
			// Redraw and Set Focus
			Node nodeToDrawFrom = item.getNode();
			int ioNodeToDrawFrom = tree.visibleNodes.indexOf(nodeToDrawFrom);
			layout.setNodeToDrawFrom(nodeToDrawFrom, ioNodeToDrawFrom);
	
			layout.draw();
			layout.setFocus(nodeToDrawFrom, outlineLayoutManager.ICON);
			
			// Update the MenuBar
			updateOutlinerMenuHoisting();		
		}
	}
	
	public void updateOutlinerMenuHoisting() {
		if (isHoisted()) {
			Outliner.menuBar.outlineMenu.OUTLINE_DEHOIST_ITEM.setEnabled(true);
			Outliner.menuBar.outlineMenu.OUTLINE_DEHOIST_ALL_ITEM.setEnabled(true);
		} else {
			Outliner.menuBar.outlineMenu.OUTLINE_DEHOIST_ITEM.setEnabled(false);
			Outliner.menuBar.outlineMenu.OUTLINE_DEHOIST_ALL_ITEM.setEnabled(false);
		}
		Outliner.menuBar.outlineMenu.OUTLINE_HOIST_ITEM.setText(OutlineMenu.OUTLINE_HOIST + " (" + getHoistDepth() + ")");
	}
}






public class HoistStackItem {

	// Instance Variables
	private Node hoistedNode = null;
	private Node hoistedNodeParent = null;
	private int hoistedNodeIndex = -1;
	private int hoistedNodeDepth = -1;
	
	private Node oldNodeSet = null;
	private int lineCountOffset = 0;
		
	
	// The Constructor
	public HoistStackItem(Node hoistedNode) {
		this.hoistedNode = hoistedNode;
		this.hoistedNodeParent = hoistedNode.getParent();
		this.hoistedNodeIndex = hoistedNodeParent.getChildIndex(hoistedNode);
		this.hoistedNodeDepth = hoistedNode.getDepth();
		
		this.oldNodeSet = hoistedNode.getTree().getRootNode();
		this.lineCountOffset = hoistedNode.getLineNumber();
	}
	
	public void destroy() {
		hoistedNode = null;
	}
	
	
	// Accessors
	public Node getNode() {return this.hoistedNode;}
	public Node getNodeParent() {return this.hoistedNodeParent;}

	public Node getOldNodeSet() {return this.oldNodeSet;}
	public int getLineCountOffset() {return this.lineCountOffset;}
	
	// Methods
	public void dehoist() {
		// Shorthand
		TreeContext tree = hoistedNode.getTree();

		hoistedNode.setHoisted(false);

		// Prune things
		tree.setRootNode(oldNodeSet);
		tree.visibleNodes.clear();
		hoistedNodeParent.insertChild(hoistedNode, hoistedNodeIndex);
		hoistedNode.setDepthRecursively(hoistedNodeDepth);
		for (int i = 0; i < oldNodeSet.numOfChildren(); i++) {
			Node node = oldNodeSet.getChild(i);
			tree.insertNode(node);
		}
				
		return;
	}
	
	public void hoist() {
		
		// Shorthand
		TreeContext tree = hoistedNode.getTree();
		
		hoistedNode.setHoisted(true);
		
		// Prune things
		hoistedNode.getParent().removeChild(hoistedNode);
		hoistedNode.setDepthRecursively(-1);
		tree.setRootNode(hoistedNode);
		tree.visibleNodes.clear();
		for (int i = 0; i < hoistedNode.numOfChildren(); i++) {
			Node node = hoistedNode.getChild(i);
			tree.insertNode(node);
		}
		
		return;
	}
}