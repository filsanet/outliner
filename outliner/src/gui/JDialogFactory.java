///////////////////////////////////////////////////////////////////////////////
// JDialogFactory: Mouse wheel support for Java applications on Win32 platforms
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
import java.awt.*;
import java.util.Properties;

public class JDialogFactory  {

	public static JDialog createDialog(Dialog owner)  {
		return createDialog(owner, "", false);
	}

	public static JDialog createDialog(Dialog owner, boolean modal) {
		return createDialog(owner, "", modal);
	}

	public static JDialog createDialog(Dialog owner, String title) {
		return createDialog(owner, title, false);
	}

	public static JDialog createDialog(Dialog owner, String title, boolean modal) {
		return createDialog(owner, title, modal);
	}

	public static JDialog createDialog(Frame owner) {
		return createDialog(owner, "", false);
	}

	public static JDialog createDialog(Frame owner, boolean modal) {
		return createDialog(owner, "", modal);
	}
	
	public static JDialog createDialog(Frame owner, String title) {
		return createDialog(owner, title, false);
	}

	public static JDialog createDialog(Frame owner, String title, boolean modal) {
		JDialog dialog = createDialogImpl(owner);
		dialog.setModal(modal);
		dialog.setTitle(title);
		return dialog;
	}

	public static JDialog createDialog()  {
		return createDialogImpl(null);
	}
	
    public static boolean needsMouseWheelSupport() {
        return JFrameFactory.needsMouseWheelSupport();   
    }
    
	private static JDialog createDialogImpl(Window owner)  {
		if (needsMouseWheelSupport()) {
            if (owner instanceof Frame)  {
                return new JMouseWheelDialog((Frame)owner);
            } else if (owner instanceof Dialog)  {
                return new JMouseWheelDialog((Dialog)owner);				
            } else  {
                return new JMouseWheelDialog();				
            }
		}
		
		if (owner instanceof Frame)  {
			return new JDialog((Frame)owner);
		} else if (owner instanceof Dialog)  {
			return new JDialog((Dialog)owner);				
		} else  {
			return new JDialog();				
		}
	}	
}

/*
 * $Log$
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
