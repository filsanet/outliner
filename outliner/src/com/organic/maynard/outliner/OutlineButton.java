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

import java.awt.image.*;
import java.awt.geom.*;

public class OutlineButton extends JLabel {

	// Class Fields
	public static final ImageIcon ICON_CLOSED_NODE = new ImageIcon(Outliner.GRAPHICS_DIR + "closed_node.gif");
	
	public static int BUTTON_WIDTH = ICON_CLOSED_NODE.getIconWidth();
	public static int BUTTON_HEIGHT = ICON_CLOSED_NODE.getIconHeight();
	
	public static ImageIcon ICON_OPEN_NODE = null;
	public static ImageIcon ICON_OPEN_NODE_SELECTED = null;
	public static ImageIcon ICON_CLOSED_NODE_SELECTED = null;

	public static ImageIcon ICON_LEAF = null;
	public static ImageIcon ICON_LEAF_SELECTED = null;
	
	// Note: icons are initialized by the createIcons() method below. This method
	// is called from Outliner during it's endSetup() method.

	public static final ImageIcon ICON_OPEN_NODE_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "open_node_commented.gif");
	public static final ImageIcon ICON_OPEN_NODE_SELECTED_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "open_node_selected_commented.gif");
	public static final ImageIcon ICON_CLOSED_NODE_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "closed_node_commented.gif");
	public static final ImageIcon ICON_CLOSED_NODE_SELECTED_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "closed_node_selected_commented.gif");
	public static final ImageIcon ICON_LEAF_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "leaf_commented.gif");
	public static final ImageIcon ICON_LEAF_SELECTED_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "leaf_selected_commented.gif");

	public static final ImageIcon ICON_DOWN_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + "down_arrow.gif");
	public static final ImageIcon ICON_SE_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + "se_arrow.gif");
	//public static final ImageIcon ICON_RIGHT_ARROW = new ImageIcon(Outliner.GRAPHICS_DIR + "right_arrow.gif");

	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean node = true;
	private boolean open = true;
	private boolean selected = false;
	private boolean comment = false;
	
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
	public void setNode(boolean node) {this.node = node;}
	
	public boolean isOpen() {return open;}
	public void setOpen(boolean open) {this.open = open;}
	
	public boolean isSelected() {return selected;}
	public void setSelected(boolean selected) {this.selected = selected;}

	public boolean isComment() {return comment;}
	public void setComment(boolean comment) {this.comment = comment;}

	public void updateIcon() {
		if(isNode()) {
			if(isOpen()) {
				if(isSelected()) {
					if(isComment()) {
						setIcon(ICON_OPEN_NODE_SELECTED_COMMENTED);
					} else {
						setIcon(ICON_OPEN_NODE_SELECTED);
					}
				} else {
					if(isComment()) {
						setIcon(ICON_OPEN_NODE_COMMENTED);
					} else {
						setIcon(ICON_OPEN_NODE);
					}
				}
			} else {
				if(isSelected()) {
					if(isComment()) {
						setIcon(ICON_CLOSED_NODE_SELECTED_COMMENTED);
					} else {
						setIcon(ICON_CLOSED_NODE_SELECTED);
					}
				} else {
					if(isComment()) {
						setIcon(ICON_CLOSED_NODE_COMMENTED);
					} else {
						setIcon(ICON_CLOSED_NODE);
					}
				}			
			}	
		} else {
			if(isSelected()) {
				if(isComment()) {
					setIcon(ICON_LEAF_SELECTED_COMMENTED);
				} else {
					setIcon(ICON_LEAF_SELECTED);
				}
			} else {
				if(isComment()) {
					setIcon(ICON_LEAF_COMMENTED);
				} else {
					setIcon(ICON_LEAF);
				}
			}
		}
	}
	
	// Static Methods
	public static void createIcons() {
		System.out.println("Start Creating Icons...");
		
		// Create a buffered image from the closed node image.
		Image closedImage = ICON_CLOSED_NODE.getImage();
		BufferedImage image = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(closedImage,0,0,Outliner.outliner);

		System.out.println("  prototype icon loaded: closedNode");

		// Create Buffered Image for the derived images.
		BufferedImage openImage = new BufferedImage(BUTTON_WIDTH, BUTTON_HEIGHT, image.getType());
		
		// Define a transforamtion to rotate the closed image to create the open image.
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getRotateInstance((java.lang.Math.PI)/2, BUTTON_WIDTH/2, BUTTON_HEIGHT/2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		at.filter(image, openImage);
		
		ICON_OPEN_NODE = new ImageIcon(openImage);
		System.out.println("  icon: openNode");
		
		// Lighten color to create leaf
		lightenFilter lightenFilter = new lightenFilter(0x00999999);
		FilteredImageSource leafSource = new FilteredImageSource(image.getSource(), lightenFilter);
		Image leafImage = Outliner.outliner.createImage(leafSource);

		ICON_LEAF = new ImageIcon(leafImage);
		System.out.println("  icon: leaf");

		// Darken color for selected leaf
		lightenFilter lightenFilter2 = new lightenFilter(0x00333333);
		FilteredImageSource leafSelectedSource = new FilteredImageSource(leafImage.getSource(), lightenFilter2);
		Image leafSelectedImage = Outliner.outliner.createImage(leafSelectedSource);

		ICON_LEAF_SELECTED = new ImageIcon(leafSelectedImage);
		System.out.println("  icon: leafSelected");
		
		// Invert color for selected images
		inversionFilter inversionFilter = new inversionFilter();
		FilteredImageSource openSource = new FilteredImageSource(openImage.getSource(), inversionFilter);
		FilteredImageSource closedSource = new FilteredImageSource(image.getSource(), inversionFilter);
		Image openSelectedImage = Outliner.outliner.createImage(openSource);
		Image closedSelectedImage = Outliner.outliner.createImage(closedSource);
		
		ICON_OPEN_NODE_SELECTED = new ImageIcon(openSelectedImage);
		System.out.println("  icon: openNodeSelected");
		ICON_CLOSED_NODE_SELECTED = new ImageIcon(closedSelectedImage);
		System.out.println("  icon: closedNodeSelected");

		System.out.println("End Creating Icons");
		System.out.println("");

	}
}

		// Full red filter.
		//return (
		//	(rgb & 0xff000000) | 0x00ff0000
		//);
		
class inversionFilter extends RGBImageFilter {
	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | (0xffffff - (rgb & 0x00ffffff))
		);
	}
}

class lightenFilter extends RGBImageFilter {
	private int amount = 0;
	
	public lightenFilter(int amount) {
		this.amount = amount;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | (amount + (rgb & 0x00ffffff))
		);
	}
}

class darkenFilter extends RGBImageFilter {
	private int amount = 0;
	
	public darkenFilter(int amount) {
		this.amount = amount;
	}

	public int filterRGB(int x, int y, int rgb) {
		return (
			(rgb & 0xff000000) | ((rgb & 0x00ffffff) - amount)
		);
	}
}