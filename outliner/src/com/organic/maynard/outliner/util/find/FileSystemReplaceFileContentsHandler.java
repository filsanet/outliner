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
import com.organic.maynard.util.crawler.*;

import java.io.*;
import java.util.*;

import com.organic.maynard.io.*;
import com.organic.maynard.util.string.*;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.MatchResult;


/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */
 
public class FileSystemReplaceFileContentsHandler extends FileContentsHandler {

	private String query = null;
	private String replacement = null;
	private Perl5Util util = new Perl5Util();
	private PatternMatcherInput input = null;
	private MatchResult result = null;
	
	private FindReplaceResultsModel results = null;
	private boolean isRegexp;
	private boolean ignoreCase;
	
	
	// Constructors
	public FileSystemReplaceFileContentsHandler(String query, String replacement, FindReplaceResultsModel results, boolean isRegexp, boolean ignoreCase, String lineEnding) {
		super(lineEnding);
		this.results = results;
		this.isRegexp = isRegexp;
		this.ignoreCase = ignoreCase;
		
		if (!isRegexp && ignoreCase) {
			this.query = query.toLowerCase();
		} else {
			this.query = query;
		}
		
		this.replacement = replacement;
	}
	
	// Overridden Methods
	protected String processContents(File file, String contents) {
		StringBuffer buf = new StringBuffer();
		
		// Split it into lines
		StringSplitter splitter = new StringSplitter(contents, getLineEnding());
		
		// Scan each line
		int lineCount = 1;
		while (splitter.hasMoreElements()) {
			String line = (String) splitter.nextElement();
			
			if (isRegexp) {
				// Do the regex find and return result
				try {
					String textToSearch = line;
					int searchStartIndex = 0;
					input = new PatternMatcherInput(textToSearch);
					String replacementText = null;
					int matchLength = -1;
					int difference = 0;
					
					while (util.match(query, input)) {
						// Record Match Information
						result = util.getMatch();
						matchLength = result.length(); // Store length since this method does not return it.
						int matchStartIndex = result.beginOffset(0);
						int matchEndIndex = matchStartIndex + matchLength;
						
						// Do the replacement
						int originalLength = textToSearch.length();
						replacementText = util.substitute(replacement, textToSearch);

						// Calculate the difference
						difference = replacementText.length() - originalLength;
	
						// Record Replacement Information
						int replacementEndIndex = matchEndIndex + difference;

						// Apply change to line
						line = line.substring(0, searchStartIndex) + replacementText;
												
						// Add Result
						results.addResult(new FindReplaceResult(file, lineCount, searchStartIndex + matchStartIndex, result.group(0), replacementText.substring(matchStartIndex,replacementEndIndex)));
						
						// Prep for next match
						searchStartIndex += replacementEndIndex;
						
						textToSearch = line.substring(searchStartIndex, line.length());
						if (textToSearch.length() == 0) {
							break;
						}
						
						input = new PatternMatcherInput(textToSearch);
					}
				} catch (MalformedPerl5PatternException e) {
					System.out.println("MalformedPerl5PatternException: " + e.getMessage());
				}
			} else {
				String searchLine = null;
				if (ignoreCase) {
					searchLine = line.toLowerCase();
				} else {
					searchLine = line;
				}
				
				int start = 0;
				
				while (true) {
					// Record Match Information
					start = searchLine.indexOf(query, start);
					
					if (start != -1) {
						// Record Match
						String match = line.substring(start, start + query.length());
						
						// Apply change to line
						line = line.substring(0, start) + replacement + line.substring(start + query.length(), line.length());
						searchLine = searchLine.substring(0, start) + replacement + searchLine.substring(start + query.length(), searchLine.length());
						
						// Add Result
						results.addResult(new FindReplaceResult(file, lineCount, start, match, replacement));
						
						// Prep for next match
						start = start + replacement.length();
					} else {
						break;
					}
				}
			}
			
			buf.append(line).append(getLineEnding()); //TBD: preserve real line endings.
			
			lineCount++;
		}
		
		if (results.size() == 0) {
			// If we made no changes then don't write a re-write the file.
			return null;
		} else {
			return buf.toString();
		}
	}	
}