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
import javax.swing.event.*;


public class OutlinerWindowMonitor extends InternalFrameAdapter {
	
	public void internalFrameClosing(InternalFrameEvent e) {
		closeInternalFrame(e.getInternalFrame());
	}
	
	public static boolean closeInternalFrame(JInternalFrame w) {
		// Confirm Close when the document is not saved.
		OutlinerDocument doc = (OutlinerDocument) w;
		
		if (doc.getFileName().equals("") && doc.isFileModified()) {
			int result = JOptionPane.showConfirmDialog(doc, "The text in the Untitled file has changed.\nDo you want to save the changes?");
			if (result == JOptionPane.YES_OPTION) {
				FileMenu.saveAsOutlinerDocument(doc);
			} else if (result == JOptionPane.NO_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		} else if (doc.isFileModified()) {
			int result = JOptionPane.showConfirmDialog(doc, "The text in the " + doc.getFileName() + " file has changed.\nDo you want to save the changes?");
			if (result == JOptionPane.YES_OPTION) {
				FileMenu.saveOutlinerDocument(doc);
			} else if (result == JOptionPane.NO_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		// Hide the document
		doc.setVisible(false);
		doc.dispose();

		// Remove the document from the window menus
		WindowMenu.removeWindow(doc);
		
		// Remove the document.
		Outliner.removeDocument(doc);

		// Update the Save All Menu Item
		Outliner.updateSaveAllMenuItem();
		
		return true;
	}
}