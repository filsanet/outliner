/**
 * StanVectorTools class
 * 
 * A few useful vector tools
 * 
 * members
 *	methods
 *		class
 *			public
 *				void moveElementToTail (Vector)
 *				void moveElementToHead (Vector)
 *				void swapElements (Vector, int, int)
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
import java.util.Vector ;

// Stan's string tools
public class StanVectorTools {

	// Class Methods
	
	// move an element to the tail end of a vector
	public static void moveElementToTail(Vector someVector, int index) {
		
		// size - 1 is useful
		int limit = someVector.size() - 1;
		
		// validate parameters
		// also leave if element's already at the tail
		if ((someVector == null) || (index < 0) || (index >= limit)) {
			return ;
		} // end if
		
		// grab the element to be moved
		Object theNewTail = someVector.get(index) ;
		
		// move everyone further tailward one spot headward
		for (int position = index + 1; position < limit; position ++) {
			someVector.set(position, someVector.get(position + 1)) ;
		} // end for
		
		// set the movee tailmost
		someVector.set(limit, theNewTail) ;
		
		// done
		return ;
		
	} // end method moveElementToTil 
		
		
	// move an element to the head end of a vector
	public static void moveElementToHead(Vector someVector, int index) {
		
		// size is useful
		int size = someVector.size();
		
		// validate parameters
		// also leave if element is already at the head
		if ((someVector == null) || (index <= 0) || (index >= size)) {
			return ;
		} // end if
		
		// grab the element to be moved
		Object theNewHead = someVector.get(index) ;
		
		// move everyone further headward one spot tailward
		for (int position = index - 1; position >= 0; position --) {
			someVector.set(position + 1, someVector.get(position)) ;
		} // end for
		
		// set the movee headmost
		someVector.set(0, theNewHead) ;
		
		// done
		return ;
		
	} // end method moveElementToTop 
		
		
	// swap the positions of two elements
	public static void swapElements(Vector someVector, int indexOne, int indexTwo) {
		
		// size is useful
		int size = someVector.size() ;
		
		// validate parameters
		// also leave in identity case
		if (	(someVector == null) || 
			(indexOne < 0) || (indexOne >= size)||
			(indexTwo < 0) || (indexTwo >= size)||
			(indexOne == indexTwo)	)
			{
			return ;
		} // end if
		
		// grab element one
		Object elementOne = someVector.get(indexOne) ;
		
		// set element two in its place
		someVector.set(indexOne, someVector.get(indexTwo)) ;
		
		// set it in element two's place
		someVector.set(indexTwo, elementOne) ;
		
		// done
		return ;
		
	} // end method swapElements
	
} // end class StanVectorTools
