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
	
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char EOF = (char) -1;
	
	public static final String DEFAULT_ENCODING = "ISO-8859-1";
	
	// Constructors
	public FileTools() {}
	
	
	// Class Methods
	public static void dumpArrayOfLinesToFile(File file, String encoding, ArrayList lines, ArrayList lineEndings) {
		try {
			// Create Parent Directories
			File parent = new File(file.getParent());
			parent.mkdirs();
			
			// Write the File
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				String lineEnding = (String) lineEndings.get(i);
				if (line != null) {
					out.write(line);
				}
				if (lineEnding != null) {
					out.write(lineEnding);
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Since we typically scan many many files repeatedly lets keep the object instantiation down to a minimum.
	// These are not currently threadsafe, but for now that shouldn't be a problem. In the future, we should
	// create an instance of an object that does readFileToArrayOfLines so we can have as many copies (1 per thread) as
	// neccessary.
	private static final int BUFFER_SIZE = 8192;
	private static final StringBuffer fileBuf = new StringBuffer();
	private static final StringBuffer lineBuf = new StringBuffer();
	private static final char[] in = new char[BUFFER_SIZE];
	
	public static boolean readFileToArrayOfLines(File file, String encoding, ArrayList lines, ArrayList lineEndings) {
		try {
			// First read the contents of the file into a StringBuffer.
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			
			// Enhancement Opportunity: it should be possible to eliminate the fileBuf since we could process 
			// characters directly from the char[] in. Gotta be careful with boundary issues on the lookahead though.
			fileBuf.setLength(0);
			
			while (true) {
				int result = reader.read(in, 0, BUFFER_SIZE);
				if (result == -1) {
					break;
				} else {
					fileBuf.append(in, 0, result);
				}
			}
			
			// Now Process the contents into ArrayLists
			lineBuf.setLength(0);
			
			for (int i = 0, limit = fileBuf.length(); i < limit; i++) {
				char c = fileBuf.charAt(i);
				
				switch (c) {
					case CR:
						lines.add(lineBuf.toString());
						lineBuf.setLength(0);
						
						i++;
						if (i < fileBuf.length()) {
							char lookahead = fileBuf.charAt(i);
							switch (lookahead) {
								case CR:
									lineEndings.add(LINE_ENDING_MAC);
									lines.add("");
									lineEndings.add(LINE_ENDING_MAC);
									break;
								
								case LF:
									lineEndings.add(LINE_ENDING_WIN);
									break;
								
								default:
									lineEndings.add(LINE_ENDING_MAC);
									lineBuf.append(lookahead);
									if (i == fileBuf.length() - 1) {
										lines.add(lineBuf.toString());
										lineEndings.add(null);
									}
									break;
							}
						}	
						break;

					case LF:
						lines.add(lineBuf.toString());
						lineBuf.setLength(0);
						lineEndings.add(LINE_ENDING_UNIX);
						break;
					
					default:
						lineBuf.append(c);
						if (i == fileBuf.length() - 1) {
							lines.add(lineBuf.toString());
							lineEndings.add(null);
						}
						break;
				}
			}
			reader.close();
			
			return true;
		} catch (FileNotFoundException fnfe) {
			System.out.println("Error: FileTools.readFileToArrayOfLines: " + fnfe.getMessage());
		} catch (Exception e) {
			System.out.println("Error: FileTools.readFileToArrayOfLines: " + e.getMessage());
		}
		return false;
	}
	
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
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			out.write(text);
			out.flush();
			out.close();
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

	public static boolean writeObjectToFile(Object obj, String filename) {
		try {
			File file = new File(filename);
			File parent = file.getParentFile();
			
			if (parent != null && !parent.exists()) {
				boolean dirsCreated = parent.mkdirs();
				
				if (!dirsCreated) {
					System.out.println("ERROR: Unable to create parent directories for: " + file.getPath());
					return false;
				}
			}
			
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
			stream.writeObject(obj);
			stream.close();
			return true;
		} catch (IOException e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		}
	}

	public static Object ReadObjectFromFile(String filename) {
		Object obj = null;
		
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename));
			obj = stream.readObject();
			stream.close();
			
		} catch (OptionalDataException ode) {
			System.out.println("Exception: " + ode);
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + cnfe);
			
		} catch (FileNotFoundException fnfe) {
			System.out.println("Exception: " + fnfe);
			
		} catch (StreamCorruptedException sce) {
			System.out.println("Exception: " + sce);
					
		} catch (IOException ioe) {
			System.out.println("Exception: " + ioe);
					
		}
		
		return obj;
	}
}