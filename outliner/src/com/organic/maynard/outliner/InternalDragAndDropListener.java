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

import javax.swing.*;

public class InternalDragAndDropListener implements MouseListener {

	private static final int ICON = 0;
	private static final int TEXT = 1;
	private static final int OTHER = -1;
	
	protected boolean isDragging = false;
	protected Node targetNode = null;
	protected int componentType = OTHER;
	
	protected OutlinerCellRendererImpl currentRenderer = null;
	protected OutlinerCellRendererImpl prevRenderer = null;
	
	// The Constructor
	public InternalDragAndDropListener() {}
	
	public void destroy() {
		targetNode = null;
		currentRenderer = null;
		prevRenderer = null;
	}
	
	
	// MouseListener Interface
	public void mouseEntered(MouseEvent e) {
		if (isDragging) {
			//System.out.println("DND Mouse Entered: " + e.paramString());
			
			componentType = getUIComponents(e.getSource());
			targetNode = getNodeFromSource(e.getSource());
			
			// Update the UI
			if (!targetNode.isAncestorSelected()) {
				if (componentType == ICON) {
					currentRenderer.button.setIcon(OutlineButton.ICON_DOWN_ARROW);
				} else if (componentType == TEXT) {
					currentRenderer.button.setIcon(OutlineButton.ICON_SE_ARROW);
				}
			} else if (targetNode.isSelected() && (componentType == TEXT) && (!targetNode.prevSibling().isSelected())) {
				OutlineLayoutManager layout = targetNode.getTree().doc.panel.layout;
				OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
				if (renderer != null) {
					renderer.button.setIcon(OutlineButton.ICON_SE_ARROW);
				}
			}
		}
	}
	
	public void mouseExited(MouseEvent e) {
		if (isDragging) {
			// Update the UI
			if (targetNode.isSelected() && !targetNode.isFirstChild() && (componentType == TEXT)) {
				OutlineLayoutManager layout = targetNode.getTree().doc.panel.layout;
				OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
				if (renderer != null) {
					renderer.button.updateIcon();
				}
			} else {
				currentRenderer.button.updateIcon();
			}
			
			// Update targetNode
			targetNode = null;
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if (e.isConsumed()) {
			return;
		}
		
		// Initiate Drag and Drop
		targetNode = getNodeFromSource(e.getSource());
		componentType = getUIComponents(e.getSource());
				
		if ((componentType == ICON) && targetNode.isSelected()) {
			isDragging = true;
		} else {
			reset();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isDragging) {
			//System.out.println("DND Mouse Released: " + e.paramString());
			
			// Handle the drop
			currentRenderer.button.updateIcon();
			
			if (targetNode != null) {
				if (!targetNode.isAncestorSelected()) {
					if (componentType == ICON) {
						moveAsOlderSibling();
					} else if (componentType == TEXT) {
						moveAsFirstChild();
					}
				} else if (targetNode.isSelected() && !targetNode.isFirstChild() && (componentType == TEXT)) {
					OutlineLayoutManager layout = targetNode.getTree().doc.panel.layout;
					OutlinerCellRendererImpl renderer = layout.getUIComponent(targetNode.prevSibling());
					if (renderer != null) {
						renderer.button.updateIcon();
					}
					
					targetNode = targetNode.prevSibling();
					moveAsFirstChild();
				}
			}
			
			// Terminate Drag and Drop
			reset();
		}
	}

	public void mouseClicked(MouseEvent e) {
		//System.out.println("DND Mouse Clicked: " + e.paramString());
	}


	// Utility Methods
	private void moveAsOlderSibling() {
		TreeContext tree = targetNode.getTree();

		// Put the Undoable onto the UndoQueue
		CompoundUndoableMove undoable = new CompoundUndoableMove(tree.getSelectedNodesParent(),targetNode.getParent());
		int targetIndexAdj = 0;
		int currentIndexAdj = 0;
		
		for (int i = 0; i < tree.selectedNodes.size(); i++) {
			// Record the Insert in the undoable
			Node nodeToMove = tree.selectedNodes.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}
		
			int currentIndex = nodeToMove.currentIndex();
			int targetIndex = targetNode.currentIndex();
			
			if (nodeToMove.getParent() == targetNode.getParent()) {
				if (currentIndex > targetIndex) {
					targetIndexAdj++;
					targetIndex += targetIndexAdj;
				} else if (currentIndex < targetIndex) {
					currentIndex += currentIndexAdj;
					currentIndexAdj--;
				}
			} else {
				targetIndexAdj++;
				targetIndex += targetIndexAdj;
				currentIndex += currentIndexAdj;
				currentIndexAdj--;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, currentIndex, targetIndex));
		}
		
		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
	private void moveAsFirstChild() {
		TreeContext tree = targetNode.getTree();

		CompoundUndoableMove undoable = new CompoundUndoableMove(tree.getSelectedNodesParent(),targetNode);
		int currentIndexAdj = 0;
		
		for (int i = tree.selectedNodes.size() - 1; i >= 0; i--) {
			Node nodeToMove = tree.selectedNodes.get(i);

			// Abort if node is not moveable
			if (!nodeToMove.isMoveable()) {
				continue;
			}

			int currentIndex = nodeToMove.currentIndex();
			int targetIndex = 0;
			
			if (nodeToMove.getParent() == targetNode) {
				currentIndex += currentIndexAdj;
				currentIndexAdj++;
			}
			
			undoable.addPrimitive(new PrimitiveUndoableMove(undoable, nodeToMove, currentIndex, targetIndex));
		}
		
		if (!undoable.isEmpty()) {
			tree.doc.undoQueue.add(undoable);
			undoable.redo();
		}
	}
	
		
	private Node getNodeFromSource(Object source) {
		if (source instanceof OutlinerCellRendererImpl) {
			return ((OutlinerCellRendererImpl) source).node;
		} else if (source instanceof OutlineButton) {
			return ((OutlineButton) source).renderer.node;
		} else if (source instanceof OutlineLineNumber) {
			return ((OutlineLineNumber) source).renderer.node;
		} else if (source instanceof OutlineCommentIndicator) {
			return ((OutlineCommentIndicator) source).renderer.node;
		} else {
			return null;
		}
	}

	private int getUIComponents(Object source) {
		if (source instanceof OutlinerCellRendererImpl) {
			prevRenderer = currentRenderer;
			currentRenderer = (OutlinerCellRendererImpl) source;
			return TEXT;
		} else if (source instanceof OutlineButton) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineButton) source).renderer;
			return ICON;
		} else if (source instanceof OutlineLineNumber) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineLineNumber) source).renderer;
			return ICON;
		} else if (source instanceof OutlineCommentIndicator) {
			prevRenderer = currentRenderer;
			currentRenderer = ((OutlineCommentIndicator) source).renderer;
			return ICON;
		} else {
			// Something went wrong.
			return OTHER;
		}
	}
	
	private void reset() {
		isDragging = false;
		targetNode = null;
		componentType = OTHER;
	
		currentRenderer = null;
		prevRenderer = null;
	}
}