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