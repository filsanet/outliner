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

import com.organic.maynard.util.string.StringTools;
import com.organic.maynard.util.string.StringSplitter;
import java.util.*;

public class DocumentInfo {
	
	// Constants
	public static final String EXPANDED_NODE_SEPERATOR = ",";
	
	// Instance Fields		
	private String fileFormat = null;
	private String encodingType = null;
	private String lineEnding = null;
	private String padding = null;
	private String path = null;
	private String title = null;
	private String dateCreated = null;
	private String dateModified = null;
	private String ownerName = null;
	private String ownerEmail = null;
	private int verticalScrollState = 1;
	private int windowTop = 0;
	private int windowLeft = 0;
	private int windowBottom = 0;
	private int windowRight = 0;
	private Vector expandedNodes = new Vector(); // Should only store Integers
	
	// The Constructors
	public DocumentInfo() {
		this(
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			1,
			OutlinerDocument.INITIAL_Y,
			OutlinerDocument.INITIAL_X,
			OutlinerDocument.INITIAL_Y + OutlinerDocument.INITIAL_HEIGHT,
			OutlinerDocument.INITIAL_X + OutlinerDocument.INITIAL_WIDTH,
			""
		);
	}
	
	public DocumentInfo(
		String fileFormat,
		String encodingType,
		String lineEnding,
		String padding,
		String path, 
		String title, 
		String dateCreated,
		String dateModified,
		String ownerName,
		String ownerEmail,
		int verticalScrollState,
		int windowTop,
		int windowLeft,
		int windowBottom,
		int windowRight,
		String expandedNodesString) 
	{
		setFileFormat(fileFormat);
		setEncodingType(encodingType);
		setLineEnding(lineEnding);
		setPadding(padding);
		setPath(path);
		setTitle(title);
		setDateCreated(dateCreated);
		setDateModified(dateModified);
		setOwnerName(ownerName);
		setOwnerEmail(ownerEmail);
		setVerticalScrollState(verticalScrollState);
		setWindowTop(windowTop);
		setWindowLeft(windowLeft);
		setWindowBottom(windowBottom);
		setWindowRight(windowRight);
		setExpandedNodesString(expandedNodesString);
	}

	// Accessors
	public String getFileFormat() {return this.fileFormat;}
	public void setFileFormat(String fileFormat) {this.fileFormat = fileFormat;}

	public String getEncodingType() {return this.encodingType;}
	public void setEncodingType(String encodingType) {this.encodingType = encodingType;}

	public String getLineEnding() {return this.lineEnding;}
	public void setLineEnding(String lineEnding) {this.lineEnding = lineEnding;}

	public String getPadding() {return this.padding;}
	public void setPadding(String padding) {this.padding = padding;}

	public String getPath() {return this.path;}
	public void setPath(String path) {this.path = path;}

	public String getTitle() {return this.title;}
	public void setTitle(String title) {this.title = title;}

	public String getDateCreated() {return this.dateCreated;}
	public void setDateCreated(String dateCreated) {this.dateCreated = dateCreated;}

	public String getDateModified() {return this.dateModified;}
	public void setDateModified(String dateModified) {this.dateModified = dateModified;}

	public String getOwnerName() {return this.ownerName;}
	public void setOwnerName(String ownerName) {this.ownerName = ownerName;}

	public String getOwnerEmail() {return this.ownerEmail;}
	public void setOwnerEmail(String ownerEmail) {this.ownerEmail = ownerEmail;}

	public int getVerticalScrollState() {return this.verticalScrollState;}
	public void setVerticalScrollState(int verticalScrollState) {
		if (verticalScrollState >= 1) {
			this.verticalScrollState = verticalScrollState;
		} else {
			this.verticalScrollState = 1;
		}
	}
	
	public int getWindowTop() {return this.windowTop;}
	public void setWindowTop(int windowTop) {
		if ((windowTop >= 0) && (windowTop <= 10000)) {
			this.windowTop = windowTop;
		} else {
			this.windowTop = 0;
		}
	}

