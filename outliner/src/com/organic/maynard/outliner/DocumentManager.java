/**
 * DocumentManager class
 * 
 * Manages sets of documents
 *	tracks whether they're open or shut
 *	stores their pathnames
 *		pathnames can come in many forms
 *		we just store 'em
 *		examples
 *			local:  c:\someDir\someDoc.opml
 *			network: \\some_machine\some_sharepoint\someDir\someDoc.opml
 *			internet: http://someSite.dom/someDir/someDoc.opml
 *			internet: ftp://someSite.dom/someDir/someDoc.opml
 * 
 * extends Object
 * implements JoeReturnCodes
 *
 * Members
 *	variables
 *		instance
 *			protected
 *				boolean [] docOpenStates
 *			private
 *				String [] docPaths
 *				int [] docAlfaOrder
 *i
 *	methods
 * 		instance
 *			public
 *				DocumentManager (int)
 *				boolean documentIsOpen(int)
 *				int isThisOneOfOurs (String)
 *			protected
 *				int setDocPath (int, String)
 *				String getDocPath (int)
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
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