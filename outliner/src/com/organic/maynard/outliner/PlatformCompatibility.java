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

import java.util.*;
import javax.swing.*;

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

public class PlatformCompatibility {

	// Constants
	
	
	// Constructors
	public PlatformCompatibility() {}


	// Static Methods
	public static String getScrollBarUIClassName() {
		// Code switches on the platform and returns the appropriate
		// platform specific classname or else the default.
		if (isWindows()) {
			return "com.organic.maynard.outliner.MetalScrollBarUI";
		} else {
			return "com.organic.maynard.outliner.BasicScrollBarUI";
		}
	}
	
	public static boolean areFilenamesEquivalent(String filename1, String filename2) {
		if (isWindows()) {
			// Windows is not case sensitive so we need to ignore case.
			return filename1.equalsIgnoreCase(filename2); // Should be expanded to handle the ~ thing.
		} else {
			return filename1.equals(filename2);
		}
	}
	
	
	// Utility Methods
	public static boolean isWindows() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("win")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isJava1_3_1() {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.startsWith("1.3.1")) {
			return true;
		} else {
			return false;
		}	
	}
}
