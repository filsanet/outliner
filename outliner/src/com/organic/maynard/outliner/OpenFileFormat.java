/**
 * OpenFileFormat interface		[srk] added this header info 8/14/01 9:27PM
 * 
 * Interface for opening files
 * 
 * members
 *	interfaces
 * 		instance
 * 			public
 * 				int open(TreeContext, DocumentInfo, InputStream)
 * 				void addExtension(String, boolean)
 * 				void removeExtension(String);
 * 				String getDefaultExtension();
 * 				Iterator getExtensions();
 * 				boolean extensionExists(String);
 * 
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

// [srk] we're part of this 
package com.organic.maynard.outliner;

// [srk] we need access to these
import javax.swing.*;
import java.io.*;
import java.util.*;

public interface OpenFileFormat { 
	
	// Constants
	// public static final int FAILURE = 0;		// [srk] implementors now get these via JoeReturnCodes 8/14/01
	// public static final int SUCCESS = 1;
	// public static final int SUCCESS_MODIFIED = 2;
	// public static final int FAILURE_USER_ABORTED = 3;


	// Methods
	
	public int open(
		TreeContext tree,
		DocumentInfo docInfo,
		InputStream stream
		); 

	
	// File Extensions
	public void addExtension(String ext, boolean isDefault);
	public void removeExtension(String ext);
	
	public String getDefaultExtension();
	public Iterator getExtensions();
	public boolean extensionExists(String ext);
	
	} // end interface OpenFileFormat