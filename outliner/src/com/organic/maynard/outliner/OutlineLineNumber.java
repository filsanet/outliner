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

public class OutlineLineNumber extends JLabel {

	// Constants
	public static final int LINE_NUMBER_WIDTH_DEFAULT = 30;
	public static final int LINE_NUMBER_WIDTH_MIN = 0;
	public static final int LINE_NUMBER_HEIGHT_DEFAULT = 15;

	// Class Variables
	public static int LINE_NUMBER_HEIGHT = LINE_NUMBER_HEIGHT_DEFAULT;
	public static int LINE_NUMBER_WIDTH = LINE_NUMBER_WIDTH_DEFAULT;
	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	// The Constructor
	public OutlineLineNumber(OutlinerCellRendererImpl renderer) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		if (Preferences.SHOW_LINE_NUMBERS.cur) {
			setOpaque(true);
			LINE_NUMBER_WIDTH = LINE_NUMBER_WIDTH_DEFAULT;
		} else {
			setOpaque(false);
			LINE_NUMBER_WIDTH = LINE_NUMBER_WIDTH_MIN;
		}
		setVisible(false);
	}
	
	public void destroy() {
		removeAll();
		renderer = null;
	}
	
	public boolean isManagingFocus() {return true;}
}