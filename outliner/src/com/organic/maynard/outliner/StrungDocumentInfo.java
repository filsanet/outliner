/**
 * Copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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
import java.util.Comparator ;
import java.lang.ClassCastException ;

// this lets us put docInfo into a sorted-by-name/orSomeOtherString data structure
public class StrungDocumentInfo 
	implements Comparable, Comparator 
	{
	
	// Instance Fields		
	private DocumentInfo docInfo = null;
	private String string = null ;
	boolean ignoreCase = false ;

	// The Constructors
	public StrungDocumentInfo(
		String someString, 
		DocumentInfo someDocInfo
		){
			
		docInfo = someDocInfo ;
		string = someString ;
		
	} // end constructor
	
	public String getString () {
		return string ;
	}
	
	public DocumentInfo getDocumentInfo () {
		return docInfo ;
	}
	
	public void setString (String someString) {
		string = someString ;
	} // end method setString
	
	public void setDocumentInfo (DocumentInfo someDocInfo) {
		docInfo = someDocInfo ;
	} // end method setDocumentInfo
	
	
	// Comparable interface methods
	public int compareTo(Object obj) {
		
		// if obj is not effectively one of us ...
		if (! this.getClass().isInstance(obj)) {
			throw new ClassCastException ();
		} // end if
		
		// use the string component for the comparison
		// switch on case-handling
		if (ignoreCase) {
			return string.compareTo(((StrungDocumentInfo)obj).getString().toUpperCase()) ;
		} else {
			return string.compareTo(((StrungDocumentInfo)obj).getString()) ;
		} // end if-else
		
	} // end method compareTo
	
	// Comparator interface methods
	public int compare(Object obj01, Object obj02) {
		// if obj01 or obj02 is not effectively one of us ...
		if ( (! this.getClass().isInstance(obj01)) || (! this.getClass().isInstance(obj02)) ) {
			throw new ClassCastException ();
		} // end if
		
		// use the string component for the comparison
		// switch on case-handling
		if (ignoreCase) {
			return ((StrungDocumentInfo)obj01).getString().compareTo(((StrungDocumentInfo)obj02).getString().toUpperCase()) ;
		} else {
			return ((StrungDocumentInfo)obj01).getString().compareTo(((StrungDocumentInfo)obj02).getString()) ;
		} // end if-else
		
	} // end method compare
	
	public boolean equals(Object obj) {
		
		// if obj is not effectively one of us ...
		if (! this.getClass().isInstance(obj)) {
			throw new ClassCastException ();
		} // end if
		
		// use the string component for the comparison
		// switch on case-handling
		if (ignoreCase) {
			return string.equals(((StrungDocumentInfo)obj).getString().toUpperCase()) ;
		} else {
			return string.equals(((StrungDocumentInfo)obj).getString()) ;
		} // end if-else
		
	} // end method equals
	
} // end class StrungDocumentInfo