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

public class UndoableEdit implements Undoable {

	// Fields
	private Node node = null;

	private String newText = "";
	private String oldText = "";

	private int newPosition = 0;
	private int oldPosition = 0;

	private int newMarkPosition = 0;
	private int oldMarkPosition = 0;
	
	private boolean frozen = false;


	// The Constructors
	public UndoableEdit(
		Node node, 
		String oldText, 
		String newText, 
		int oldPosition, 
		int newPosition, 
		int oldMarkPosition, 
		int newMarkPosition) 
	{
		this.node = node;
		this.newText = newText;
		this.oldText = oldText;
		this.newPosition = newPosition;
		this.oldPosition = oldPosition;
		this.newMarkPosition = newMarkPosition;
		this.oldMarkPosition = oldMarkPosition;
	}
	
	public void destroy() {
		node = null;
		newText = null;
		oldText = null;
	}
	
	// Accessors
	public void setNode(Node node) {this.node = node;}
	public Node getNode() {return this.node;}

	public void setNewText(String newText) {this.newText = newText;}
	public String getNewText() {return this.newText;}

	public void setNewPosition(int newPosition) {this.newPosition = newPosition;}
	public int getNewPosition() {return this.newPosition;}

	public void setNewMarkPosition(int newMarkPosition) {this.newMarkPosition = newMarkPosition;}
	public int getNewMarkPosition() {return this.newMarkPosition;}
	
	public void setFrozen(boolean frozen) {this.frozen = frozen;}
	public boolean isFrozen() {return frozen;}
	
	// Undoable Interface
	public void undo() {
		TreeContext tree = node.getTree();
		
		node.setValue(oldText);
		tree.setCursorPosition(oldPosition);
		tree.doc.setPreferredCaretPosition(oldPosition);
		tree.setCursorMarkPosition(oldMarkPosition);
		tree.setEditingNode(node);
		tree.clearSelection();
		tree.insertNode(node); // Used for visibility
		
		tree.doc.panel.layout.draw(node, OutlineLayoutManager.TEXT);
	}
	
	public void redo() {
		TreeContext tree = node.getTree();

		node.setValue(newText);
		tree.setCursorPosition(newPosition);
		tree.doc.setPreferredCaretPosition(newPosition);
		tree.setCursorMarkPosition(newMarkPosition);
		tree.setEditingNode(node);
		tree.clearSelection();
		tree.insertNode(node); // Used for visibility
		
		tree.doc.panel.layout.draw(node, OutlineLayoutManager.TEXT);
	}


	// Static Methods
	public static void freezeUndoEdit(Node currentNode) {
		UndoableEdit undoable = currentNode.getTree().doc.undoQueue.getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode)) {
			undoable.setFrozen(true);
		}
	}
}