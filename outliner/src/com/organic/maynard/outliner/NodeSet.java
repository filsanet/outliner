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
import java.io.*;
import java.awt.datatransfer.*;

public class NodeSet implements Cloneable, Transferable {
	
	private DataFlavor[] dataFlavors = new DataFlavor[1];
	private ArrayList nodes = new ArrayList();
			
	// The Constructors
	public NodeSet() {
		dataFlavors[0] = new NodeSetDataFlavor(this);
	}
	
	
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
	
	// Transferable Interface
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return clone();
	}

	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavors;
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i = 0; i < dataFlavors.length; i++) {
			DataFlavor dataFlavor = dataFlavors[i];
			if (dataFlavor.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	
	// Overridden Methods
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) nodes.get(i);
			node.depthPaddedValue(buf,  Preferences.LINE_END_STRING);
		}
		
		return buf.toString();
	}	
}

class NodeSetDataFlavor extends DataFlavor {
	
	// The Constructors
	public NodeSetDataFlavor(NodeSet nodeSet) {
		super(nodeSet.getClass(), "NodeSet");
	}
	
	public void writeExternal(ObjectOutput os) {
		String s = new String("ack");
		try {
			os.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}