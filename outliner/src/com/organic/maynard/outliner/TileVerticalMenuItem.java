/**
 * Copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

 // we're part of this
package com.organic.maynard.outliner;

// we use these
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;
import java.util.Vector ;

// our class
public class TileVerticalMenuItem 
	extends AbstractOutlinerMenuItem 
	implements ActionListener, GUITreeComponent 
	{

	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	} // end method startSetup


	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// determine how many documents are open
		int openDocCount = Outliner.openDocumentCount() ;

		// if no documents are open, leave
		if (openDocCount == 0){
			return ;
		} // end if
		
		// a general-purpose doc var
		OutlinerDocument doc = null ;
		
		// let's build a list of not-iconified windows
		// [we don't touch iconified windows]
		Vector notIconified = new Vector() ;
		
		// for each open document
		for (int counter = 0; counter < openDocCount; counter++) {
			// grab the doc ref
			doc = Outliner.getDocument(counter) ;
			
			// if we're not iconified
			if (! doc.isIcon()) {
				// add us to the list
				notIconified.add(doc) ;
			} // end if
		} // end for
		
		// store the count
		int openNotIconifiedDocCount = notIconified.size() ;
		
		// if everybody's iconified, leave
		if (openNotIconifiedDocCount == 0) {
			return ;
		} // end if

		// if we're in a maximized state ...
		if (Outliner.desktop.desktopManager.isMaximized()) {
			// leave that state
			Outliner.desktop.desktopManager.setMaximized(false) ;
			
			// if there's a topmost window
			doc = Outliner.getMostRecentDocumentTouched(); 
			if (doc != null) {
				// have it leave the max state
				try {
				doc.setMaximum(false) ;
				} // end try
				catch (java.beans.PropertyVetoException pve) {
					pve.printStackTrace();
				} // end catch
			} // end if
		} // end if we're in a maximized state
		
		// get the raw available space
		Dimension curAvailSpace = Outliner.desktop.getCurrentAvailableSpace() ; 
		
		// since we're tiling, any scrollbars will melt away
		Outliner.desktop.addScrollbarsToAvailSpace (curAvailSpace) ;

		// determine how much room we have to play with
		double availWidth = curAvailSpace.getWidth() ;
		double availHeight = curAvailSpace.getHeight() ;

		// obtain minimum tiling column width
		// TBD make this for real via window features figgern'
		int minTileColumnWidth = 60 ;
		
		// determine the maximum number of columns
		int maxColumns = (int)availWidth/minTileColumnWidth;
		
		// some column vars
		int nominalColumnWidth = 0 ;
		int finalColumnWidth = 0 ;
		int actualColumns = 0 ;
		
		// plenty of room ?
		boolean plentyOfRoom = maxColumns >= openNotIconifiedDocCount ;
		
		// determine actual number of columns we'll need
		actualColumns = plentyOfRoom
			? openNotIconifiedDocCount 
			: maxColumns ;
			
		// some column arrays
		int [] columnWidths = new int[actualColumns] ;
		int [] columnPositions = new int [actualColumns] ;

		// if we have plenty of columns
		if (plentyOfRoom) {
			// we have a limit
			int limit = openNotIconifiedDocCount - 1 ;
			
			// determine nominal column width
			nominalColumnWidth = (int) (availWidth/openNotIconifiedDocCount) ;
			// determine final row width
			finalColumnWidth = (int)availWidth - (nominalColumnWidth * limit) ;
			
			// set up all but the last column
			for (int column = 0; column < limit; column++) {
				columnWidths[column] = nominalColumnWidth ;
				columnPositions[column] = column * nominalColumnWidth ;
			} // end for all but the last column
			
			// set up the last column
			columnWidths[limit] = finalColumnWidth ;
			columnPositions[limit] = (int)availWidth - finalColumnWidth ;
		} // end if
		// else we must squeeze extras on bottom rows
		else {
			// how many extras are there ??
			
			// what's the minimum height ??
			
			// how many extras fit in a column 
			
			// do we have enuf columns for extras
			
		} // end else we must squeeze extras on bottom rows
		
		// okay, everything's figured
		
		// some vars for window size and location info
		Point pLocation = new Point();
		Dimension dSize = new Dimension();
		
		// for each open doc
		for (int counter = 0; counter < openNotIconifiedDocCount; counter++) {
			// TBD [srk] make this a bit slicker
			// tile em in z order
			// right now we just use chrono order
			
			// grab the doc ref
			doc = (OutlinerDocument)notIconified.get(counter) ;
			
			// set up location
			pLocation.setLocation(columnPositions[counter],0) ;
			
			// set up size
			dSize.setSize(columnWidths[counter],(int)availHeight) ;
			
			// set the doc's new location and size
			doc.setLocation(pLocation) ;
			doc.setSize(dSize) ;
			
			
		} // end for each open doc
		
		// clean up the horizontal scrollbar's area
		Outliner.jsp.getHorizontalScrollBar().revalidate();
		
		
		
		
		// if it's greater than the number of rows, the leftovers
		// go in the bottom row, at minimum width, and if more leftovers,
		// spill up to next row, etc.\
		// determine row height
		// determine row y positions
		// for a one-doc row
		//	set width to max
		//	set height 
		// for a multi-doc row
		//	determine # of docs to be shown
		//	divide up space
		//	set widths
		//	set height
		// set one row at a time, top to bottom
		// order is same as document z-ordering
		
		
		
		
//		// if we're not in a totally-maximized state 
//		// [which would make this all pointless] ...
//		if (!Outliner.desktop.desktopManager.isMaximized()) {
//			
//			// grabaholda the topmost doc
//			OutlinerDocument doc = Outliner.getMostRecentDocumentTouched();
//			
			// tell it to leave maximized state
			
			
			
//			// see how wide we can get
//			Dimension curAvailSpace = Outliner.desktop.getCurrentAvailableSpace() ; 
//			double maxWidth = curAvailSpace.getWidth() ;
//			
//			// get the doc's current location
//			Point pLocation = doc.getLocation() ;
//			
//			// set its left point to the left edge of the content area
//			pLocation.setLocation(0, pLocation.getY()) ;
//			
//			// get the doc's current size
//			Dimension dSize = new Dimension() ;
//			dSize.setSize((int)maxWidth, (int)doc.getSize().getHeight()) ;
//			
//			// set the doc's new location and size
//			doc.setLocation(pLocation) ;
//			doc.setSize(dSize) ;
//			
//			// let the vertical scroll bar adjust for our new size
//			Outliner.jsp.getVerticalScrollBar().revalidate();
//			
//		} // end if we're not totally maximized
		
	} // end method actionPerformed
	
} // end class TileVerticalMenuItem