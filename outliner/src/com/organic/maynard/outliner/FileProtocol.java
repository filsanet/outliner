/**
 * copyright (C) 2001 Maynard Demmon <maynard@organic.com>
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

public interface FileProtocol { 
	
	public static final int SAVE = 0;
	public static final int EXPORT = 1;
	
	/**
	 * Gets the name of this protocol.
	 * @return The protocol's unique name.
	 */
	public String getName();

	/**
	 * Sets the name of this protocol. This name must be unique. The name is also
	 * used for protocol selection in the GUI.
	 * @param name The protocol's unique name.
	 */	
	public void setName(String name);
	
	/**
	 * Handles selection of a file to open. All GUI elements involved in the selection
	 * process must be managed by this method. The results of this selection process
	 * are placed in the provided DocumentInfo object. It is strongly suggested that the
	 * GUI allow the user to select an OpenFileFormat and an encoding type.
	 * @param docInfo The DocumentInfo object to store the results of the selection
	 *                process.
	 * @return True indicates sucess and false indicates failure.
	 */		
	public boolean selectFileToOpen(DocumentInfo docInfo);

	/**
	 * Handles selection of a file to save. All GUI elements involved in the selection
	 * process must be managed by this method. The results of this selection process
	 * are placed in the provided OutlinerDocument's associated DocumentInfo object. 
	 * It is strongly suggested that the GUI allow the user to select an OpenFileFormat, 
	 * line ending (platform) and an encoding type.
	 * @param document The OutlinerDocument that contains the DocumentInfo object to
	 *                 store the results of the selection process.
	 * @param type Indicates if this is a Save or an Export.
	 * @return True indicates sucess and false indicates failure.
	 */	
	public boolean selectFileToSave(OutlinerDocument document, int type);
	
	
	/**
	 * Saves the data stored in the DocumentInfo. The byte[] data is pulled
	 * from the DocumentInfo object's getOutputBytes method.
	 * @return True indicates sucess and false indicates failure.
	 */		
	public boolean saveFile(DocumentInfo docInfo);

	/**
	 * Open a file and stores an InputStream in the DocumentInfo. The InputStream
	 * is stored by using the DocumentInfo object's setInputStream method.
	 * @return True indicates sucess and false indicates failure.
	 */	
	public boolean openFile(DocumentInfo docInfo);
}