/**
 * OpenFileFormat interface	
 * 
 * an interface for opening files
 * 
 * extends the FileFormat interface
 * 
 * members
 *	interfaces
 * 		instance
 * 			public
 * 				int open(TreeContext, DocumentInfo, InputStream)
 * 
 * 
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 *
 * Most recent changes: 8/15/01 5:04PM
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

// [srk] we need access to this
import java.io.*;

// the interface
public interface OpenFileFormat 
	
	extends FileFormat {
		
	// Methods
	
	// open a file as an outline	[srk]
	public int open(
		TreeContext tree,
		DocumentInfo docInfo,
		InputStream stream
		); 
	
	} // end interface OpenFileFormat