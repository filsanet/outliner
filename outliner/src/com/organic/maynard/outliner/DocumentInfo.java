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

//import java.awt.*;
//import javax.swing.*;

public class DocumentInfo {
	
	// Instance Fields		
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
	
	// The Constructors
	public DocumentInfo() {
		this("","","","","","","","","",1,0,0,0,0);
	}
	
	public DocumentInfo(
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
		int windowRight) 
	{
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
	}

	// Accessors
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
	public void setVerticalScrollState(int verticalScrollState) {this.verticalScrollState = verticalScrollState;}

	public int getWindowTop() {return this.windowTop;}
	public void setWindowTop(int windowTop) {this.windowTop = windowTop;}

	public int getWindowLeft() {return this.windowLeft;}
	public void setWindowLeft(int windowLeft) {this.windowLeft = windowLeft;}

	public int getWindowBottom() {return this.windowBottom;}
	public void setWindowBottom(int windowBottom) {this.windowBottom = windowBottom;}

	public int getWindowRight() {return this.windowRight;}
	public void setWindowRight(int windowRight) {this.windowRight = windowRight;}

}