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

public class OutlineMoveableIndicator extends AbstractOutlineIndicator {

	// Class Fields
	public static String TOOL_TIP_TEXT = "Click to Toggle Moveability Mode";

	public static final ImageIcon ICON_IS_NOT_PROPERTY = new ImageIcon(Outliner.GRAPHICS_DIR + "is_not_moveable.gif");
	public static ImageIcon ICON_IS_PROPERTY = new ImageIcon(Outliner.GRAPHICS_DIR + "is_moveable.gif");
	public static ImageIcon ICON_IS_PROPERTY_INHERITED = null;
	public static ImageIcon ICON_IS_NOT_PROPERTY_INHERITED = null;
	
	public static int TRUE_WIDTH = ICON_IS_NOT_PROPERTY.getIconWidth();
	
	public static int WIDTH_DEFAULT = ICON_IS_NOT_PROPERTY.getIconWidth();
	public static int BUTTON_WIDTH = WIDTH_DEFAULT;
	public static int BUTTON_HEIGHT = ICON_IS_NOT_PROPERTY.getIconHeight();
	
	
	// The Constructor
	public OutlineMoveableIndicator(OutlinerCellRendererImpl renderer) {
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
		System.out.println("Start Creating Moveable Indicators...");
		
		// Create a buffered image from the is not property image.
		Image isNotPropertyImage = ICON_IS_NOT_PROPERTY.getImage();
		BufferedImage isNotImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gIsNotImage = isNotImage.createGraphics();
		gIsNotImage.drawImage(isNotPropertyImage,0,0,Outliner.outliner);

		System.out.println("  prototype icon loaded: is not moveable");

		// Create a buffered image from the is property image.
		Image isPropertyImage = ICON_IS_PROPERTY.getImage();
		BufferedImage isImage = new BufferedImage(TRUE_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gIsImage = isImage.createGraphics();
		gIsImage.drawImage(isPropertyImage,0,0,Outliner.outliner);

		System.out.println("  prototype icon loaded: is moveable");
			
		// Lighten color to inherited versions
		lightenFilter lightenFilter = new lightenFilter(0x00cccccc);
		FilteredImageSource isNotPropertyInheritedSource = new FilteredImageSource(isNotImage.getSource(), lightenFilter);
		FilteredImageSource isPropertyInheritedSource = new FilteredImageSource(isImage.getSource(), lightenFilter);
		Image isNotPropertyInheritedImage = Outliner.outliner.createImage(isNotPropertyInheritedSource);
		Image isPropertyInheritedImage = Outliner.outliner.createImage(isPropertyInheritedSource);

		ICON_IS_NOT_PROPERTY_INHERITED = new ImageIcon(isNotPropertyInheritedImage);
		System.out.println("  icon: is not moveable inherited");

		ICON_IS_PROPERTY_INHERITED = new ImageIcon(isPropertyInheritedImage);
		System.out.println("  icon: is moveable inherited");

		System.out.println("End Creating Moveable Indicators");
		System.out.println("");
	}
}