	public int getWindowLeft() {return this.windowLeft;}
	public void setWindowLeft(int windowLeft) {
		if ((windowLeft >= 0) && (windowLeft <= 10000)) {
			this.windowLeft = windowLeft;
		} else {
			this.windowLeft = 0;
		}
	}
	
	public int getWindowBottom() {return this.windowBottom;}
	public void setWindowBottom(int windowBottom) {
		if ((windowBottom - windowTop >= OutlinerDocument.MIN_HEIGHT) && (windowBottom <= 10000 + OutlinerDocument.INITIAL_HEIGHT)) {
			this.windowBottom = windowBottom;
		} else {
			this.windowBottom = windowTop + OutlinerDocument.INITIAL_HEIGHT;
		}
	}

	public int getWindowRight() {return this.windowRight;}
	public void setWindowRight(int windowRight) {
		if ((windowRight - windowLeft >= OutlinerDocument.MIN_WIDTH) && (windowRight <= 10000 + OutlinerDocument.INITIAL_WIDTH)) {
			this.windowRight = windowRight;
		} else {
			this.windowRight = windowLeft + OutlinerDocument.INITIAL_WIDTH;
		}
	}

	public Vector getExpandedNodes() {return this.expandedNodes;}
	public boolean setExpandedNodes(Vector expandedNodes) {
		// Make sure all values are Integers
		for (int i = 0; i < expandedNodes.size(); i++) {
			try {
				Integer temp = (Integer) expandedNodes.elementAt(i);
			} catch (ClassCastException e) {
				return false;
			}
		}
		
		this.expandedNodes = expandedNodes;
		return true;
	}
	
	public String getExpandedNodesString() {
		return getExpandedNodesStringShifted(0);
	}
	
	public String getExpandedNodesStringShifted(int shift) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < expandedNodes.size(); i++) {
			buf.append("" + (((Integer) expandedNodes.elementAt(i)).intValue() + shift));
			if (i < expandedNodes.size() - 1) {
				buf.append(EXPANDED_NODE_SEPERATOR);
			}
		}
		return buf.toString();
	}
	
	
	public void setExpandedNodesString(String nodeList) {
		setExpandedNodesStringShifted(nodeList, 0);
	}

	public void setExpandedNodesStringShifted(String nodeList, int shift) {
		// Clear out the current expandedNodes Vector
		getExpandedNodes().clear();
		
		// Load it up with Integers
		StringSplitter splitter = new StringSplitter(nodeList,EXPANDED_NODE_SEPERATOR); 
		while (splitter.hasMoreElements()) {
			addExpandedNodeNumShifted((String) splitter.nextElement(), shift);
		}
	}
	
	public boolean addExpandedNodeNum(String nodeNum) {
		return addExpandedNodeNumShifted(nodeNum, 0);
	}

	public boolean addExpandedNodeNumShifted(String nodeNum, int shift) {
		try {
			return addExpandedNodeNum(Integer.parseInt(nodeNum) + shift);
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean addExpandedNodeNum(int nodeNum) {
		int lastIntOnList = -1;
		try {
			lastIntOnList = ((Integer) expandedNodes.lastElement()).intValue();
		} catch (NoSuchElementException e) {}
		if (nodeNum > lastIntOnList) {
			expandedNodes.add(new Integer(nodeNum));
			return true;
		}
		return false;
	}
	
	// Additional Accessors
	public int getWidth() {return getWindowRight() - getWindowLeft();}
	public int getHeight() {return getWindowBottom() - getWindowTop();}
	
	// Utility Methods
	public String toEncodedString(String seperator, char escapeChar) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(StringTools.escape(getFileFormat(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getEncodingType(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getLineEnding(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getPadding(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getPath(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getTitle(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getDateCreated(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getDateModified(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getOwnerName(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape(getOwnerEmail(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getVerticalScrollState(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getWindowTop(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getWindowLeft(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getWindowBottom(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getWindowRight(), escapeChar, null));
		buffer.append(Outliner.COMMAND_PARSER_SEPARATOR);

		buffer.append(StringTools.escape("" + getExpandedNodesString(), escapeChar, null));
		
		return buffer.toString();
	}
}