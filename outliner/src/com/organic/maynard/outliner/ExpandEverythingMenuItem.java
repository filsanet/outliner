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

public class ExpandEverythingMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		expandEverything(Outliner.getMostRecentDocumentTouched());
	}

	private static void expandEverything(OutlinerDocument doc) {
		OutlinerCellRendererImpl textArea = doc.panel.layout.getUIComponent(doc.tree.getEditingNode());
		if (textArea == null) {return;}
		try {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				TextKeyListener.expandEverything(doc.tree);
			} else if (doc.tree.getComponentFocus() == OutlineLayoutManager.ICON) {
				IconKeyListener.expandEverything(doc.tree);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
