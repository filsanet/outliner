/**
 * FileFormat interface		
 * 
 * Interface for file format stuff that's common to both OpenFileFormat and SaveFileFormat interfaces
 * 
  * members
 *	interfaces
 * 		instance
 * 			public
 * 				void addExtension(String, boolean)
 * 				void removeExtension(String);
 * 				String getDefaultExtension();
 * 				Iterator getExtensions();
 * 				boolean extensionExists(String);
 * 
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
  * 
 * Most recent changes: 8/15/01 10:10PM
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

// we use this
import java.util.*;


public interface FileFormat { 
	
	// File Extension methods
	
	public void addExtension(String ext, boolean isDefault);
	public void removeExtension(String ext);
	
	public Iterator getExtensions();
	public String getDefaultExtension();

	public boolean extensionExists(String ext);
	
	} // end interface FileFormat