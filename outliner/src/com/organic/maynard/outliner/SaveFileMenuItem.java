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

import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

public class SaveFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent {

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
		
		setEnabled(false);
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		saveOutlinerDocument(Outliner.getMostRecentDocumentTouched());
	}

	protected static void saveOutlinerDocument(OutlinerDocument document) {
		FileProtocol protocol = Outliner.fileProtocolManager.getProtocol(document.getDocumentInfo().getProtocolName());
	
		// Get the default protocol if none was found.
		if (protocol == null) {
			protocol = Outliner.fileProtocolManager.getDefault();
		}
		
		if (!document.getFileName().equals("")) {
			FileMenu.saveFile(document.getFileName(), document,  protocol, false);
		} else {
			SaveAsFileMenuItem.saveAsOutlinerDocument(document, protocol);
		}
	}
}