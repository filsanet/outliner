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

public class OutlineCommentIndicator extends JLabel {

	// Class Fields
	public static final ImageIcon ICON_IS_NOT_COMMENTED = new ImageIcon(Outliner.GRAPHICS_DIR + "closed_node_commented.gif");
	
	public static int SPACING = 4;
	
	private static int TRUE_WIDTH = ICON_IS_NOT_COMMENTED.getIconWidth();
	
	public static int WIDTH_DEFAULT = ICON_IS_NOT_COMMENTED.getIconWidth() + SPACING;
	public static int BUTTON_WIDTH = WIDTH_DEFAULT;
	public static int BUTTON_HEIGHT = ICON_IS_NOT_COMMENTED.getIconHeight();
	
	public static ImageIcon ICON_IS_COMMENTED = null;
	public static ImageIcon ICON_IS_COMMENTED_INHERITED = null;
	public static ImageIcon ICON_IS_NOT_COMMENTED_INHERITED = null;

	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean comment = false;
	private boolean commentInherited = false;
	
	// The Constructor
	public OutlineCommentIndicator(OutlinerCellRendererImpl renderer) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		setOpaque(true);
		setVisible(false);

		setToolTipText("Click to Toggle Comment");
		
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
	
	public boolean isComment() {return comment;}
	public void setComment(boolean comment) {this.comment = comment;}

	public boolean isCommentInherited() {return commentInherited;}
	public void setCommentInherited(boolean commentInherited) {this.commentInherited = commentInherited;}

	public void updateIcon() {
		if(isComment()) {
			if (isCommentInherited()) {
				setIcon(ICON_IS_COMMENTED_INHERITED);
			} else {
				setIcon(ICON_IS_COMMENTED);
			}
		} else {
			if (isCommentInherited()) {
				setIcon(ICON_IS_NOT_COMMENTED_INHERITED);
			} else {
				setIcon(ICON_IS_NOT_COMMENTED);
			}
		}
	}
	
	// Static Methods
	public static void createIcons() {
		System.out.println("Start Creating Comment Indicators...");
		
		// Create a buffered image from the commented image.
		Image notCommentedImage = ICON_IS_NOT_COMMENTED.getImage();
		BufferedImage image = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(notCommentedImage,0,0,Outliner.outliner);

		System.out.println("  prototype icon loaded: uncommented");

		// Create Buffered Image for the derived images.
		BufferedImage commentedImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, image.getType());
		
		// Define a transforamtion to rotate the closed image to create the open image.
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getRotateInstance((java.lang.Math.PI), TRUE_WIDTH/2, BUTTON_HEIGHT/2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		at.filter(image, commentedImage);

		lightenFilter redFilter = new lightenFilter(0x00ff0000);
		FilteredImageSource commentedSource = new FilteredImageSource(commentedImage.getSource(), redFilter);
		Image commentedImage2 = Outliner.outliner.createImage(commentedSource);

		ICON_IS_COMMENTED = new ImageIcon(commentedImage2);
		System.out.println("  icon: commented");
		
		// Lighten color to inherited versions
		lightenFilter lightenFilter = new lightenFilter(0x00999999);
		FilteredImageSource commentedInheritedSource = new FilteredImageSource(commentedImage2.getSource(), lightenFilter);
		FilteredImageSource notCommentedInheritedSource = new FilteredImageSource(image.getSource(), lightenFilter);
		Image commentedInheritedImage = Outliner.outliner.createImage(commentedInheritedSource);
		Image notCommentedInheritedImage = Outliner.outliner.createImage(notCommentedInheritedSource);

		ICON_IS_COMMENTED_INHERITED = new ImageIcon(commentedInheritedImage);
		System.out.println("  icon: commented inherited");

		ICON_IS_NOT_COMMENTED_INHERITED = new ImageIcon(notCommentedInheritedImage);
		System.out.println("  icon: uncommented inherited");

		System.out.println("End Creating Comment Indicators");
		System.out.println("");

	}
}