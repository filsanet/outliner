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

public class StackMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// Constants
	private static final int STACK_X_START = 5;
	private static final int STACK_Y_START = 5;
	private static final int STACK_X_STEP = 30;
	private static final int STACK_Y_STEP = 30;

	private static final int STACK_X_COLUMN_STEP = 45;


	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (!Outliner.desktop.desktopManager.isMaximized()) {
			Point p = new Point(STACK_X_START,STACK_Y_START);
			int rowCount = 1;
			int columnCount = 1;
			
			int upperBound = getUpperScreenBoundary();
			
			OutlinerDocument mostRecentDocumentTouched = Outliner.getMostRecentDocumentTouched();
			
			for (int i = 0; i < Outliner.openDocumentCount(); i++) {
				OutlinerDocument doc = Outliner.getDocument(i);
				
				// Restore Size
				doc.restoreWindowToInitialSize();
				
				// Set Location
				doc.setLocation(p);
				p.x += STACK_X_STEP;
				p.y += STACK_Y_STEP;
				
				// Set Z Order
				doc.toFront();
				
				// If we've gone down to far, then reset for a new column
				if ((p.y + OutlinerDocument.INITIAL_HEIGHT) >= upperBound ) {
					p.y = STACK_Y_START;
					p.x = STACK_X_START + STACK_X_COLUMN_STEP * columnCount;
					columnCount++;
					rowCount = 0;
				}
				
				rowCount++;
			}
			
			if (mostRecentDocumentTouched != null) {
				mostRecentDocumentTouched.toFront();
			}		
		}
	}

	private static int getUpperScreenBoundary() {
		return Outliner.desktop.getHeight() - STACK_Y_START;
	}
}