/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
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

//import java.awt.*;
//import javax.swing.*;
import java.io.*;

public class SimpleFileFormat implements SaveFileFormat, OpenFileFormat {
	
	// Constructors
	public SimpleFileFormat() {}

	
	// SaveFileFormat Interface
	public boolean save(TreeContext tree, DocumentInfo docInfo) {
		return FileFormatManager.writeFile(
			docInfo.getPath(), 
			docInfo.getEncodingType(), 
			tree.rootNode.depthPaddedValue(Preferences.platformToLineEnding(docInfo.getLineEnding()))
		);
	}
	
	
	// OpenFileFormat Interface
	public boolean open(TreeContext tree, DocumentInfo docInfo) {
		boolean success = false;

		String text = FileFormatManager.loadFile(docInfo.getPath(), docInfo.getEncodingType());
		if (text != null) {
			tree.setRootNode(PadSelection.pad(text, tree, 0,Preferences.LINE_END_UNIX));
			success = true;
		} else {
			success = false;
		}
				
		return success;
	}
}