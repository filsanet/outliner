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

public class OpenFileMenuItem extends AbstractOutlinerMenuItem implements ActionListener {

	private FileProtocol protocol = null;

	// Constructors
	public OpenFileMenuItem(FileProtocol protocol) {
		setProtocol(protocol);
		addActionListener(this);
	}
	
	// Accessors
	public FileProtocol getProtocol() {
		return this.protocol;
	}
	
	public void setProtocol(FileProtocol protocol) {
		this.protocol = protocol;
		setText(protocol.getName());
	}


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		openOutlinerDocument(getProtocol());
	}

	protected static void openOutlinerDocument(FileProtocol protocol) {
		DocumentInfo docInfo = new DocumentInfo();
		docInfo.setProtocolName(protocol.getName());

		// Select the file we are going to open.
		if (!protocol.selectFileToOpen(docInfo)) {
			return;
		}
		
		FileMenu.openFile(docInfo, protocol);
	}
}