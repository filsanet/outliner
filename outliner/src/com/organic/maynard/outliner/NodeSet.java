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
import java.util.*;
import java.io.*;
import java.awt.datatransfer.*;

public class NodeSet implements Cloneable {

	private ArrayList nodes = new ArrayList();

	// Good Info: http://developer.java.sun.com/developer/bugParade/bugs/4066902.html
		

	// The Constructors
	public NodeSet() {}
	
	
	// Accessors
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public Node getNode(int i) {
		return (Node) nodes.get(i);
	}
	
	public void removeNode(int i) {
		nodes.remove(i);
	}
	
	public int getSize() {
		return nodes.size();
	}
	
	public boolean isEmpty() {
		if (getSize() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// Cloneable Interface
	public Object clone() {
		NodeSet nodeSet = new NodeSet();
		
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) nodes.get(i);
			Node newNode = node.cloneClean();
			
			nodeSet.addNode(newNode);
		}
		
		return nodeSet;
	}

	
	// Overridden Methods
	
	// turn the node set into a string
	public String toString() {
		
		// a buffer to hold the string		[sk]
		StringBuffer buf = new StringBuffer();
		
		// for each node ...		[sk]
		for (int i = 0; i < nodes.size(); i++) {
			
			// grab the node		[sk]
			Node node = (Node) nodes.get(i);
			
			// Since a node may be a root node, 
			// and depthPaddedValue doesn't throw in root level text,
			// let's put it back in.
			if (node.isRoot()) {
				
				// for each level of depth ...		[sk]
				for (int j = 0; j < node.getDepth(); j++) {
					
					// add a depth padding string to the buffer		[sk]
					buf.append(Preferences.DEPTH_PAD_STRING);
					
					} // end for
				
				// add the root node's text, and a line-ending string, to the buffer		[sk]
				buf.append(node.getValue()).append(Preferences.LINE_END_STRING);
				
				} // end if the node's a root
			
			// for both root nodes and not-root-nodes: append the not-root nodes' text		[sk]
			node.depthPaddedValue(buf,  Preferences.LINE_END_STRING);
		
			} // end for each node
		
		// return the string		[sk]
		return buf.toString();
	
		}  // end method toString
		
	} // end class NodeSet

// non-public class for transferring node sets
class NodeSetTransferable extends StringSelection implements Transferable {
	
	private NodeSet nodeSet = null;
	
	public static DataFlavor nsFlavor;
	
	static {
		try {
			nsFlavor = new DataFlavor(Class.forName("com.organic.maynard.outliner.NodeSet"), "NodeSet");
		} catch (ClassNotFoundException ex) {}
	}
	
	private static final int STRING = 0;
	private static final int NODESET = 1;
	
	private DataFlavor[] flavors = {
		DataFlavor.stringFlavor,
		nsFlavor
	};
	
	
	// The Constructors
	public NodeSetTransferable(NodeSet nodeSet) {
		super(nodeSet.toString());
		this.nodeSet = nodeSet;
	}


	// Transferable Interface
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
			flavor.equals(flavors[STRING]) || 
			flavor.equals(flavors[NODESET])
		);    
	}
    
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavors[STRING])) {
			return nodeSet.toString();
			
		} else if (flavor.equals(flavors[NODESET])) {
			return nodeSet.clone();
			
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}