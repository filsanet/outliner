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
 
package com.organic.maynard.io;

import java.io.*;
import java.util.*;

public class FileTools {
	// Constants
	public static final String LINE_ENDING_UNIX = "\n";
	public static final String LINE_ENDING_WIN = "\r\n";
	public static final String LINE_ENDING_MAC = "\r";

	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	// Constructors
	public FileTools() {}


	// Class Methods
	public static String readFileToString(File file) {
		return readFileToString(file, DEFAULT_ENCODING, LINE_ENDING_UNIX);
	}

	public static String readFileToString(File file, String lineEnding) {
		return readFileToString(file, DEFAULT_ENCODING, lineEnding);
	}
	
	public static String readFileToString(File file, String encoding, String lineEnding) {
		StringBuffer text = new StringBuffer("");
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(fileInputStream, encoding));
			
			boolean eof = false;
			while (!eof) {
				String theLine = buffer.readLine();
				if (theLine == null) {
					eof = true;
				} else {
					text.append(theLine).append(lineEnding);
				}
			}
			
			fileInputStream.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return text.toString();
	}

	public static void dumpStringToFile(File file, String text) {
		dumpStringToFile(file, text, DEFAULT_ENCODING);
	}
	
	public static void dumpStringToFile(File file, String text, String encoding) {
		try {
			// Create Parent Directories
			File parent = new File(file.getParent());
			parent.mkdirs();
			
			// Write the File
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), encoding);
			outputStreamWriter.write(text);
			outputStreamWriter.flush();
			outputStreamWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copy(File from, File to) throws IOException {
		Vector fromFileList = new Vector();
		Vector toFileList = new Vector();
		
		fromFileList.add(from);
		toFileList.add(to);
		
		String rootTo = "";
		String rootFrom = "";
		if (from.isDirectory()) {
			rootTo = to.getAbsolutePath();
			rootFrom = from.getAbsolutePath();
		}
		
		int i = 0;
		
		while (true) {
			if (i >= fromFileList.size()) {
				break;
			}
			
			File fromFile = (File) fromFileList.get(i);
			File toFile = (File) toFileList.get(i);
			i++;
			
			if (fromFile.isDirectory()) {
				toFile.mkdirs();
				
				//System.out.println("Dir: " + fromFile.getAbsolutePath() + ":" + toFile.getAbsolutePath());
				
				File[] files = fromFile.listFiles();
				for (int j = 0; j < files.length; j++) {
					File newFromFile = files[j];
					fromFileList.add(newFromFile);
					String newFromPath = newFromFile.getAbsolutePath();
					String newPath = newFromPath.substring(rootFrom.length(),newFromPath.length());
					File newToFile = new File(rootTo + newPath);
					toFileList.add(newToFile);
				}
				
			} else if (fromFile.isFile()) {
				//System.out.println("File: " + fromFile.getAbsolutePath() + ":" + toFile.getAbsolutePath());
				FileInputStream sFrom = new FileInputStream(fromFile);
				FileOutputStream sTo = new FileOutputStream(toFile);
				copy(sFrom, sTo);
				sFrom.close();
				sTo.flush();
				sTo.close();
			} else {
			
			}
		}
	}
	
	public static void copy(InputStream in, OutputStream out) throws IOException {
		synchronized (in) {
			synchronized (out) {
				byte [] buffer = new byte[256];
				while (true) {
					int bytesread = in.read(buffer);
					if (bytesread == -1) {
						break;
					}
					out.write(buffer, 0, bytesread);
				}
			}
		}
	} 
}

