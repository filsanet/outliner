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

import javax.swing.*;
import java.io.*;
import java.util.*;

public class SimpleFileFormat implements SaveFileFormat, OpenFileFormat {
	
	// Constructors
	public SimpleFileFormat() {}

	
	// SaveFileFormat Interface
	public byte[] save(TreeContext tree, DocumentInfo docInfo) {
		StringBuffer buf = new StringBuffer();
		tree.rootNode.depthPaddedValue(buf, Preferences.platformToLineEnding(docInfo.getLineEnding()));
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}
	
	public boolean supportsComments() {return false;}
	
	
	// OpenFileFormat Interface
	public int open(TreeContext tree, DocumentInfo docInfo, InputStream stream) {
		int success = OpenFileFormat.FAILURE;

		String text = null;


		try {
			InputStreamReader inputStreamReader = new InputStreamReader(stream, docInfo.getEncodingType());
			BufferedReader buf = new BufferedReader(inputStreamReader);
			
			StringBuffer sb = new StringBuffer();
			String s;
			while((s = buf.readLine()) != null) {
				sb.append(s);
				sb.append(Preferences.LINE_END_STRING);
			}

			text = sb.toString();
		} catch(IOException e) {
			e.printStackTrace();
		}

		if (text != null) {
			Node newNode = new NodeImpl(tree,"");
			int padSuccess = PadSelection.pad(text, tree, 0,Preferences.LINE_END_UNIX, newNode);
			
			switch (padSuccess) {
			
				case PadSelection.SUCCESS:
					tree.setRootNode(newNode);
					success = OpenFileFormat.SUCCESS;
					break;
					
				case PadSelection.SUCCESS_MODIFIED:

					Object[] options = {"Yes","No"};
					int result = JOptionPane.showOptionDialog(Outliner.outliner,
						"The file " + docInfo.getPath() + " has an inconsistent heirarchy.\nEmpty nodes will need to be inserted to open it.\nDo you want to open it anyway?",
						"Confirm Open",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]
					);
					
					if (result == JOptionPane.YES_OPTION) {
						success = OpenFileFormat.SUCCESS_MODIFIED;
						tree.setRootNode(newNode);
						break;
					} else if (result == JOptionPane.NO_OPTION) {
						success = OpenFileFormat.FAILURE_USER_ABORTED;
						break;
					}
										
				case PadSelection.FAILURE:
					success = OpenFileFormat.FAILURE;
					break;
			}
		} else {
			success = OpenFileFormat.FAILURE;
		}
				
		return success;
	}
	
	// File Extensions
	private HashMap extensions = new HashMap();
	
	public void addExtension(String ext, boolean isDefault) {
		extensions.put(ext, new Boolean(isDefault));
	}
	
	public void removeExtension(String ext) {
		extensions.remove(ext);
	}
	
	public String getDefaultExtension() {
		Iterator i = getExtensions();
		while (i.hasNext()) {
			String key = (String) i.next();
			Boolean value = (Boolean) extensions.get(key);
			
			if (value.booleanValue()) {
				return key;
			}
		}
		
		return null;
	}
	
	public Iterator getExtensions() {
		return extensions.keySet().iterator();
	}
	
	public boolean extensionExists(String ext) {
		Iterator it = getExtensions();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (ext.equals(key)) {
				return true;
			}
		}
		
		return false;
	}

}
