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

abstract public class AbstractOutlineIndicator extends JLabel {

	// Static Fields
	public static int SPACING = 4;

	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean prop = false;
	private boolean propInherited = false;


	// The Constructor
	public AbstractOutlineIndicator(OutlinerCellRendererImpl renderer, String toolTipText) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		setOpaque(true);
		setVisible(false);

		setToolTipText(toolTipText);
		
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


	// Accessors
	public boolean isProperty() {return prop;}
	public void setProperty(boolean prop) {this.prop = prop;}

	public boolean isPropertyInherited() {return propInherited;}
	public void setPropertyInherited(boolean propInherited) {this.propInherited = propInherited;}


	// Abstract Methods
	abstract public void updateIcon();
}