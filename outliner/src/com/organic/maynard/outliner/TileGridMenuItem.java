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
import java.lang.Math ;

/* ------------------------
 * [srk] currently under 
 * heavy construction.
 * please ignore the mess.
 * hope to finish by 1-31-02.
 * ------------------------
 */
// our class
public class TileGridMenuItem 
	extends AbstractOutlinerMenuItem 
	implements ActionListener, GUITreeComponent 
	{
	// TBD [srk] gui_tree.xml and user-pref this stuff
	// we don't tile more than this many windows
	private static final int TILE_LIMIT = 100 ; 
	// we have styles of tiling
	// these tile styles deal with the distribution of
	// regular/fat rows
	private static final int BOTTOM_HEAVY = 1 ;
	private static final int TOP_HEAVY = 2 ;
	private static final int HOUR_GLASS = 3 ;
	private static final int BLIMP = 4 ;
	private static int TILE_STYLE = BOTTOM_HEAVY ;
	
	// calculate a tiling pattern for a spec'd # of windows
	private int [] calcTilePattern (int numWindows) {
		// local vars
		int numColumns ;
		int windowsAccountedFor ;
		
		// no pattern for no windows
		if (numWindows < 1) {
			return null ;
		} // end if
		
		// calculate pattern engine parameters
		int numRows = Math.round((float)Math.sqrt(numWindows)) ; 
		boolean uneven = (numWindows % numRows) > 0 ; 
		int weightChangeRow = numRows - (numWindows % numRows);
		int regularRowSize = Math.round((float)((numWindows/numRows) - 0.5)); 
		int fatRowSize = regularRowSize + 1 ;
		int [] pattern = new int[numRows + 2];
		int fatCounter = 0 ;
		
		// for each row of the pattern
		for (int rowCounter = 1; rowCounter <= numRows; rowCounter++){
			
			// add its number of columns to the pattern
			// if itza fat row ...
			if (uneven && (rowCounter > weightChangeRow)) {
				pattern[rowCounter -1] = fatRowSize ;
				// may as well count fat rows while we're here
				// data comes in handy for tiling styling
				fatCounter++ ;
			// else itza regular row
			} else {
				pattern[rowCounter -1] = regularRowSize ;
			} // end if-else
		} // end for each row of the pattern
		
		// stick a data tail on the pattern donkey
		pattern[numRows] = regularRowSize ;
		pattern[numRows + 1] = fatCounter ;
		
		// return the pattern
		return pattern; 
		
	}// end method calcTilePattern ;
		
		
	// GUITreeComponent interface
	public void startSetup(AttributeList atts) {
		super.startSetup(atts);
		
		addActionListener(this);
	} // end method startSetup


	//------------ ActionListener Interface
	
	// we've been clicked - deal with it
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
		
		// determine how much room we have to play with
		Dimension curAvailSpace = Outliner.desktop.getCurrentAvailableSpace() ; 
		double availWidth = curAvailSpace.getWidth() ;
		double availHeight = curAvailSpace.getHeight() ;

		// obtain minimum tiling width and height values
		// TBD [srk] make this for real via window features figgern' and user prefs
		int minTileRowHeight = 45 ;
		int minTileColumnWidth = 60 ;
		
		// determine the maximum number of rows and columns
		int maxRows = (int)availHeight/minTileRowHeight;
		int maxColumns = (int)availWidth/minTileColumnWidth;
		
		// some row and column vars
		int regularRowHeight = 0 ;
		int regularColumnWidth = 0 ;
		int finalRowHeight = 0 ;
		int finalColumnWidth = 0 ;
		int actualRows = 0 ;
		int actualMaxColumns = 0 ;
		
		// obtain a tiling pattern
		int [] pattern = calcTilePattern(openNotIconifiedDocCount) ;
		
		// determine pattern's number of rows
		int patternRowCount = pattern.length - 2 ;
		
		// grab data tail from pattern donkey
		
		// determine pattern's regular number of columns
		int patternRegRowColumnCount = pattern [patternRowCount] ;
		int patternFatRowColumnCount = patternRegRowColumnCount + 1 ;
		
		// determine # of fat and regular rows in pattern
		int fatRowCount = pattern[patternRowCount + 1] ;
		int regRowCount = patternRowCount - fatRowCount ;

		// how many cells in the whole pattern
		int patternTotalCellCount = patternRegRowColumnCount * regRowCount
					+ patternFatRowColumnCount * fatRowCount ;

		// plenty of room ?
		boolean plentyOfRowRoom = patternRowCount <= maxRows ;
		boolean plentyOfColumnRoom = patternFatRowColumnCount <= maxColumns ;
		boolean plentyOfRoom = plentyOfRowRoom && plentyOfColumnRoom ;

// don't need these, cuz we fork below		
//		// determine actual max number of columns we'll need
//		actualMaxColumns = plentyOfColumnRoom
//			? patternFatRowColumnCount 
//			: maxColumns ;
//		
//		// determine actual number of rows we'll need
//		actualRows = plentyOfRowRoom
//			? patternRowCount 
//			: maxRows ;
			
		// some row and column arrays
		int [] columnWidths = null;
		int [] columnPositions = null;
		int [] rowHeights = null;
		int [] rowPositions = null;

		// if we have plenty of room
		if (plentyOfRoom) {
			// a useful number
			int docLimit = patternTotalCellCount - 1 ;

			// create arrays to store size and position constants
			rowHeights = new int[patternRowCount] ;
			rowPositions = new int [patternRowCount] ;
			
			// determine column widths
			
			// in a regular row
			int regRowColumnWidthStd = (int) (availWidth/patternRegRowColumnCount) ;
			int regRowColumnWidthAdj = (int) availWidth -
						patternRegRowColumnCount * regRowColumnWidthStd ;
			// for standard columns and adjustment columns
			// in a fat row
			int fatRowColumnWidthStd = (int) (availWidth/patternFatRowColumnCount) ;
			int fatRowColumnWidthAdj = (int) availWidth -
						patternFatRowColumnCount * fatRowColumnWidthStd ;
					
			int foo = 0 ;
			
			// for each row in the pattern
				
				
				// for each column in the row
				
//			// set up all but the last column
//			for (int column = 0; column < limit; column++) {
//				columnWidths[column] = regularColumnWidth ;
//				columnPositions[column] = column * regularColumnWidth ;
//			} // end for all but the last column
//			
//			// set up the last column
//			columnWidths[limit] = finalColumnWidth ;
//			columnPositions[limit] = (int)availWidth - finalColumnWidth ;
		} // end if
		
		// else we don't have enuf room, and must limit
		// ourselves to the size of the pattern
		else {
			// how many extras are there ??
			
			// what's the minimum height ??
			
			// how many extras fit in a column 
			
			// do we have enuf columns for extras
			
		} // end else we must squeeze extras on bottom rows
		
		// okay, everything's figured
		
		// [srk] growth bud detour
		if (true) {
			return ;
		}
		
		// some vars for window size and location info
		Point pLocation = new Point();
		Dimension dSize = new Dimension();
		
		// for each open doc
		for (int counter = 0; counter < openDocCount; counter++) {
			// TBD [srk] make this a bit slicker
			// tile em in z order
			// right now we just use chrono order
			
			// grab the doc ref
			doc = Outliner.getDocument(counter) ;
			
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
	
} // end class TileGridMenuItem