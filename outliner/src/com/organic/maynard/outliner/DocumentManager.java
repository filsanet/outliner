/**
 * DocumentManager class
 * 
 * Manages sets of documents
 *	tracks whether they're open or shut
 *	stores their pathnames
 		pathnames can come in many forms
 		we just store 'em
 		examples
 			local:  c:\someDir\someDoc.opml
 			network: \\some_machine\some_sharepoint\someDir\someDoc.opml
 			internet: http://someSite.dom/someDir/someDoc.opml
 			internet: ftp://someSite.dom/someDir/someDoc.opml
 * 
 * extends Object
 * implements JoeReturnCodes
 *
 * Members
 *	variables
 *		instance
 			protected
 				boolean [] docOpenStates
 *			private
 *				String [] docPaths
 				int [] docAlfaOrder
 *i
 *	methods
 * 		instance
 			public
 				DocumentManager (int)
 				boolean documentIsOpen(int)
 				int isThisOneOfOurs (String)
 *			protected
 *				int setDocPath (int, String)
 				String getDocPath (int)
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 8/12/01 11:29AM
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

// we manage a set of documents
public class DocumentManager 

	extends Object 
	implements JoeReturnCodes {
	
	// protected instance vars
	protected boolean [] docOpenStates ;
	
	// private instance vars
	private String [] docPaths ;
	private int [] docAlfaOrder ;
	
	
	// constructor 
	public DocumentManager(int sizeOfDocSet) {
		// call on the ancestors
		super();
		
		// set up the data arrays
		docOpenStates = new boolean[sizeOfDocSet] ;
		docPaths = new String[sizeOfDocSet] ;
		docAlfaOrder = new int[sizeOfDocSet] ;
		
		} // end constructor DocumentManager


	// determine whether one of our documents is currently open
	public boolean documentIsOpen(int docSelector) {
		
		return docOpenStates[docSelector] ;
		
		} // end method documentIsOpen


	// is a doc spec'd by its pathname a member of our set ? 
	// if it is, returns that doc's selector, otherwise returns DOCUMENT_NOT_FOUND
	public int isThisOneOfOurs (String docPathName) {
		
		/* TODO: 
		 *	this stoopid brute search works fine for extremely small sets
		 *	but it'll bog quickly as doc set sizes grow > 10
		 *	
		 *	stan sez he'll fix this Real Soon
		 *	
		 *	his cheap solution: an ordering array ... 
		 *		int docAlfaOrder[sizeOfDocSet]
		 *		each entry is one of our selector integers
		 *		whenever a pathname's set, adjust the ordering array
		 *		then we can binary search off of the ordering array for stuff like this
		 *		o log n baby
		 *	... so that searches can get flatly fast
		 */
		 
		// for each doc in the set 
		for ( int selector = 0;
			selector < docPaths.length;
			selector ++ ){
				
			// if it's the spec'd doc
			if (docPaths[selector].compareTo(docPathName) == 0) {
				
				// return its selector
				return selector ;	
				
				} // end if
			
			} // end for
		
		// if we get this far, it's not one of ours
		return DOCUMENT_NOT_FOUND ;
		
		} // end method isThisOneOfOurs

	
	// set the pathname of one of our documents 
	protected int setDocPath (int docSelector, String docPathName) {
		
		// make sure we're in bounds
		if ((docSelector < 0) || (docSelector > docPaths.length)) {
			return ARRAY_SELECTOR_OUT_OF_BOUNDS ;
			} // end if
		
		// we're in bounds. 
		
		// TBD ASAP
		// if the supplied path is relative rather than absolute ...
		// [if it doesn't start with http or ftp or drive designator or network designator
		// it's relative]
			// make it absolute by prefixing with the absolute path of outliner's root dir
		// for the moment, we fake it, and just assume relativity, and add in outliner's root dir path
		docPathName = Outliner.APP_DIR_PATH + docPathName;
		
		// set the string.
		docPaths[docSelector] = docPathName ;
		
		// exit triumphant
		return SUCCESS;
		
		} // end method setDocPath


	// get the pathname of one of our documents 
	protected String getDocPath (int docSelector) {
		
		// if we're outta bounds ...
		if ((docSelector < 0) || (docSelector > docPaths.length)) {
			
			// ... return bupkis
			return null ;
			
			} // end if
		
		// we're in bounds. return the path name
		return docPaths[docSelector] ;
		
		} // end method getDocPath


	} // END DocumentManager class