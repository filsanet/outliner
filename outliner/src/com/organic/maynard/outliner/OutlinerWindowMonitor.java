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
				SaveAsFileMenuItem item = (SaveAsFileMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.SAVE_AS_MENU_ITEM);
				item.saveAsOutlinerDocument(doc);
			} else if (result == JOptionPane.NO_OPTION) {
				// Do Nothing
			} else if (result == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		} else if (doc.isFileModified()) {
			int result = JOptionPane.showConfirmDialog(doc, "The text in the " + doc.getFileName() + " file has changed.\nDo you want to save the changes?");
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
			// Window Position
			Rectangle r = doc.getNormalBounds();
			docInfo.setWindowTop(r.y);
			docInfo.setWindowLeft(r.x);
			docInfo.setWindowBottom(r.y + r.height);
			docInfo.setWindowRight(r.x + r.width);
			
			// VerticalScrollState
			int index = doc.tree.visibleNodes.indexOf(doc.panel.layout.getNodeToDrawFrom()) + 1;
			docInfo.setVerticalScrollState(index);
			
			// ExpandedNodes
			docInfo.getExpandedNodes().clear();
			for (int i = 0; i < doc.tree.visibleNodes.size(); i++) {
				Node node = (Node) doc.tree.visibleNodes.get(i);
				if (node.isExpanded()) {
					docInfo.addExpandedNodeNum(i);
				}
			}
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