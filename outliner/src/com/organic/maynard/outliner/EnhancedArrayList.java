/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

public class NodeList {

	// Fields
	private Node nodes[];
	private int size;

	// Constructors
	public NodeList() {
		this(10);
	}
   
	public NodeList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
		}
		this.nodes = new Node[initialCapacity];
	}


	// Accessors
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Node get(int index) {
		RangeCheck(index);
		return nodes[index];
	}

	public void set(int index, Node node) {
		RangeCheck(index);
		nodes[index] = node;
	}

	public void add(Node node) {
		ensureCapacity(size + 1);
		nodes[size++] = node;
	}
	
	public void add(int index, Node node) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		ensureCapacity(size + 1);
		System.arraycopy(nodes, index, nodes, index + 1, size - index);
		nodes[index] = node;
		size++;
	}

	public void remove(int index) {
		RangeCheck(index);

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(nodes, index + 1, nodes, index, numMoved);
			nodes[--size] = null; 
		}
	}

	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(nodes, toIndex, nodes, fromIndex, numMoved);

		int newSize = size - (toIndex - fromIndex);
		while (size != newSize) {
			nodes[--size] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			nodes[i] = null;
		}

		size = 0;
	}

	// Index Of
	public boolean contains(Node node) {
		return indexOf(node) >= 0;
	}

	public int indexOf(Node node) {
		return firstIndexOf(node);
	}

	public int firstIndexOf(Node node) {
		for (int i = 0; i < size; i++) {
			if (nodes[i] == node) {
				return i;
			}
		}
		
		return -1;
	}

	public int lastIndexOf(Node node) {
		for (int i = size - 1; i >= 0; i--) {
			if (nodes[i] == node) {
				return i;
			}
		}
		
		return -1;
	}
	
	// indexOf(int start, int end)

	// Misc Methods
	private void RangeCheck(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	public void ensureCapacity(int minCapacity) {
		int oldCapacity = nodes.length;
		
		if (minCapacity > oldCapacity) {
			Node oldData[] = nodes;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			nodes = new Node[newCapacity];
			System.arraycopy(oldData, 0, nodes, 0, size);
		}
	}
}