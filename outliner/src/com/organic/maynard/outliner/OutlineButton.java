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

public class OutlineButton extends JLabel {

	// Class Fields
	public static final ImageIcon ICON_OPEN_NODE = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "open_node.gif");
	public static final ImageIcon ICON_OPEN_NODE_SELECTED = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "open_node_selected.gif");
	public static final ImageIcon ICON_CLOSED_NODE = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "closed_node.gif");
	public static final ImageIcon ICON_CLOSED_NODE_SELECTED = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "closed_node_selected.gif");
	public static final ImageIcon ICON_LEAF = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "leaf.gif");
	public static final ImageIcon ICON_LEAF_SELECTED = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "leaf_selected.gif");

	public static final ImageIcon ICON_DOWN_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "down_arrow.gif");
	public static final ImageIcon ICON_SE_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "se_arrow.gif");
	//public static final ImageIcon ICON_RIGHT_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + System.getProperty("file.separator") + "right_arrow.gif");

	public static int BUTTON_WIDTH = 15;
	public static int BUTTON_HEIGHT = 15;
	
	static {
		BUTTON_WIDTH = ICON_OPEN_NODE.getIconWidth();
		BUTTON_HEIGHT = ICON_OPEN_NODE.getIconHeight();
	}
	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean node = true;
	private boolean open = true;
	private boolean selected = false;
	
	// The Constructor
	public OutlineButton(OutlinerCellRendererImpl renderer) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		setOpaque(true);
		setVisible(false);

		updateIcon();
	}
	
	public void destroy() {
		removeAll();
		
		renderer = null;
		
	}
	
	public boolean isManagingFocus() {return true;}

	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}
	
	public boolean isNode() {return node;}
	public void setNode(boolean node) {
		if (this.node != node) {
			this.node = node;
			updateIcon();
		}
	}
	
	public boolean isOpen() {return open;}
	public void setOpen(boolean open) {
		if (this.open != open) {
			this.open = open;
			updateIcon();
		}
	}
	
	public boolean isSelected() {return selected;}
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			updateIcon();
		}
	}

	public void updateIcon() {
		if(isNode()) {
			if(isOpen()) {
				if(isSelected()) {
					setIcon(ICON_OPEN_NODE_SELECTED);
				} else {
					setIcon(ICON_OPEN_NODE);
				}
			} else {
				if(isSelected()) {
					setIcon(ICON_CLOSED_NODE_SELECTED);
				} else {
					setIcon(ICON_CLOSED_NODE);
				}			
			}	
		} else {
			if(isSelected()) {
				setIcon(ICON_LEAF_SELECTED);
			} else {
				setIcon(ICON_LEAF);
			}
		}
	}
}