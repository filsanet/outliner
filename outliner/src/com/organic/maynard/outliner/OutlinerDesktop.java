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

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class OutlinerDesktop extends JDesktopPane implements Scrollable {
	
	public OutlinerDesktopManager desktopManager = new OutlinerDesktopManager();
	
	// The Constructor
	public OutlinerDesktop() {
		super();
		//setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		setDesktopManager(desktopManager);
	}

	public Dimension getPreferredSize() {
		if (desktopManager.isMaximized()) {
			return new Dimension(getParent().getWidth(),getParent().getHeight());
		}
		
		Component[] children = getComponents();
		
		int scrollBarWidth = Outliner.jsp.getVerticalScrollBar().getWidth();
		int maxWidth = getParent().getWidth() - scrollBarWidth;
		int maxHeight = getParent().getHeight() - scrollBarWidth;
		
		for (int i = 0; i < children.length; i++) {
			boolean isIcon = true;
			if (children[i] instanceof JInternalFrame) {
				JInternalFrame child = (JInternalFrame) children[i];
				if (!child.isIcon()) {
					isIcon = false;
				}
			}
			
			if (!isIcon) {
				Point p = children[i].getLocation();
				int x = children[i].getWidth() + p.x;
				int y = children[i].getHeight() + p.y;
				
				if (x > maxWidth) {
					maxWidth = x;
				}
				
				if (y > maxHeight) {
					maxHeight = y;
				}
			}
		}
		//System.out.println(maxWidth + " : " + maxHeight);
		return (new Dimension(maxWidth,maxHeight));
	}
	
	// Scrollable Interface
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    	//System.out.println("getScrollableUnitIncrement");
        switch(orientation) {
	        case SwingConstants.VERTICAL:
	            return visibleRect.height / 10;
	        case SwingConstants.HORIZONTAL:
	            return visibleRect.width / 10;
	        default:
	            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
    }
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		//System.out.println("getScrollableBlockIncrement");
        switch(orientation) {
	        case SwingConstants.VERTICAL:
	            return visibleRect.height;
	        case SwingConstants.HORIZONTAL:
	            return visibleRect.width;
	        default:
	            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
	}

    public boolean getScrollableTracksViewportHeight() {
    	//System.out.println("getScrollableTracksViewportHeight");
    	return false;
    }
    
    public boolean getScrollableTracksViewportWidth() {
    	//System.out.println("getScrollableTracksViewportWidth");
    	return false;
	}
}