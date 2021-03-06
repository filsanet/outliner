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
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import org.xml.sax.*;

public class PreferenceLineEnding extends AbstractPreference implements GUITreeComponent {
	
	// Instance Fields
	public String def = "";
	public String cur = "";
	public String tmp = "";


	// Constructors
	public PreferenceLineEnding() {
	
	}

	public PreferenceLineEnding(String def, String command) {
		this(def,"",command);
	}

	public PreferenceLineEnding(String def, String cur, String command) {
		this.def = def;
		this.cur = cur;
		this.tmp = cur;
		setCommand(command);
	}


	// GUITreeComponent Interface
	public void endSetup(Attributes atts) {
		super.endSetup(atts);
	}	


	// Setters	
	public void setDef(String value) {this.def = value;}
	public void setCur(String value) {this.cur = value;}
	public void setTmp(String value) {this.tmp = value;}

	public String getCur() {return cur;}
	public String getDef() {return def;}
	public String getTmp() {return tmp;}


	// Misc Methods
	public String toString() {return String.valueOf(cur);}


	// Preference Interface
	public void restoreCurrentToDefault() {cur = def;}
	public void restoreTemporaryToDefault() {tmp = def;}
	public void restoreTemporaryToCurrent() {tmp = cur;}
	public void applyTemporaryToCurrent() {cur = tmp;}
}