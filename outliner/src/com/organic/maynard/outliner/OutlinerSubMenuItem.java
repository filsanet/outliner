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

import org.xml.sax.*;

public class OutlinerSubMenuItem extends AbstractOutlinerMenu implements GUITreeComponent {

	// Constants
	public static final String A_TEXT = "text";

	public OutlinerSubMenuItem() {}


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		// Set the title of the menuItem
		String title = atts.getValue(A_TEXT);
		setText(title);

		// Add this menuItem to the parent menu.
		JMenu menu = (JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2);
		menu.add(this);
	}
	
	public void endSetup(AttributeList atts) {}
}