/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util.crawler;

import java.io.*;
import com.organic.maynard.io.*;

public class FileContentsHandler implements FileHandler {

	// Declare Fields
	private String lineEnding = null;
	private boolean lineEndingAtEnd = true;
	
	
	// Constructors
	public FileContentsHandler(String lineEnding) {
		this(lineEnding, true);
	}

	public FileContentsHandler(String lineEnding, boolean lineEndingAtEnd) {
		setLineEnding(lineEnding);
		setLineEndingAtEnd(lineEndingAtEnd);
	}
	
	
	// Accessors
	public String getLineEnding() {return lineEnding;}
	public void setLineEnding(String lineEnding) {this.lineEnding = lineEnding;}

	public boolean getLineEndingAtEnd() {return lineEndingAtEnd;}
	public void setLineEndingAtEnd(boolean lineEndingAtEnd) {this.lineEndingAtEnd = lineEndingAtEnd;}

	
	// FileHandler Interface
	public void handleFile(File file) {
		String contents = FileTools.readFileToString(file, lineEnding);
		contents = processContents(contents);
		
		// Clean last line ending if neccessary.
		int lineEndingLength = lineEnding.length();
		int contentsLength = contents.length();
		if (!lineEndingAtEnd && (contentsLength >= lineEndingLength)) {
			contents = contents.substring(0, contentsLength - lineEndingLength);
		}
		
		FileTools.dumpStringToFile(file, contents);
	}
	
	protected String processContents(String contents) {
		System.out.println("Contents: " + contents);
		return contents;
	}
}