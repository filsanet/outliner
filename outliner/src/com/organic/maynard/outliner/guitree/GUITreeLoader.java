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
 
package com.organic.maynard.outliner.guitree;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.util.string.StringTools;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import javax.swing.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class GUITreeLoader extends HandlerBase implements JoeXMLConstants {

	// Constants
	private static final String TYPE_TEXT = "text";
	
	
	// Class Fields
    private static boolean errorOccurred = false;

	public static GUITreeComponentRegistry reg = new GUITreeComponentRegistry();
	
	public static GUITreeComponentList elementStack = new GUITreeComponentList();
	public static AttributeListList attributesStack = new AttributeListList();
	
	
	// Constructors
	public GUITreeLoader() {
		Outliner.XML_PARSER.setDocumentHandler(this);
		Outliner.XML_PARSER.setErrorHandler(this);	
	}

	/**
	 * A convience method wrapper for <code>getAncestorElementOfClass(String className, int startDepth)</code>
	 * where startDepth is automatically set to 0.
	 */				
	public static Object getAncestorElementOfClass(String className) {
		return getAncestorElementOfClass(className, 0);
	}

	/**
	 * Returns the deepest object from the element stack that is of the
	 * same class, subclass, or implementor of, the class name provided.
	 * This method makes it easy to find the objects instantiated by the ancestor
	 * elements in the gui_tree.xml file.
	 *
	 * @param className  a fully qualified class or interface name. 
	 * @param startDepth How far up to start inspecting. 0 indicates to start
	 *                   looking from the deepest node, 1 from the next deepest,
	 *                   2 from the next, etc.
	 * @return        An <code>Object</code> if a matching element is found, or
	 *                <code>null</code> if no match is found.
	 */			
	public static Object getAncestorElementOfClass(String className, int startDepth) {
		if (startDepth < 0 || startDepth >= elementStack.size()) {
			throw new IllegalArgumentException("Illegal Depth: " + startDepth);
		}
		
		try {
			Class cAncestor = Class.forName(className);
			
			for (int i = elementStack.size() - 1 - startDepth; i >= 0; i--) {
				GUITreeComponent element = elementStack.get(i);
				Class cElement = element.getClass();
				if (cAncestor.isAssignableFrom(cElement) ) {
					return element;
				}
			}
		
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		}
	
		return null;
	}

	
	// OpenFileFormat Interface
	public boolean load(String file) {
		try {
			Outliner.XML_PARSER.parse(new InputSource(new BufferedInputStream(new FileInputStream(file))));
			if (errorOccurred) {
				return false;
			}
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	// Sax DocumentHandler Implementation
	public void startDocument() {}
    
	public void endDocument() {}

	public void startElement (String name, AttributeList atts) {
		
		// Special Handling for elements that are not GUITreeComponents
		try {
			if (E_SEPARATOR.equals(name)) {
				JMenu menu = (JMenu) elementStack.get(elementStack.size() - 1);
				menu.insertSeparator(menu.getItemCount());
				return;
			} else if (E_VERTICAL_STRUT.equals(name)) {
				Box box = ((AbstractPreferencesPanel) elementStack.get(elementStack.size() - 1)).box;
				box.add(Box.createVerticalStrut(Integer.parseInt(atts.getValue(A_SIZE))));
				return;		
			} else if (E_ASSET.equals(name)) {
				reg.addText(atts.getValue(A_KEY), atts.getValue(A_VALUE));
				return;		
			}
		} catch (Exception e) {
			System.out.println("Exception: Something went wrong during special handling in the GUITreeLoader.java.");
			e.printStackTrace();
			return;
		}

		// Process Elements
		try {
			//System.out.println("Setting component: " + className);
			GUITreeComponent obj = (GUITreeComponent) Class.forName(atts.getValue(A_CLASS)).newInstance();
			
			// Set the GUITreeComponent's name
			obj.setGUITreeComponentID(atts.getValue(A_ID));

			// Update Stack
			elementStack.add(obj);
			attributesStack.add(new AttributeListImpl(atts));
			
			// Add it to the registry
			reg.add(obj);
			
			// Call startSetup
			obj.startSetup(atts);
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + atts.getValue(A_CLASS) + " " + cnfe);
		} catch (Exception e) {
			System.out.println("Exception: Something went wrong during processing in the GUITreeLoader.java.");
			e.printStackTrace();
		}
		

		// Special Handling for elements with custom attributes

	}
	
	public void endElement(String name) throws SAXException {
		// Special Handling for elements that are not GUITreeComponents
		if (E_SEPARATOR.equals(name) || E_VERTICAL_STRUT.equals(name) || E_ASSET.equals(name)) {
			return;
		}
		
		int lastElementIndex = elementStack.size() - 1;
		
		// Call endSetup
		elementStack.get(lastElementIndex).endSetup(attributesStack.get(lastElementIndex));
		
		// Update Stack
		elementStack.remove(lastElementIndex);
		attributesStack.remove(lastElementIndex);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		//String text = new String(ch, start, length);
		// Does nothing right now.
	}
	
	
	// ErrorHandler Interface
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e.getMessage());
		this.errorOccurred = true;
	}

	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e.getMessage());
		this.errorOccurred = true;
	}

	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e.getMessage());
		this.errorOccurred = true;
	}
}