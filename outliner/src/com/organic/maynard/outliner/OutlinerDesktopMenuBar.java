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

import javax.swing.JMenuBar;

import org.xml.sax.*;

public class OutlinerDesktopMenuBar extends JMenuBar implements GUITreeComponent {

	public FileMenu fileMenu = null;
	public EditMenu editMenu = null;
	public OutlineMenu outlineMenu = null;
	public SearchMenu searchMenu = null;
	public WindowMenu windowMenu = null;
	
	
	// Constructor
	public OutlinerDesktopMenuBar() {}


	// GUITreeComponent interface
	private String id = null;
	public String getGUITreeComponentID() {return this.id;}
	public void setGUITreeComponentID(String id) {this.id = id;}
	
	public void startSetup(AttributeList atts) {
		Outliner.menuBar = this;
		Outliner.outliner.setJMenuBar(this);
	}
	
	public void endSetup(AttributeList atts) {}
}