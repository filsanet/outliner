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
import java.util.*;

import com.organic.maynard.io.*;
import com.organic.maynard.util.string.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;

import com.organic.maynard.xml.*;

public class XMLCheckerFileContentsHandler extends FileContentsInspector {

	private SAXParser parser = new SAXParser();
	private String checkType = null;
	
	// Constructors
	public XMLCheckerFileContentsHandler(String checkType, String lineEnding) {
		super(lineEnding);
		
		// Setup the parser
		parser.setErrorHandler(new SimpleSAXErrorHandler("    "));
		setCheckType(checkType);

	}


	// Accessors
	public String getCheckType() {return checkType;}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
		try {
			if (checkType.equals(XMLChecker.VALID)) {
				parser.setFeature("http://xml.org/sax/features/validation", true);
			} else {
				parser.setFeature("http://xml.org/sax/features/validation", false);			
			}
		} catch (Exception e) {
			System.out.println("Error setting up parser.");
			e.printStackTrace();
		}
	}

	// Overridden Methods
	protected void inspectContents(File file, String contents) {

		if (checkType.equals(XMLChecker.VALID)) {
			System.out.println("  START VALIDATION FOR FILE: " + file.getPath());
			doParse(contents);
			System.out.println("  END VALIDATION");
		} else if (checkType.equals(XMLChecker.WELL_FORMED)) {
			System.out.println("  START WELL-FORMEDNESS CHECK FOR FILE: " + file.getPath());
			doParse(contents);
			System.out.println("  END WELL-FORMEDNESS CHECK");
		}
		System.out.println("");
	}
	
	private void doParse(String contents) {
		try {
			InputSource xmlParserSource = new InputSource(new StringReader(contents));
			parser.parse(xmlParserSource);
		} catch (IOException ioe) {
			System.out.println("IOException when running parser.");
			ioe.printStackTrace();
			System.out.println("IOException when running parser.");
		} catch (SAXException se) {
			//System.out.println("SAXException when running parser." + se);
			//se.printStackTrace();
			//System.out.println("SAXException when running parser.");
		}
	}
}