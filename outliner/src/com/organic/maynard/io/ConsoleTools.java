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
 
package com.organic.maynard.io;

import java.io.*;
import java.util.*;

public class ConsoleTools {
	// Constants
	
	
	// Constructors
	public ConsoleTools() {}


	// Class Methods
	public static String getInput(String query) {
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print(query);
		try {
			return console.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}
	
	public static String getNonEmptyInput(String query) {
		String response = ConsoleTools.getInput(query);
		while (response == null || response.equals("")) {
			response = ConsoleTools.getInput(query);
		}
		return response;	
	}

	public static int getIntInput(String query) {
		int response = 0;
		boolean done = false;
		do {
			try {
				response = new Integer(ConsoleTools.getInput(query)).intValue();
				done = true;
			} catch (Exception e) {}
		} while (!done);
		
		return response;	
	}

	public static String getNonNullInput(String query) {
		String response = ConsoleTools.getInput(query);
		while (response == null) {
			response = ConsoleTools.getInput(query);
		}
		return response;	
	}

	public static String[] getSeriesOfInputs(String query) {
		System.out.println("Enter multiple values below. When done, enter an empty response to proceed.");
		
		ArrayList responseList = new ArrayList();
		
		boolean done = false;
		while (!done) {
			String response = ConsoleTools.getInput("  " + query);
			if (response == null) {
				done = true;
			} else if (response.equals("")) {
				done = true;
			} else {
				responseList.add(response);
			}
		}
		
		String[] responseArray = new String[responseList.size()];
		for (int i = 0; i < responseList.size(); i++) {
			responseArray[i] = (String) responseList.get(i);
		}

		return responseArray;	
	}
}
