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

import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;

import java.lang.reflect.*;

import javax.swing.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class GUITreeLoader extends HandlerBase implements JoeXMLConstants {

	// Constants
	private static final String TYPE_TEXT = "text";
	
	
	// Class Fields
    private static Parser parser = new com.jclark.xml.sax.Driver();
	private static boolean errorOccurred = false;

	public static GUITreeComponentRegistry reg = new GUITreeComponentRegistry();
	public static ArrayList elementStack = new ArrayList(); // Holds the created objects.
	public static ArrayList attributesStack = new ArrayList();
	
	
	// Constructors
	public GUITreeLoader() {
		parser.setDocumentHandler(this);
		parser.setErrorHandler(this);	
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
				Object element = elementStack.get(i);
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
			FileInputStream fileInputStream = new FileInputStream(file);
			parser.parse(new InputSource(fileInputStream));
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
	public void startDocument () {}
    
	public void endDocument () {}

	public void startElement (String name, AttributeList atts) {
		
		// Special Handling for elements that are not GUITreeComponents
		try {
			if (name.equals(E_SEPARATOR)) {
				JMenu menu = (JMenu) elementStack.get(elementStack.size() - 1);
				menu.insertSeparator(menu.getItemCount());
				return;
			} else if (name.equals(E_VERTICAL_STRUT)) {
				Box box = ((AbstractPreferencesPanel) elementStack.get(elementStack.size() - 1)).box;
				box.add(Box.createVerticalStrut(Integer.parseInt(atts.getValue(A_SIZE))));
				return;		
			} else if (name.equals(E_ASSET)) {
				reg.addText(atts.getValue(A_KEY), atts.getValue(A_VALUE));
				return;		
			}
		} catch (Exception e) {
			System.out.println("Exception: Something went wrong during special handling in the GUITreeLoader.java.");
			e.printStackTrace();
			return;
		}
				
		// Get Standard Attributes
		String componentName = atts.getValue(A_ID);
		String className = atts.getValue(A_CLASS);

		// Process Elements
		try {
			//System.out.println("Setting component: " + className);
			GUITreeComponent obj = (GUITreeComponent) Class.forName(className).newInstance();
			
			// Set the GUITreeComponent's name
			obj.setGUITreeComponentID(componentName);

			// Update Stack
			elementStack.add(obj);
			attributesStack.add(new AttributeListImpl(atts));
			
			// Add it to the registry
			reg.add(obj);
			
			// Call startSetup
			obj.startSetup(atts);
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println("Exception: Something went wrong during processing in the GUITreeLoader.java.");
			System.out.println(e);
		}
		

		// Special Handling for elements with custom attributes

	}
	
	public void endElement (String name) throws SAXException {
		// Special Handling for elements that are not GUITreeComponents
		if (name.equals(E_SEPARATOR) || name.equals(E_VERTICAL_STRUT) || name.equals(E_ASSET)) {
			return;
		}
		
		// Get GUITreeComponent from stack
		GUITreeComponent obj = (GUITreeComponent) elementStack.get(elementStack.size() - 1);
		
		// Call endSetup
		AttributeList atts = (AttributeList) attributesStack.get(attributesStack.size() - 1);
		obj.endSetup(atts);
		
		// Update Stack
		elementStack.remove(elementStack.size() - 1);
		attributesStack.remove(attributesStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		
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
