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

import com.organic.maynard.util.string.*;

public class PadSelection {
	
	// Constants
	public static final int FAILURE = 0;
	public static final int SUCCESS = 1;
	public static final int SUCCESS_MODIFIED = 2;
	
	// The Constructors
	public PadSelection() {}

	// This method provides backwards compatibility but is now depricated.
	public static Node pad(String text, TreeContext tree, int targetDepth, String lineEndString) {
		Node tempRoot = new NodeImpl(tree,"");

		int success = pad(text, tree, targetDepth, lineEndString, tempRoot);
		
		return tempRoot;
	}
	
	private static int padRetVal = FAILURE;
	
	// This code should be rewritten to use instances of a PadSelection object so that there can be
	// one object per thread. Synchonizing it is a cheap short term fix.
	public synchronized static int pad(String text, TreeContext tree, int targetDepth, String lineEndString, Node tempRoot) {
		padRetVal = SUCCESS;
		
		tempRoot.setDepth(targetDepth - 1);
		
		// Break the text up into lines
		ArrayList nodes = new ArrayList(500);
		int shallowest = -1;
		
		StringSplitter splitter = new StringSplitter(text,lineEndString);
		while (splitter.hasMoreElements()) {
			String line = (String) splitter.nextElement();
			
			int depth = Count.startsWith(line,Preferences.DEPTH_PAD_STRING);
			line = Replace.replace(line, Preferences.DEPTH_PAD_STRING, "");
			
			// Record Shallowest
			if ((depth < shallowest) || (shallowest == -1)) {shallowest = depth;}
			
			Node node = new NodeImpl(tree,line);
			node.setDepth(depth);
			nodes.add(node);
		}
		
		// Normalize depths to the target depth based on shallowest
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) nodes.get(i);
			node.setDepth((node.getDepth() - shallowest) + targetDepth);
		}
		
		// Pad based on targetDepth and the preceeding line
		// and build the tree as we go.
		int previousDepth = targetDepth;
		Node previousNode = null;
		
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) nodes.get(i);
			if (node.getDepth() == previousDepth) {
				if (previousNode != null) {
					previousNode.getParent().appendChild(node);
				} else {
					tempRoot.appendChild(node);
				}
			} else if (node.getDepth() < previousDepth) {
				Node parent = getParentNodeOfDepth(previousNode,node.getDepth()).getParent();
				parent.appendChild(node);			
			} else if (node.getDepth() > previousDepth) {
				appendChildPaddedForDepth(previousNode,node,tree,tempRoot);				
			}
			previousDepth = node.getDepth();
			previousNode = node;				
		}
		
		return padRetVal;
	}
	
	private static Node getParentNodeOfDepth(Node node, int depth) {
		if (node.getDepth() == depth) {return node;}
		
		Node parentNode = node.getParent();
		return getParentNodeOfDepth(parentNode,depth);
	}
	
	private static void appendChildPaddedForDepth(Node parentNode, Node childNode, TreeContext tree, Node tempRoot) {
		if (parentNode == null) { //This should only happen when we are at the top of the tree.
			Node newNode = new NodeImpl(tree,"");
			newNode.setDepth(0);
			tempRoot.appendChild(newNode);
			appendChildPaddedForDepth(newNode,childNode,tree,tempRoot);
			return;
		} else if ((parentNode.getDepth() + 1) == childNode.getDepth()) {
			parentNode.appendChild(childNode);
			return;
		} else {
			Node newNode = new NodeImpl(tree,"");
			newNode.setDepth(parentNode.getDepth() + 1);
			parentNode.appendChild(newNode);
			appendChildPaddedForDepth(newNode,childNode,tree,tempRoot);
			padRetVal = SUCCESS_MODIFIED;
			return;
		}
	}	
}