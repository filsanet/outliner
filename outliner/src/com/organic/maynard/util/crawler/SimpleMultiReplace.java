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
 * IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard;

import java.io.*;
import java.util.*;

import com.organic.maynard.io.*;
import com.organic.maynard.util.*;
import com.organic.maynard.util.crawler.*;
import com.organic.maynard.util.string.StringTools;

public class SimpleMultiReplace {
	
	// Constants
	public static final String COMMAND_PARSER_SEPARATOR = "\t";
	public static final String COMMAND_SET = "set";
	
	public static final String COMMAND_START_PATH = "start_path";
	public static final String COMMAND_MATCH = "match";
	public static final String COMMAND_FILE_EXTENSION = "file_ext";
	public static final String COMMAND_LINE_ENDING = "line_ending";

	public static final String PLATFORM_MAC = "mac";
	public static final String PLATFORM_WIN = "win";
	public static final String PLATFORM_UNIX = "unix";

	// Configuration Variables
	public Vector matches = new Vector();
	public Vector replacements = new Vector();
	public String[] fileExtensions;
	public String startingPath = null;
	public String lineEnding = FileTools.LINE_ENDING_WIN;
	
	public boolean blockSetStartingPath = false;
			
	public SimpleMultiReplace(String args[]) {
		
		// Get argument for configFile
		String configPath = null;
		try {configPath = args[0];} catch (ArrayIndexOutOfBoundsException e) {}
		
		if (configPath != null) {
		
			// Set startpath from command line if it was provided
			String startPathFromArgs = null;
			try {startPathFromArgs = args[1];} catch (ArrayIndexOutOfBoundsException e) {}
			
			if (startPathFromArgs != null) {
				this.startingPath = startPathFromArgs;
				// Block any further configuration of the start path so the config file doesn't change it.
				blockSetStartingPath = true;
			}

			// Get input from a configFile
			CommandParser parser = new CommandParser(COMMAND_PARSER_SEPARATOR);
			parser.addCommand(new SimpleMultiReplaceConfigCommand(COMMAND_SET,2,this));
	
			// Load things from the config file
			CommandQueue commandQueue = new CommandQueue(30);
			commandQueue.loadFromFile(configPath);
			
			while (commandQueue.getSize() > 0) {
				try {
					parser.parse((String) commandQueue.getNext());
				} catch (UnknownCommandException uce) {
					System.out.println("Unknown Command");
				}
			}
		} else {
			// Get input from the console
			startingPath = ConsoleTools.getNonEmptyInput("Enter starting path: ");
			
			System.out.println("Enter multiple matches/replacements below. When done, enter an empty response to proceed.");
			boolean done = false;
			while (!done) {
				String match = ConsoleTools.getNonNullInput("  Match: ");
				
				if (match.equals("")) {
					if (matches.size() > 0) {
						done = true;
					}
					continue;
				}
	
				String replacement = ConsoleTools.getNonNullInput("  Replace: ");
				
				matches.add(match);
				replacements.add(replacement);
			}
			
			fileExtensions = ConsoleTools.getSeriesOfInputs("Enter file extension to match: ");
			while (fileExtensions.length <= 0) {
				fileExtensions = ConsoleTools.getSeriesOfInputs("Enter file extension to match: ");
			}
			System.out.println("");
		}
		
		// Setup the Crawler
		DirectoryCrawler crawler = new DirectoryCrawler();
		crawler.setFileHandler(new SimpleMultiReplacementFileContentsHandler(matches,replacements,lineEnding));
		crawler.setFileFilter(new FileExtensionFilter(fileExtensions));
		
		// Do the Crawl
		System.out.println("STARTING...");
		crawler.crawl(startingPath);
		System.out.println("DONE");
	}

	
	public static void main(String args[]) {
		SimpleMultiReplace sr = new SimpleMultiReplace(args);
	}
}

public class SimpleMultiReplacementFileContentsHandler extends FileContentsHandler {
	private Vector matches = null;
	private Vector replacements = null;
	
	
	// Constructors
	public SimpleMultiReplacementFileContentsHandler(Vector matches, Vector replacements, String lineEnding) {
		super(lineEnding);
		setMatches(matches);
		setReplacements(replacements);
	}


	// Accessors
	//public String getMatch() {return match;}
	public void setMatches(Vector matches) {this.matches = matches;}

	//public String getReplacement() {return replacement;}
	public void setReplacements(Vector replacements) {this.replacements = replacements;}
	
	
	// Overridden Methods
	protected String processContents(String contents) {
		for (int i = 0; i < matches.size(); i++) {
			String match = (String) matches.get(i);
			String replacement = (String) replacements.get(i);
			contents = StringTools.replace(contents, match, replacement);
		}
		//return StringTools.replace(contents, match, replacement);
		return contents;
	}
}


public class SimpleMultiReplaceConfigCommand extends Command {
	public SimpleMultiReplace app = null;
	
	// The Constructors
	public SimpleMultiReplaceConfigCommand(String name, int numOfArgs, SimpleMultiReplace app) {
		super(name,numOfArgs);
		this.app = app;
	}

	public synchronized void execute(Vector signature) {
		String variableName = (String) signature.elementAt(1);
		
		if (variableName.equals(SimpleMultiReplace.COMMAND_START_PATH)) {
			if (!app.blockSetStartingPath) {
				app.startingPath = (String) signature.elementAt(2);
			}
		} else if (variableName.equals(SimpleMultiReplace.COMMAND_MATCH)) {
			app.matches.add((String) signature.elementAt(2));
			app.replacements.add((String) signature.elementAt(3));
		} else if (variableName.equals(SimpleMultiReplace.COMMAND_LINE_ENDING)) {
			String lineEndingType = (String) signature.elementAt(2);
			if (lineEndingType.equals(SimpleMultiReplace.PLATFORM_MAC)) {
				app.lineEnding = FileTools.LINE_ENDING_MAC;
			} else if (lineEndingType.equals(SimpleMultiReplace.PLATFORM_WIN)) {
				app.lineEnding = FileTools.LINE_ENDING_WIN;
			} else if (lineEndingType.equals(SimpleMultiReplace.PLATFORM_UNIX)) {
				app.lineEnding = FileTools.LINE_ENDING_UNIX;
			}
		} else if (variableName.equals(SimpleMultiReplace.COMMAND_FILE_EXTENSION)) {
			String fileExtension = (String) signature.elementAt(2);
			
			String[] newArray;
			if (app.fileExtensions != null) {
				newArray = new String[app.fileExtensions.length + 1];
				System.arraycopy(app.fileExtensions,0,newArray,0,app.fileExtensions.length);
				newArray[app.fileExtensions.length] = fileExtension;
			} else {
				newArray = new String[1];
				newArray[0] = fileExtension;
			}
			app.fileExtensions = newArray;
		}
	}
}