/**
 * PdbTMFileFormat class
 * 
 * Handles Palm Pilot pdb files created by Thought Manager
 * 
 * extends PdbFileFormat 
 * 
 * members
 *	methods
 * 		instance
 			public
 				constructors
 *			protected
 *				void createReaderWriter() 
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/23/01 4:56PM
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
// we're part of this
package com.organic.maynard.outliner;

// we read and write Palm pdb files created by Thought Manager 
public class PdbTMFileFormat 
	
	extends PdbFileFormat { 

	
	// ======== flavor-specific methods ========

	// create a reader-writer for this format
	
	// NOTE this is PDB-flavor-specific
	// NOTE failures should throw an appropriate JoeException
	
	
	protected void createReaderWriter() 
	
		throws JoeException {
	
		// try to create an appropriate reader-writer
		ourReaderWriter = new PdbTMReaderWriter() ;
		
		// if we fail ...
		if (ourReaderWriter == null) {
			
			throw new JoeException(UNABLE_TO_CREATE_OBJECT) ;
			
			} // end if 
		
		} // end protected method createReaderWriter
	
	} // end class PdbTMFileFormat
	
