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
 
package com.organic.maynard.outliner;

import javax.swing.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;

public class HTMLExportFileFormat 

	implements ExportFileFormat, JoeReturnCodes {
	
	// Constructors
	public HTMLExportFileFormat() {}

	
	// ExportFileFormat Interface
	public boolean supportsComments() {return false;}
	public boolean supportsEditability() {return false;}
	public boolean supportsMoveability() {return false;}
	public boolean supportsAttributes() {return false;}
	public boolean supportsDocumentAttributes() {return false;}
	
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}

	private StringBuffer prepareFile(JoeTree tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">").append(lineEnding);
		buf.append("<html>").append(lineEnding);
		buf.append("<head>").append(lineEnding);
		buf.append("<title>").append(escape(docInfo.getPath())).append("</title>").append(lineEnding);
		buf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(escape(docInfo.getEncodingType())).append("\">").append(lineEnding);


		buf.append("<style type=\"text/css\">").append(lineEnding);
		buf.append("	.indented {").append(lineEnding);
		buf.append("		margin-left: 15pt;").append(lineEnding);
		buf.append("		margin-top: 3pt;").append(lineEnding);
		buf.append("		margin-bottom: 3pt;").append(lineEnding);
		buf.append("	}").append(lineEnding);
		buf.append("</style>").append(lineEnding);


		buf.append("</head>").append(lineEnding);
		
		buf.append("<body>").append(lineEnding);

		Node node = tree.getRootNode();
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildOutlineElement(node.getChild(i), lineEnding, buf);
		}

		buf.append("<div>").append(lineEnding);
		buf.append("Date Created: ").append(escape(docInfo.getDateCreated())).append("<br />").append(lineEnding);
		buf.append("Date Modified: ").append(escape(docInfo.getDateModified())).append("<br />").append(lineEnding);
		buf.append("Owner Name: ").append(escape(docInfo.getOwnerName())).append("<br />").append(lineEnding);
		buf.append("Owner Email: ").append(escape(docInfo.getOwnerEmail())).append("<br />").append(lineEnding);
		buf.append("</div>").append(lineEnding);
		
		buf.append("</body>").append(lineEnding);
		
		buf.append("</html>").append(lineEnding);

		return buf;
	}

	private void buildOutlineElement(Node node, String lineEnding, StringBuffer buf) {
		indent(node, buf);
		buf.append("<div class=\"indented\">").append(escape(node.getValue())).append(lineEnding);
		
		if (!node.isLeaf()) {
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}	
		}
		indent(node, buf);
		buf.append("</div>").append(lineEnding);
	}
	
	private void indent(Node node, StringBuffer buf) {
		for (int i = 0; i < node.getDepth(); i++) {
			buf.append("\t");
		}
	}

	private String escape(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		text = StringTools.replace(text, ">", "&gt;");
		return text;
	}
	
	// File Extensions
	private HashMap extensions = new HashMap();
	
	public void addExtension(String ext, boolean isDefault) {
		extensions.put(ext, new Boolean(isDefault));
	}
	
	public void removeExtension(String ext) {
		extensions.remove(ext);
	}
	
	public String getDefaultExtension() {
		Iterator i = getExtensions();
		while (i.hasNext()) {
			String key = (String) i.next();
			Boolean value = (Boolean) extensions.get(key);
			
			if (value.booleanValue()) {
				return key;
			}
		}
		
		return null;
	}
	
	public Iterator getExtensions() {
		return extensions.keySet().iterator();
	}
	
	public boolean extensionExists(String ext) {
		Iterator it = getExtensions();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (ext.equals(key)) {
				return true;
			}
		}
		
		return false;
	}
}