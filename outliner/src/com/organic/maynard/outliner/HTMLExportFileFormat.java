/**
 * Copyright (C) 2001 Maynard Demmon, maynard@organic.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
	
	public byte[] save(TreeContext tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(docInfo.getEncodingType());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}

	private StringBuffer prepareFile(TreeContext tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd\">").append(lineEnding);
		buf.append("<html>").append(lineEnding);
		buf.append("<head>").append(lineEnding);
		buf.append("<title>").append(escape(docInfo.getPath())).append("</title>").append(lineEnding);
		buf.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(escape(docInfo.getEncodingType())).append("\">").append(lineEnding);


		buf.append("<style type=\"text/css\">").append(lineEnding);
		buf.append("	.indented {").append(lineEnding);
		buf.append("		margin-left: 20pt;").append(lineEnding);
		buf.append("		margin-bottom: 10pt;").append(lineEnding);
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