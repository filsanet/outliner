///////////////////////////////////////////////////////////////////////////////
// JMouseWheel: Mouse wheel support for Java applications on Win32 platforms
// Copyright (C) 2001 Davanum Srinivas (dims@geocities.com)
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

package gui;	// RJO - repackaged because we can't overide unamed packages from named ones

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// This is an undocumented package.
import sun.awt.*;

public class JMouseWheelDialog extends JDialog {
	static boolean libraryLoaded = false;
	private int scrollSpeed = 3;

    private int hwnd = 0; // init to null

	static  {
		//Load the library that contains the subclassing code.
        if ( JDialogFactory.needsMouseWheelSupport() ) {
            try  {
				// If running in MS Java (Applet!!) we need to set the
				// SecurityPermission (which might fail due to missing rights)
				// Marc Hoeschele 17.08.01
				String vm_vendor = System.getProperty("java.vendor", "");
				Class cl = null;
				if (vm_vendor.indexOf("Microsoft") >= 0 && ((cl = Class.forName("com.ms.security.PolicyEngine")) != null)) {
				// Use Reflection, as we don't have the MS classes in our usual
				// compiler classpath...
					System.out.println("Trying to get MS Security permission");
					Class cl1 = Class.forName("com.ms.security.PermissionID");
					Class cl2 = Class.forName("com.ms.security.PolicyEngine");

					java.lang.reflect.Field field = cl1.getField("SYSTEM");

					Class[] classes = { cl1 };
					java.lang.reflect.Method method = cl2.getMethod("assertPermission", classes);

					Object[] obj = { field.get(null) };
					Object o = method.invoke(null, obj);

					// This invokes: com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.SYSTEM);
				}
                System.loadLibrary("lib\\MouseWheel");
                libraryLoaded = true;
            } catch (Throwable t) {
                System.err.println("Failed to load MouseWheel Library - Wheel Disabled.");
			}
        }
	}

	// constructors
	public JMouseWheelDialog()  {
		super();
	}

	public JMouseWheelDialog(Dialog owner)  {
		super(owner);
	}

	public JMouseWheelDialog(Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public JMouseWheelDialog(Dialog owner, String title) {
		super(owner, title);
	}

	public JMouseWheelDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public JMouseWheelDialog(Frame owner) {
		super(owner);
	}

	public JMouseWheelDialog(Frame owner, boolean modal) {
		super(owner, modal);
	}

	public JMouseWheelDialog(Frame owner, String title) {
		super(owner, title);
	}

	public JMouseWheelDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	// native entry point for subclassing the JDialog window
	private native void setHook(int hwnd);

	// native entry point for removing the hook.
	private native void resetHook(int hwnd);

	// this is the function which serves as a call back when
	// a mouse wheel movement is detected.
	public void notifyMouseWheel(short fwKeys,short zDelta,long xPos, long yPos) {

		JMouseWheelSupport.notifyMouseWheel(this, scrollSpeed, fwKeys, zDelta, xPos, yPos);

	}

	// Returns the HWND for canvas.
	// This is undocumented, but works on JDK1.1.8, JDK1.2.2 and JDK1.3
	private int getHWND() {
		String vm_vendor = System.getProperty("java.vendor", "");
		if ( hwnd != 0 ) return hwnd;

		// If Java VM is from MS, then use this code to get the hWND
		// (Hope this also works for Dialogs... Marc Hoeschele 17.08.01
		else if (vm_vendor.indexOf("Microsoft") >= 0) {
			try {
				Object o = getPeer();

				// Use Reflection, as we don't have the MS classes here...
				Class[] classes = null;
				java.lang.reflect.Method method = o.getClass().getMethod("gethwnd", classes);
				Object[] obj = null;

				// Reflection call to MS Window Peer
				Object resp = method.invoke(o, null);
				hwnd = ((Integer)resp).intValue();
			} catch(Exception e) { }

		// If not MS Java then its probably standard SUN Java (the better one)
		} else {

			DrawingSurfaceInfo drawingSurfaceInfo;
			Win32DrawingSurface win32DrawingSurface;

			// Get the drawing surface
			drawingSurfaceInfo =
				((DrawingSurface)(getPeer())).getDrawingSurfaceInfo();

			if (null != drawingSurfaceInfo) {
				drawingSurfaceInfo.lock();
				// Get the Win32 specific information
				win32DrawingSurface =
					(Win32DrawingSurface)drawingSurfaceInfo.getSurface();
				hwnd = win32DrawingSurface.getHWnd();
				drawingSurfaceInfo.unlock();
			}
		}
		return hwnd;
	}

	// subclass the window once it has been created.
	public void addNotify() {
		super.addNotify();
		if (libraryLoaded) setHook(getHWND());
	}

	// remove the subclass-ing when the window is about to be destroyed.
	public void removeNotify() {
		if (libraryLoaded) resetHook(getHWND());
		super.removeNotify();
	}


	/**

	 * Returns the MouseWheel scroll speed for components contained within this frame

	 */

	public int getScrollSpeed() {

		return this.scrollSpeed;

	}



	/**

	 * Sets the MouseWheel scroll speed for components contained within this frame

	 */

	public void setScrollSpeed(int speed) {

		this.scrollSpeed = speed;

	}

}

/*
 * $Log$
 * Revision 1.1  2001/11/12 09:05:38  maynardd
 * Added code from outliner.jar to make this library self contained.
 *
 * Revision 1.6  2001/10/12 21:44:30  mhoesch
 * Added support for MS Java (also for Internet Explorer, if Applets are signed!!!
 * Code for loading DLL to client is missing.., but if copied into WIN System
 * classpath works fine.
 * It also works if DLL is copied at runtime, before the loadLibrary() method is
 * called. Maybe someone else will provide the necessary code.
 *
 * Revision 1.5  2001/08/13 02:15:51  davidconnard
 * Added support for JComboBoxes.  Moved shared scrolling code into
 * a helper class - JMouseWheelSupport so that it does not have
 * to be duplicated between the Frame and Dialog flavours.
 *
 * Revision 1.4  2001/06/11 13:36:25  mpowers
 * Now only looking up hwnd the first time.  This avoids a NPE when
 * removeNotify is called on a window that is already disposed.
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
 */
