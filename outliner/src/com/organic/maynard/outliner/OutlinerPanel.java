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

import javax.swing.*;
import java.awt.event.*;

public class OutlinerPanel extends JPanel {


	// GUI Fields
	public OutlinerDocument doc = null;
	public OutlineLayoutManager layout = new OutlineLayoutManager(this);


	// The Constructor
	public OutlinerPanel(OutlinerDocument doc) {
		this.doc = doc;
		setBackground(Preferences.PANEL_BACKGROUND_COLOR.cur);
		setLayout(layout);
		
		//addMouseMotionListener(new TestMouseMotionListener());
	}
	
	public void destroy() {
		removeNotify();
		doc = null;
		
		setLayout(null);
		layout.destroy();
		layout = null;
		
		removeAll();
	}
}

public class TestMouseMotionListener extends MouseMotionAdapter {

	public void mouseMoved(MouseEvent e) {
		System.out.println("[" + e.getX() + "," + e.getY() + "]");
	}

}
