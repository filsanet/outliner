/**
 * StanStringTools class
 * 
 * A few useful string tools
 * 
 * members
 *	methods
 *		instance
 *			public
 *				constructor
 *					StanStringTools ()
 *		class
 *			public
 *				String trimFileExtension (String)
 				String getFileNameFromPathName (String) 
 *
 *		
 * Copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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
 * @version $Revision$ Date:$
 */

// we're part of this
package com.organic.maynard.util.string;

// we use these
import java.util.*;
import java.io.File ;

// Stan's string tools
public class StanStringTools {

	// Constructor
	// public StanStringTools() {}
	
	
	// Class Methods
	
	// trim off last four characters if there's a dot three chars before the end
	public static String trimFileExtension(String fileNameString) {
		
		// local vars
		String resultString = fileNameString ;
		int dotPoint = fileNameString.length() - 4;
		
		// if we have a dot 3 chars in from right side ...
		if (fileNameString.charAt(dotPoint) == '.') {
			
			// trim last 4 chars
			resultString = fileNameString.substring(0, dotPoint) ;
			
		}
		
		return resultString ;
		
	} // end method trimOffAnyFileExtension
	

	// grab a filename from a pathname
	public static String getFileNameFromPathName(String pathNameString) {
		
		File file = new File (pathNameString) ;
		if (file == null) {
			return null ;
		} else {
			return file.getName() ;
		} // end else
		
	} // end method trimOffAnyFileExtension
	
} // end class StanStringTools
