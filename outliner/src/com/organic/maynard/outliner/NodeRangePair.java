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

public class NodeRangePair {
	
	public Node node = null;
	public int startIndex = 0;
	public int endIndex = 0;
	public boolean loopedOver = false;
		
	// The Constructors
	public NodeRangePair(Node node, int startIndex, int endIndex) {
		this(node,startIndex,endIndex,false);
	}
	
	public NodeRangePair(Node node, int startIndex, int endIndex, boolean loopedOver) {
		this.node = node;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.loopedOver = loopedOver;
	}	
}