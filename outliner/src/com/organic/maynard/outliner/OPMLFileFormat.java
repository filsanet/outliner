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

import java.io.*;
import com.organic.maynard.util.string.StringTools;

public class OPMLFileFormat implements SaveFileFormat, OpenFileFormat {
	
	// Constructors
	public OPMLFileFormat() {}

	
	// SaveFileFormat Interface
	public boolean save(TreeContext tree, DocumentInfo docInfo) {
		return FileFormatManager.writeFile(
			docInfo.getPath(), 
			docInfo.getEncodingType(), 
			prepareFile(tree, docInfo)
		);
	}

	private String prepareFile(TreeContext tree, DocumentInfo docInfo) {
		String lineEnding = Preferences.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"" + docInfo.getEncodingType() + "\" ?>" + lineEnding);
		buf.append("<opml version=\"1.0\">" + lineEnding);
		
		buf.append("<head>" + lineEnding);

		buf.append("<title>" + escapeXMLText(docInfo.getTitle()) + "</title>" + lineEnding);
		buf.append("<dateCreated>" + escapeXMLText(docInfo.getDateCreated()) + "</dateCreated>" + lineEnding);
		buf.append("<dateModified>" + escapeXMLText(docInfo.getDateModified()) + "</dateModified>" + lineEnding);
		buf.append("<ownerName>" + escapeXMLText(docInfo.getOwnerName()) + "</ownerName>" + lineEnding);
		buf.append("<ownerEmail>" + escapeXMLText(docInfo.getOwnerEmail()) + "</ownerEmail>" + lineEnding);
		buf.append("<expansionState>" + escapeXMLText(docInfo.getExpandedNodesString()) + "</expansionState>" + lineEnding);
		buf.append("<vertScrollState>" + escapeXMLText("" + docInfo.getVerticalScrollState()) + "</vertScrollState>" + lineEnding);
		buf.append("<windowTop>" + escapeXMLText("" + docInfo.getWindowTop()) + "</windowTop>" + lineEnding);
		buf.append("<windowLeft>" + escapeXMLText("" + docInfo.getWindowLeft()) + "</windowLeft>" + lineEnding);
		buf.append("<windowBottom>" + escapeXMLText("" + docInfo.getWindowBottom()) + "</windowBottom>" + lineEnding);
		buf.append("<windowRight>" + escapeXMLText("" + docInfo.getWindowRight()) + "</windowRight>" + lineEnding);

		buf.append("</head>" + lineEnding);

		buildOutliner(tree.getRootNode(),lineEnding, buf);
		
		buf.append("</opml>" + lineEnding);
		return buf.toString();
	}

	private void buildOutliner(Node node, String lineEnding, StringBuffer buf) {
		if (node.isRoot()) {
			buf.append("<body>" + lineEnding);
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutliner(node.getChild(i), lineEnding, buf);
			}
			buf.append("</body>" + lineEnding);
		} else if (node.isLeaf()) {
			buf.append("<outline text=\"" + escapeXMLAttribute(node.getValue()) + "\"/>" + lineEnding);
		} else {
			buf.append("<outline text=\"" + escapeXMLAttribute(node.getValue()) + "\">" + lineEnding);
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutliner(node.getChild(i), lineEnding, buf);
			}
			buf.append("</outline>" + lineEnding);		
		}
	}
	
	private String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "\"", "&quot;");
		return text;
	}

	private String escapeXMLText(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "\"", "&quot;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, ">", "&gt;");
		return text;
	}


	
	// OpenFileFormat Interface
	public boolean open(TreeContext tree, DocumentInfo docInfo) {
		boolean success = false;

		String text = FileFormatManager.loadFile(docInfo.getPath(), docInfo.getEncodingType());
		if (text != null) {
			tree.setRootNode(PadSelection.pad(text, tree, 0,Preferences.LINE_END_UNIX));
			success = true;
		} else {
			success = false;
		}
				
		return success;
	}
}