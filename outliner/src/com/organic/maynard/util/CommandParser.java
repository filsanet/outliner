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

package com.organic.maynard.util;

import java.util.*;
import com.organic.maynard.util.string.*;

public class CommandParser{

	private Hashtable commands = new Hashtable();

	public static final String WHITESPACE = " \t\r\n";
	public static final char DEFAULT_ESCAPE_CHAR = '\\';
	public static final String COMMENT_CHAR = "#";

	private String delimiters = null;
	private char escapeChar = DEFAULT_ESCAPE_CHAR;
	
	// The Constructors
	public CommandParser() {
		this(WHITESPACE);
	}

	public CommandParser(String delimiters) {
		this(delimiters, DEFAULT_ESCAPE_CHAR);
	}

	public CommandParser(String delimiters, char escapeChar) {
		this.delimiters = delimiters;
		this.escapeChar = escapeChar;
	}

	// Parse Methods
	public void parse(String input) throws UnknownCommandException {
		parse(input, true, true);
	}

	public void parse(String input, boolean trimWhitespace, boolean useComments) throws UnknownCommandException {
		Vector commandSignature = new Vector();
		
		// Strip whitespace
		if (trimWhitespace) {
			input.trim();
		}
		
		// Ignore Comments
		if (useComments) {
			if ((input.startsWith(COMMENT_CHAR)) || (input.length() <= 0)) {
				return;
			}
		}
		
		// Break it down into pieces
		commandSignature = StringTools.split(input, escapeChar, delimiters.toCharArray());
			/*StringTokenizer tok = new StringTokenizer(input,delimiters);
			while (tok.hasMoreElements()) {
				commandSignature.addElement(tok.nextElement());
			}*/

		
		// Find the appropriate command and execute it
		if (commandSignature.size() > 0) {
			// Lookup the command
			Command command = (Command) commands.get((String) commandSignature.firstElement());
			if (command != null) {
				command.execute(commandSignature);
			} else {
				UnknownCommandException uce = new UnknownCommandException();
				throw uce;
			}
		}
	}
	
	// The Accessors
	public synchronized void addCommand(Command command) {
		commands.put(command.getName(),command);
	}
	
	public synchronized void removeCommand(Command command) {
		removeCommand(command.getName());
	}
	
	public synchronized void removeCommand(String key) {
		commands.remove(key);
	}
	
	public synchronized Command getCommand(String key) {
		return (Command) commands.get(key);
	}
	
	public synchronized void setDelimiters(String delimiters) {
		if (delimiters.length() > 0) {
			this.delimiters = delimiters;
		} else {
			this.delimiters = WHITESPACE;
		}
	}
	
	public synchronized String getDelimiters() {
		return this.delimiters;
	}
}