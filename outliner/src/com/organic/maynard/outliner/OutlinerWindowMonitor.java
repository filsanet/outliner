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
import java.awt.Rectangle;
import com.organic.maynard.util.string.Replace;

public class OutlinerWindowMonitor extends InternalFrameAdapter {
	
	public void internalFrameClosing(InternalFrameEvent e) {
		closeInternalFrame(e.getInternalFrame());
	}
	
	public static boolean closeInternalFrame(JInternalFrame w) {
		// Confirm Close when the document is not saved.
		OutlinerDocument doc = (OutlinerDocument) w;
		
		String msg = null;
		if (doc.getFileName().equals("") && doc.isFileModified()) {
			msg = GUITreeLoader.reg.getText("error_window_monitor_untitled_save_changes");

			int result = JOptionPane.showConfirmDialog(doc, msg);
			if (result == JOptionPane.YES_OPTION) {
				SaveAsFileMenuItem item = (SaveAsFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_AS_MENU_ITEM);
				item.saveAsOutlinerDocument(doc);
			} else if (result == JOptionPane.NO_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		} else if (doc.isFileModified()) {
			msg = GUITreeLoader.reg.getText("error_window_monitor_untitled_save_changes");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, doc.getFileName());

			int result = JOptionPane.showConfirmDialog(doc, msg);
			if (result == JOptionPane.YES_OPTION) {
				SaveFileMenuItem item = (SaveFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_MENU_ITEM);
				item.saveOutlinerDocument(doc);
			} else if (result == JOptionPane.NO_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		
		// Record current state into DocumentInfo in the RecentFileList if we can
		DocumentInfo docInfo = RecentFilesList.getDocumentInfo(doc.getFileName());
		if (docInfo != null) {
			docInfo.recordWindowPositioning(doc);
		}

		// Hide the document
		doc.setVisible(false);

		// Remove the document.
		Outliner.removeDocument(doc);
		
		// Explicitly Destroy since Swing has problems letting go.
		// Seems to make a difference when we also use -Xincgc.
		doc.destroy();
		
		doc.dispose();
		
		System.gc();
		
		return true;
	}
}