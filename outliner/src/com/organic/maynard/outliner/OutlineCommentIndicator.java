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

public class OutlineCommentIndicator extends AbstractOutlineIndicator {

	// Class Fields
	public static String TOOL_TIP_TEXT = "Click to Toggle Comment";

	public static final ImageIcon ICON_IS_NOT_PROPERTY = new ImageIcon(Outliner.GRAPHICS_DIR + "closed_node_commented.gif");
	public static ImageIcon ICON_IS_PROPERTY = null;
	public static ImageIcon ICON_IS_PROPERTY_INHERITED = null;
	public static ImageIcon ICON_IS_NOT_PROPERTY_INHERITED = null;
	
	public static int TRUE_WIDTH = ICON_IS_NOT_PROPERTY.getIconWidth();
	
	public static int WIDTH_DEFAULT = ICON_IS_NOT_PROPERTY.getIconWidth() + AbstractOutlineIndicator.SPACING;
	public static int BUTTON_WIDTH = WIDTH_DEFAULT;
	public static int BUTTON_HEIGHT = ICON_IS_NOT_PROPERTY.getIconHeight();
	
	
	// The Constructor
	public OutlineCommentIndicator(OutlinerCellRendererImpl renderer) {
		super(renderer, TOOL_TIP_TEXT);
	}

	// Misc Methods
	public void updateIcon() {
		if(isProperty()) {
			if (isPropertyInherited()) {
				setIcon(ICON_IS_PROPERTY_INHERITED);
			} else {
				setIcon(ICON_IS_PROPERTY);
			}
		} else {
			if (isPropertyInherited()) {
				setIcon(ICON_IS_NOT_PROPERTY_INHERITED);
			} else {
				setIcon(ICON_IS_NOT_PROPERTY);
			}
		}
	}
	
	// Static Methods
	public static void createIcons() {
		System.out.println("Start Creating Comment Indicators...");
		
		// Create a buffered image from the commented image.
		Image notCommentedImage = ICON_IS_NOT_PROPERTY.getImage();
		BufferedImage image = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(notCommentedImage,0,0,Outliner.outliner);

		System.out.println("  prototype icon loaded: uncommented");

		// Create Buffered Image for the derived images.
		BufferedImage commentedImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, image.getType());
		
		// Define a transforamtion to rotate the closed image to create the open image.
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getRotateInstance((java.lang.Math.PI), TRUE_WIDTH/2, BUTTON_HEIGHT/2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		at.filter(image, commentedImage);

		Color c = Preferences.getPreferenceColor(Preferences.TEXTAREA_COMMENT_COLOR).cur;
		int hexColor = ((c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue());
		
		lightenFilter redFilter = new lightenFilter(hexColor);
		FilteredImageSource commentedSource = new FilteredImageSource(commentedImage.getSource(), redFilter);
		Image commentedImage2 = Outliner.outliner.createImage(commentedSource);

		ICON_IS_PROPERTY = new ImageIcon(commentedImage2);
		System.out.println("  icon: commented");
		
		// Lighten color to inherited versions
		lightenFilter lightenFilter = new lightenFilter(0x00999999);
		FilteredImageSource commentedInheritedSource = new FilteredImageSource(commentedImage2.getSource(), lightenFilter);
		FilteredImageSource notCommentedInheritedSource = new FilteredImageSource(image.getSource(), lightenFilter);
		Image commentedInheritedImage = Outliner.outliner.createImage(commentedInheritedSource);
		Image notCommentedInheritedImage = Outliner.outliner.createImage(notCommentedInheritedSource);

		ICON_IS_PROPERTY_INHERITED = new ImageIcon(commentedInheritedImage);
		System.out.println("  icon: commented inherited");

		ICON_IS_NOT_PROPERTY_INHERITED = new ImageIcon(notCommentedInheritedImage);
		System.out.println("  icon: uncommented inherited");

		System.out.println("End Creating Comment Indicators");
		System.out.println("");
	}
}