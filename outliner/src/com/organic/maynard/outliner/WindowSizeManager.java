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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class WindowSizeManager implements ComponentListener {

	// Fields
	private int minWidth = 100;
	private int minHeight = 100;

	private int initialWidth = 100;
	private int initialHeight = 100;
	
	private boolean resizeOnShow = true;


	// The Constructor
	public WindowSizeManager(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public WindowSizeManager(int initialWidth, int initialHeight, int minWidth, int minHeight) {
		this(true, initialWidth, initialHeight, minWidth, minHeight);
	}

	public WindowSizeManager(boolean resizeOnShow, int initialWidth, int initialHeight, int minWidth, int minHeight) {
		this.resizeOnShow = resizeOnShow;
		
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.initialWidth = initialWidth;
		this.initialHeight = initialHeight;
	}


	// ComponentListener Interface
	public void componentResized(ComponentEvent e) {
		Component comp = e.getComponent();
		
		int width = comp.getWidth();
		int height = comp.getHeight();

		boolean resize = false;
		
		if (width < minWidth) {
			resize = true;
			width = minWidth;
		}
		if (height < minHeight) {
			resize = true;
			height = minHeight;
		}
		if (resize) {
			comp.setSize(width, height);
		}
	}

	public void componentMoved(ComponentEvent e) {}
	
	public void componentShown(ComponentEvent e) {
		if (resizeOnShow) {
			e.getComponent().setSize(initialWidth, initialHeight);
		}
	}
	
	public void componentHidden(ComponentEvent e) {}
}