/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.*;

import java.util.*;
import java.io.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class FindReplaceResult {

	// Constants
	public static final int TYPE_DOC = 1;
	public static final int TYPE_FILE = 2;
	public static final int TYPE_UNKNOWN = -1;


	// Fields
	private int line = -1;
	private int start = -1;
	private String match = null;
	private String replacement = null;
	
	private OutlinerDocument doc = null;
	private File file = null;


	// Constructors
	public FindReplaceResult(File file, int line, int start, String match, String replacement) {
		setFile(file);
		setLine(line);
		setStart(start);
		setMatch(match);
		setReplacement(replacement);
	}

	public FindReplaceResult(OutlinerDocument doc, int line, int start, String match, String replacement) {
		setDocument(doc);
		setLine(line);
		setStart(start);
		setMatch(match);
		setReplacement(replacement);
	}


	// Accessors
	public int getType() {
		if (doc != null) {
			return TYPE_DOC;
		} else if (file != null) {
			return TYPE_FILE;
		} else {
			return TYPE_UNKNOWN;
		}
	}
	
	public void setFile(File file) {
		this.doc = null;
		this.file = file;
	}
	public File getFile() {return this.file;}
	
	public void setDocument(OutlinerDocument doc) {
		this.file = null;
		this.doc = doc;
	}
	public OutlinerDocument getDocument() {return this.doc;}
	
	public void setLine(int line) {this.line = line;}
	public int getLine() {return this.line;}

	public void setStart(int start) {this.start = start;}
	public int getStart() {return this.start;}

	public void setMatch(String match) {this.match = match;}
	public String getMatch() {return this.match;}
	
	public void setReplacement(String replacement) {this.replacement = replacement;}
	public String getReplacement() {return this.replacement;}
}
