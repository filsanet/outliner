///////////////////////////////////////////////////////////////////////////////
// JFrameFactory: Mouse wheel support for Java applications on Win32 platforms
// Copyright (C) 2001 Richard Osbaldeston (rosbaldeston@yahoo.com)
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, see http://www.gnu.org
///////////////////////////////////////////////////////////////////////////////

package gui;

import javax.swing.*;
import java.util.Properties;

// RJO 4/6/2001 
//	Added factory methods createFrame() and createFrame(title) which
// are generally preferable to extending JMouseWheelFrame directly 
// as the create methods check the OS and VM versions for compatibility. 
// Otherwise it returns a vanilla JFrame (works with jdk1.4beta as 
// support for MouseWheels is now built-in).

public class JFrameFactory  { 

    public static boolean needsMouseWheelSupport()
    {
		Properties systemProperties = System.getProperties();
		String os_name = systemProperties.getProperty("os.name", "unknown");
		String os_arch = systemProperties.getProperty("os.arch", "unknown");
		String vm_vendor = systemProperties.getProperty("java.vendor", "unknown");

		if (os_name.indexOf("Windows")>=0 && os_arch.indexOf("x86")>=0 && vm_vendor.indexOf("Sun Microsystems")>=0)  {
			String version=systemProperties.getProperty("java.version", "0");
			if (version.startsWith("1.1") || 
				version.startsWith("1.2") || 
				version.startsWith("1.3")) {
				return true;
			}
		} else if (os_name.indexOf("Windows") >= 0 &&
					os_arch.indexOf("x86") >=0 &&
					vm_vendor.indexOf("Microsoft") >= 0) {
		    String version=systemProperties.getProperty("java.version", "0");
            if (version.startsWith("1.1")) {
                return true;
            }
        }
        return false;
    }
    
	public static JFrame createFrame()  {
		if (needsMouseWheelSupport()) {
            return new JMouseWheelFrame();
		}
		return new JFrame();
	}

	public static JFrame createFrame(String title)  {
		JFrame frame = createFrame();
		frame.setTitle(title);
		return frame;
	}
}

/*
 * $Log$
 * Revision 1.4  2001/10/12 21:44:30  mhoesch
 * Added support for MS Java (also for Internet Explorer, if Applets are signed!!!
 * Code for loading DLL to client is missing.., but if copied into WIN System
 * classpath works fine.
 * It also works if DLL is copied at runtime, before the loadLibrary() method is
 * called. Maybe someone else will provide the necessary code.
 *
 * Revision 1.3  2001/06/06 22:22:37  mpowers
 * Fixed logs.
 *
 * Revision 1.2  2001/06/06 22:17:44  mpowers
 * Refactored jvm test into a single method.
 * JMouseWheelDialog and JMouseWheelFrame now call the test method
 * before attempting to load the library: this allows implementations
 * that need to subclass those classes to work under jdk 1.4.
 *
 * Revision 1.1 2001/06/06 21:49:06  mpowers
 * Contributing on behalf of rosbaldeston:
 * Added support for JDialogs.
 * Added factory methods for frames and dialogs.
 * Implemented version checking for 1.4 compatibility.
 *
 */
