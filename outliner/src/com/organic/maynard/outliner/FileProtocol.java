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
	/**
	 * Used by the <code>selectFileToSave</code> method to indicate that the 
	 * GUI should be configured for a save.
	 */	
	public static final int SAVE = 0;
	
	/**
	 * Used by the <code>selectFileToSave</code> method to indicate that the 
	 * GUI should be configured for an export.
	 */	
	public static final int EXPORT = 1;
	
	/**
	 * Gets the name of this protocol.
	 *
	 * @return this protocol's unique name.
	 */
	public String getName();

	/**
	 * Sets the name of this protocol. This name must be unique. The name is 
	 * also used for protocol selection in the GUI.
	 *
	 * @param name sets this protocol's unique name.
	 */	
	public void setName(String name);
	
	/**
	 * Handles selection of a file to open. All GUI elements involved in the 
	 * selection process must be managed by this method. The results of this 
	 * selection process are placed in the provided <code>DocumentInfo</code> 
	 * object. It is strongly suggested that the GUI allow the user to select 
	 * an <code>OpenFileFormat</code> and an <code>encoding type</code>.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object to store the results 
	 *                of the selection process.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */		
	public boolean selectFileToOpen(DocumentInfo docInfo);

	/**
	 * Handles selection of a file to save. All GUI elements involved in the 
	 * selection process must be managed by this method. The results of this 
	 * selection process are placed in the provided <code>OutlinerDocument's</code> 
	 * associated <code>DocumentInfo<code> object. It is strongly suggested 
	 * that the GUI allow the user to select an <code>OpenFileFormat</code>, 
	 * line ending (platform) and an <code>encoding type</code>.
	 *
	 * @param document the <code>OutlinerDocument</code> that contains the 
	 *                 <code>DocumentInfo</code> object to store the results 
	 *                 of the selection process.
	 * @param type     indicates if this is a Save or an Export.
	 * @return         <code>true</code> indicates success and <code>false</code> 
	 *                 indicates failure.
	 */	
	public boolean selectFileToSave(OutlinerDocument document, int type);

	/**
	 * Saves the data stored in the DocumentInfo. The <code>byte[]</code> data is pulled
	 * from the DocumentInfo object's getOutputBytes method.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object that holds the 
	 *                <code>byte[]</code> array to save.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */		
	public boolean saveFile(DocumentInfo docInfo);

	/**
	 * Open a file and stores an <code>InputStream</code> in the <code>DocumentInfo</code>. 
	 * The <code>InputStream</code> is stored by using the <code>DocumentInfo</code> 
	 * object's <code>setInputStream</code> method.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object that holds the 
	 *                <code>InputStream</code> for the file to open.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */	
	public boolean openFile(DocumentInfo docInfo);
}