/**
 * SaveFileFormat interface
 * 
 * an interface for saving files
 * 
 * extends the FileFormat interface
 * 
 * members
 *	interfaces
 * 		instance
 * 			public
 * 				byte[] save(TreeContext, DocumentInfo docInfo)
 * 				boolean supportsComments();
 * 				boolean supportsEditability();
 * 				boolean supportsMoveability();
 * 				boolean supportsAttributes();
 * 				boolean supportsDocumentAttributes();
 * 	
 * 	
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 *
 * Most recent changes: 8/29/01 2:49PM
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
 
// we're part of this 
package com.organic.maynard.outliner;

// the interface
public interface SaveFileFormat 

	extends FileFormat {
	
	// methods
	
	// save an outline to a file
	public byte[] save(
		TreeContext tree,
		DocumentInfo docInfo
		);
	
	} // end interface SaveFileFormat