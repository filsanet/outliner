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
import javax.swing.*;

import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class OutlinerScrollBarUI extends BasicScrollBarUI {
	public static ComponentUI createUI(JComponent c) {
		return new OutlinerScrollBarUI();
	}

	// This overrides the normal behaviour to allow the thumb
	// to be resized while dragging is occurring.
	public void layoutContainer(Container scrollbarContainer) {

		JScrollBar scrollbar = (JScrollBar) scrollbarContainer;
		switch (scrollbar.getOrientation()) {
			case JScrollBar.VERTICAL:
				layoutVScrollbar(scrollbar);
				break;
	            
			case JScrollBar.HORIZONTAL:
				layoutHScrollbar(scrollbar);
				break;
		}
	}
}