/**
 * Copyright (C) 2001 Steven Spencer, steve@yearahead.com
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

package com.yearahead.io;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.Authenticator;
import java.util.HashMap;
import com.organic.maynard.outliner.guitree.GUITreeLoader;

/**
 * A plugin to a JFileChooser so that a directory on a web server will
 * appear as a "local" file system.  Requires the outliner.php script
 * on the web server.
 */
public class WebFileSystemView extends FileSystemView
{
	/** Set to true to turn on debugging. */
	public static final boolean DEBUG = false;

	/** Default name of a new folder. */
	private static String NEW_FOLDER = null;

	private HashMap fileMap = new HashMap();
	private WebFile rootDir;


	/**
	 * Create a new web file view.  Base url defaults to
	 * http://localhost/outliner.php.
	 */
	public WebFileSystemView()
	{
		this("http://localhost/outliner.php", null, null);
	}

	/**
	 * Create a new web file view with the specified url.  Should be 
	 * something like http://localhost/outliner.php.
	 */
	public WebFileSystemView(String url)
	{
		this(url, null, null);
	}

	/**
	 * Create a new web file view with the specified url.  Should be
	 * something like http://localhost/outliner.php.  You can protect
	 * the directory with basic apache authorization.  For more
	 * information, see the extras/readme.txt file.
	 */
	public WebFileSystemView(String url, String user, String pw)
	{
		NEW_FOLDER = GUITreeLoader.reg.getText("untitled_folder");
		//int len = "http://".length();
		int len = url.indexOf("//") + 2; // [md] 11-8-01 This should work better in case it's https://
		String host = url.substring(len, url.indexOf("/", len));

		// set the username/pw for basic apache authorization.
		if (user != null && pw != null) {
			//Authenticator.setDefault(new WebAuthenticator("steve", "ally"));
			Authenticator.setDefault(new WebAuthenticator(user, pw)); // [md] 11-8-01 Looks like this was hard coded. Let's see if this works.
		}

		rootDir = new WebFile(host, url);
	}

	
	// Returns a File object constructed in dir from the given filename. 
	public File createFileObject(File dir, String filename)
	{
		dbg("createFileObject: " + dir + ", " + filename);

		return new WebFile((WebFile)dir, filename);
	}

	// Returns a File object constructed from the given path string. 
	public File createFileObject(String path)
	{
		dbg("createFileObject: " +path);

		path = path.replace('\\', '/');
		File f = (File)fileMap.get(path);

		if (f == null) {
			return new WebFile(path);
		}

		return f;
	}

	// gets the list of shown (not hidden) files
	public File[] getFiles(File dir, boolean useFileHiding)
	{
		dbg("getFiles: " + dir);

		File[] f = dir.listFiles();
		for (int i = 0; i < f.length; i++) {
			fileMap.put(f[i].getPath(), f[i]);
		}
		return f;
	}

	// return home dir
	public File getHomeDirectory()
	{
		return rootDir;
	}

	//
	// abstract
	//

	// creates a new folder with a default folder name. 
	public  File createNewFolder(File containingDir)
	{
		File[] f = containingDir.listFiles();

		HashMap map = new HashMap();
		for (int i = 0; i < f.length; i++)
		{
			map.put(f[i].getName(), f[i].getName());
		}

		String newFolderName;

		for (int i = 0; i < 10000; i++)
		{
			newFolderName = NEW_FOLDER + i;
			if (map.get(newFolderName) == null)
			{
				WebFile file = new WebFile((WebFile)containingDir,
										   newFolderName);

				if (file.mkdir())
					return file;

				break;
			}
		}

		return null;
	}

	// Returns the parent directory of dir. 
	public File getParentDirectory(File dir)
	{
		return null;
	}

	public boolean isHiddenFile(File f)
	{
		return false;
	}
	
	// Returns all root partitions on this system. 
	public File[] getRoots()
	{
		return new File[] {rootDir};
	}

	// Determines if the given file is a root partition or drive. 
	public boolean isRoot(File f)
	{
		return (rootDir == f);
	}

	private static void dbg(String s)
	{
		if (DEBUG)
			System.out.println("[WebFileSystemView] " + s);
	}
}
