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

import java.util.*;
import java.io.*;
import org.xml.sax.*;
import javax.swing.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class FindReplaceModel extends HandlerBase {

	// Constants
	protected static final String DEFAULT_NAME = "Default";
	
	private static final String E_ROOT = "root";
	private static final String E_ITEM = "item";

	private static final String A_NAME = "name";
	private static final String A_FIND = "find";
	private static final String A_REPLACE = "replace";
	private static final String A_START_AT_TOP = "start_at_top";
	private static final String A_WRAP_AROUND = "wrap_around";
	private static final String A_SELECTION_ONLY = "selection_only";
	private static final String A_IGNORE_CASE = "ignore_case";
	private static final String A_INCLUDE_READ_ONLY = "include_read_only";
	private static final String A_REGEXP = "regexp";

	
	// Fields
	private ArrayList names = new ArrayList(); // Strings

	private ArrayList finds = new ArrayList(); // Strings
	private ArrayList replaces = new ArrayList(); // Strings

	private ArrayList startAtTops = new ArrayList(); // Booleans
	private ArrayList wrapArounds = new ArrayList(); // Booleans
	private ArrayList selectionOnlys = new ArrayList(); // Booleans
	private ArrayList ignoreCases = new ArrayList(); // Booleans
	private ArrayList includeReadOnlys = new ArrayList(); // Booleans
	private ArrayList regExps = new ArrayList(); // Booleans
	
	
	// Constructors
	public FindReplaceModel() {
		Outliner.XML_PARSER.setDocumentHandler(this);

		try {
			FileInputStream fileInputStream = new FileInputStream(Outliner.FIND_REPLACE_FILE);
			Outliner.XML_PARSER.parse(new InputSource(fileInputStream));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Make sure size is at least 1.
		if (getSize() <= 0) {
			add(0,DEFAULT_NAME,"","",false,false,false,false,false,false);
		}
	}


	// Accessors
	public int getSize() {return names.size();}

	public void add(
		int i,
		String name,
		String find,
		String replace,
		boolean startAtTop,
		boolean wrapAround,
		boolean selectionOnly,
		boolean ignoreCase,
		boolean includeReadOnly,
		boolean regExp
	) {
		addName(i,name);
		addFind(i,find);
		addReplace(i,replace);
		addStartAtTop(i,startAtTop);
		addWrapAround(i,wrapAround);
		addSelectionOnly(i,selectionOnly);
		addIgnoreCase(i,ignoreCase);
		addIncludeReadOnly(i,includeReadOnly);
		addRegExp(i,regExp);
	}
		
	public void remove(int i) {
		// Remove from model
		names.remove(i);
		finds.remove(i);
		replaces.remove(i);
		startAtTops.remove(i);
		wrapArounds.remove(i);
		selectionOnlys.remove(i);
		ignoreCases.remove(i);
		includeReadOnlys.remove(i);
		regExps.remove(i);
		
		// Remove from JList
		((DefaultListModel) Outliner.findReplace.LIST.getModel()).removeElementAt(i);
	}

	
	public String getName(int i) {return (String) this.names.get(i);}
	
	public void setName(int i, String s) {
		this.names.set(i, s);
		((DefaultListModel) Outliner.findReplace.LIST.getModel()).setElementAt(s,i);
	}
	
	public void addName(int i, String s) {
		this.names.add(i, s);
		((DefaultListModel) Outliner.findReplace.LIST.getModel()).insertElementAt(s,i);
	}

	public String getFind(int i) {return (String) this.finds.get(i);}
	public void setFind(int i, String s) {this.finds.set(i, s);}
	public void addFind(int i, String s) {this.finds.add(i, s);}

	public String getReplace(int i) {return (String) this.replaces.get(i);}
	public void setReplace(int i, String s) {this.replaces.set(i, s);}
	public void addReplace(int i, String s) {this.replaces.add(i, s);}

	public boolean getStartAtTop(int i) {return ((Boolean) this.startAtTops.get(i)).booleanValue();}
	public void setStartAtTop(int i, boolean b) {this.startAtTops.set(i, new Boolean(b));}
	public void setStartAtTop(int i, String b) {this.startAtTops.set(i, new Boolean(b));}
	public void addStartAtTop(int i, boolean b) {this.startAtTops.add(i, new Boolean(b));}
	public void addStartAtTop(int i, String b) {this.startAtTops.add(i, new Boolean(b));}

	public boolean getWrapAround(int i) {return ((Boolean) this.wrapArounds.get(i)).booleanValue();}
	public void setWrapAround(int i, boolean b) {this.wrapArounds.set(i, new Boolean(b));}
	public void setWrapAround(int i, String b) {this.wrapArounds.set(i, new Boolean(b));}
	public void addWrapAround(int i, boolean b) {this.wrapArounds.add(i, new Boolean(b));}
	public void addWrapAround(int i, String b) {this.wrapArounds.add(i, new Boolean(b));}

	public boolean getSelectionOnly(int i) {return ((Boolean) this.selectionOnlys.get(i)).booleanValue();}
	public void setSelectionOnly(int i, boolean b) {this.selectionOnlys.set(i, new Boolean(b));}
	public void setSelectionOnly(int i, String b) {this.selectionOnlys.set(i, new Boolean(b));}
	public void addSelectionOnly(int i, boolean b) {this.selectionOnlys.add(i, new Boolean(b));}
	public void addSelectionOnly(int i, String b) {this.selectionOnlys.add(i, new Boolean(b));}

	public boolean getIgnoreCase(int i) {return ((Boolean) this.ignoreCases.get(i)).booleanValue();}
	public void setIgnoreCase(int i, boolean b) {this.ignoreCases.set(i, new Boolean(b));}
	public void setIgnoreCase(int i, String b) {this.ignoreCases.set(i, new Boolean(b));}
	public void addIgnoreCase(int i, boolean b) {this.ignoreCases.add(i, new Boolean(b));}
	public void addIgnoreCase(int i, String b) {this.ignoreCases.add(i, new Boolean(b));}

	public boolean getIncludeReadOnly(int i) {return ((Boolean) this.includeReadOnlys.get(i)).booleanValue();}
	public void setIncludeReadOnly(int i, boolean b) {this.includeReadOnlys.set(i, new Boolean(b));}
	public void setIncludeReadOnly(int i, String b) {this.includeReadOnlys.set(i, new Boolean(b));}
	public void addIncludeReadOnly(int i, boolean b) {this.includeReadOnlys.add(i, new Boolean(b));}
	public void addIncludeReadOnly(int i, String b) {this.includeReadOnlys.add(i, new Boolean(b));}

	public boolean getRegExp(int i) {return ((Boolean) this.regExps.get(i)).booleanValue();}
	public void setRegExp(int i, boolean b) {this.regExps.set(i, new Boolean(b));}
	public void setRegExp(int i, String b) {this.regExps.set(i, new Boolean(b));}
	public void addRegExp(int i, boolean b) {this.regExps.add(i, new Boolean(b));}
	public void addRegExp(int i, String b) {this.regExps.add(i, new Boolean(b));}


	// Saving the Config File	
	public void saveConfigFile() {
		try {
			FileWriter fw = new FileWriter(Outliner.FIND_REPLACE_FILE);
			fw.write(prepareConfigFile());
			fw.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_find_replace_config") + ": " + e);
		}
	}
	
	private String prepareConfigFile() {
		StringBuffer buf = new StringBuffer();

		buf.append(XMLTools.getXmlDeclaration(null) + PlatformCompatibility.LINE_END_DEFAULT);
		buf.append(XMLTools.getElementStart(E_ROOT) + PlatformCompatibility.LINE_END_DEFAULT);

		for (int i = 0; i < getSize(); i++) {
			buf.append("<" + E_ITEM);

			buf.append(" " + A_NAME + "=\"" + XMLTools.escapeXMLAttribute(getName(i)) + "\"");
			buf.append(" " + A_FIND + "=\"" + XMLTools.escapeXMLAttribute(getFind(i)) + "\"");
			buf.append(" " + A_REPLACE + "=\"" + XMLTools.escapeXMLAttribute(getReplace(i)) + "\"");
			buf.append(" " + A_START_AT_TOP + "=\"" + XMLTools.escapeXMLAttribute("" + getStartAtTop(i)) + "\"");
			buf.append(" " + A_WRAP_AROUND + "=\"" + XMLTools.escapeXMLAttribute("" + getWrapAround(i)) + "\"");
			buf.append(" " + A_SELECTION_ONLY + "=\"" + XMLTools.escapeXMLAttribute("" + getSelectionOnly(i)) + "\"");
			buf.append(" " + A_IGNORE_CASE + "=\"" + XMLTools.escapeXMLAttribute("" + getIgnoreCase(i)) + "\"");
			buf.append(" " + A_INCLUDE_READ_ONLY + "=\"" + XMLTools.escapeXMLAttribute("" + getIncludeReadOnly(i)) + "\"");
			buf.append(" " + A_REGEXP + "=\"" + XMLTools.escapeXMLAttribute("" + getRegExp(i)) + "\"");

			buf.append("/>" + PlatformCompatibility.LINE_END_DEFAULT);
		}

		buf.append(XMLTools.getElementEnd(E_ROOT) + PlatformCompatibility.LINE_END_DEFAULT);

		return buf.toString();
	}

	
	// Sax DocumentHandler Implementation
	public void startDocument () {}
	public void endDocument () {}

	public void startElement (String elementName, AttributeList atts) {
		if (elementName.equals(E_ITEM)) {
			int size = getSize();
			
			addName(size, atts.getValue(A_NAME));
			addFind(size, atts.getValue(A_FIND));
			addReplace(size, atts.getValue(A_REPLACE));
			addStartAtTop(size, atts.getValue(A_START_AT_TOP));
			addWrapAround(size, atts.getValue(A_WRAP_AROUND));
			addSelectionOnly(size, atts.getValue(A_SELECTION_ONLY));
			addIgnoreCase(size, atts.getValue(A_IGNORE_CASE));
			addIncludeReadOnly(size, atts.getValue(A_INCLUDE_READ_ONLY));
			addRegExp(size, atts.getValue(A_REGEXP));
		}
	}
	
	public void endElement (String name) throws SAXException {}
	public void characters(char ch[], int start, int length) throws SAXException {}
}
