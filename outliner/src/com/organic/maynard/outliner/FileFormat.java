/**
 * FileFormat interface		
 * 
 * Interface for file format stuff that's common to both OpenFileFormat and SaveFileFormat interfaces
 * 
 * members
 *	interfaces
 * 		instance
 * 			public
 * 				void addExtension(String, boolean)
 * 				void removeExtension(String);
 * 				String getDefaultExtension();
 * 				Iterator getExtensions();
 * 				boolean extensionExists(String);
 * 
 * 				boolean supportsComments();
 * 				boolean supportsEditability();
 * 				boolean supportsMoveability();
 * 				boolean supportsAttributes();
 * 				boolean supportsDocumentAttributes();
 * 
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
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
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.outliner;

import java.util.*;

public interface FileFormat { 
	
	// File Extension methods
	
	public void addExtension(String ext, boolean isDefault);
	public void removeExtension(String ext);
	
	public Iterator getExtensions();
	public String getDefaultExtension();

	public boolean extensionExists(String ext);
	
	
	// supportsXxxxx methods
	
	// can we store comment attributes in this file format ?
	public boolean supportsComments();

	// can we store editability attributes in this file format ?
	public boolean supportsEditability();

	// can we store moveability attributes in this file format ?
	public boolean supportsMoveability();
	
	// can we store node attributes in this file format ?
	public boolean supportsAttributes();

	// can we store document attributes in this file format ?
	public boolean supportsDocumentAttributes();
}