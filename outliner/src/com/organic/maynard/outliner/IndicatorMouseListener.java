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

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;

import com.organic.maynard.util.string.*;

public class IndicatorMouseListener implements MouseListener {

	// Instance Fields
	private OutlinerCellRendererImpl textArea = null;

	// The Constructors
	public IndicatorMouseListener() {}
	
	public void destroy() {
		textArea = null;
	}	

	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		Component c = e.getComponent();
		if (c instanceof OutlineCommentIndicator) {
			textArea = ((OutlineCommentIndicator) c).renderer;
			Node node = textArea.node;
 			TreeContext tree = textArea.node.getTree();
			
			if (e.isControlDown()) {
				if (e.isShiftDown()) {
					clearComment(tree);
				} else {
					toggleCommentInheritance(tree);
				}
			} else if (e.isShiftDown()) {
				toggleComment(tree);
			} else {
				toggleCommentAndClear(tree);
			}			

			// Redraw and set focus
			tree.doc.panel.layout.draw();
			tree.doc.panel.layout.setFocus(tree.getEditingNode(), tree.getComponentFocus());

		}
	}
	
	private void clearComment(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.clearCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleCommentAndClear(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentAndClearForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleComment(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}

	private void toggleCommentInheritance(TreeContext tree) {
		Node currentNode = textArea.node;
		CompoundUndoablePropertyChange undoable = new CompoundUndoablePropertyChange(tree);
		IconKeyListener.toggleCommentInheritanceForSingleNode(currentNode, undoable);

		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
		}
	}
}