/*
 * joe.cpp
 * 
 * win32 exe that runs the java 2 app joe
 * 
 * Copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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

/**
 * @author  $Author$
 * @version $Revision$, $Date$
 */

// include files
#include <stdio.h>
#include <windows.h>
#include <tlhelp32.h>
#include "joe.h"


// invoke the application
// returns 1 if all goes well, 0 if it doesn't
int WINAPI WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance,
PSTR szCmdLine, int iCmdShow) {
	
	// local vars
	STARTUPINFO si;
	PROCESS_INFORMATION pi;
	char classPathBuffer[MAX_LINE] ;
	char commandLine[MAX_LINE] ;
	char appHomeBuffer[MAX_LINE];
	char docToOpen [MAX_PATH] ;
	int gotDoc = 0 ;
	char appMailslot [MAX_PATH] ;
	
	// determine which Windows world we're in
	g_Windows_Version = determineWindowsVersion();
	
	// does the command line contain a valid document path ?
	gotDoc = extractDocFromCmdLine(szCmdLine, docToOpen) ;
	
	// if we've got a document to open
	// and there's an instance of the app already running
	if (gotDoc && otherAppInstanceRunning(appMailslot)) 
			
			// if we can send the doc to that instance ...
			if (sendDocToAppInstance(docToOpen, appMailslot)) 
				
				// we're done
				return 1 ;
	
	// if we're starting up after an installation ...
			// reset startupAfterInstall flag
			// any file to open on this startup ??
			// if so, grab it

	// prep the CreateProcess data structures
	ZeroMemory( &si, sizeof(si) );
	si.cb = sizeof(si);
	ZeroMemory( &pi, sizeof(pi) );
	
	// if we can build up a class path string
	if (buildClassPath(classPathBuffer) 
	
	// and we can build up a java-invoking command line
	&& buildCommandLine(commandLine, classPathBuffer, szCmdLine) 
	
	// and we can find out where the app's living 
	&& getAppHome(appHomeBuffer) 
	
	// and we can spawn the javatic process 
	&& (CreateProcess(NULL, commandLine, NULL, NULL, FALSE, 0, NULL, appHomeBuffer, &si, &pi)!= 0 )) 
		
		// we cool
		return 1 ;
	else
		// somethin's fishy
		return 0 ;
	
} // end function WinMain


// obtain the value of the APP_HOME environment variable
// returns 1 if it succeeds, 0 if it fails
int getAppHome (char * appHomeBuffer) {
	// local vars
	
	return getEnvVar(APP_HOME, appHomeBuffer);
	
} // end getAppHome


int buildClassPath (char * classPathBuffer) {
	
	// TBD -- store these jar paths in an array in joe.h
	//	then build path from that array
	strcpy(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "com.organic.maynard.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "outliner.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "com.yearahead.io.webfile.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "com.psm.wiki.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "sax.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "xp.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "xmlrpc.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "bsh.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "jakarta-oro.jar;") ;
	strcat(classPathBuffer, ROOT_PATH) ;
	strcat(classPathBuffer, "jmousewheel.jar") ;
	
	return 1 ;
}  // end function buildClassPath


int buildCommandLine (char * commandLine, char * classPathBuffer, char * passedParams) {

	// we're supplying a class path
	strcpy(commandLine, "java -cp ") ;
	// strcpy(commandLine, "javaw -cp ") ;
	strcat(commandLine, classPathBuffer) ;
	// use incremental garbage collection, English
	strcat(commandLine, " -Xincgc com.organic.maynard.outliner.Outliner en \"") ;
	strcat(commandLine, passedParams) ;
	strcat(commandLine, "\"") ;
	
	return 1 ;
} // end function buildCommandLine


// get an environment variable's value
int getEnvVar (char * varName, char * varValueBuffer) {
	// local vars
	int result ;
	
	// switch out on windows version
	switch (g_Windows_Version) {
		case WIN_95:
		case WIN_95_OSR2:
		case WIN_98:
		case WIN_98_SE:
			// win 9x OSes use autoexec.bat
			result = getAutoExecEnvVar (varName, varValueBuffer) ;
			
			break ;
			
		case WIN_ME:
		case WIN_XP:
		case WIN_NT_4:
		case WIN_2K:
		case WIN_DOT_NET_SERVER:
		case WIN_UNKNOWN_V4:
		case WIN_UNKNOWN_V5:
		case WIN_UNKNOWN_V6:
		case WIN_UNKNOWN_V7:
			// win me/xp/nt4/2k/.netServer/unknownsV4andUp OSes use the registry
			
			// if we can't get it for current user
			if (! (result = getRegistryEnvVar (varName, varValueBuffer, USER)))
				
				// try to get it for all users
				result = getRegistryEnvVar (varName, varValueBuffer, SYSTEM) ;
			
			break ;
			
		case WIN_NT_351:
		case WIN_UNKNOWN_V3:
		case WIN_VERY_UNKNOWN:
		default:
			// we don't work on any other windows systems (NT 3.5 and less)
			result = 0 ;
			break ;
	} // end switch
		
	// done
	return result ;
	
} // end setEnvVar


// figure out what version of Windows we're running
// returns a windows_version value
// returns 0 aka CANNOT_DETERMINE if it cannot determine the Windows version
windows_version determineWindowsVersion () {
	// local vars
	OSVERSIONINFOEX osvi;
	BOOL bOsVersionInfoEx;
	windows_version result ;

	// clear the data structure
	ZeroMemory(&osvi, sizeof(OSVERSIONINFOEX));
	// set it up as the EX versionInfo
	osvi.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);

	// if calling GetVersionEx using OSVERSIONINFOEX fails ...
	if( !(bOsVersionInfoEx = GetVersionEx ((OSVERSIONINFO *) &osvi)) ){
		
		// set up as the nonEX versionInfo
		osvi.dwOSVersionInfoSize = sizeof (OSVERSIONINFO);
	
		// if calling GetVersionEx using OSVERSIONINFO fails ...
		if (! GetVersionEx ( (OSVERSIONINFO *) &osvi) ) 
			// leave failing
			// this means we're on a pre-NT_351/95 system
			return CANNOT_DETERMINE;
	} // end if

	// okay, we have version info in regular or EX form
	
	// switch out on major version #
	switch (osvi.dwMajorVersion) {
		
		// NT 351
		case 3:
			// switch out on minor version #
			switch (osvi.dwMinorVersion) {
				case 51:
					result = WIN_NT_351 ;
					break ;
				default:
					result = WIN_UNKNOWN_V3 ;
					break ;
			} // end switch
			break ;
		
		// 95, 95 OSR2, NT 4, 98, 98 SE, or ME	
		case 4:
			// switch out on minor version #
			switch (osvi.dwMinorVersion) {
				
				// 95, 95 OSR2, or NT 4
				case 0:
					// if it's not NT...
					if (osvi.dwPlatformId != VER_PLATFORM_WIN32_NT) {
						// it's 95
						// regular or OSR2 ?
						// if it's a B or C
						if ((osvi.szCSDVersion[1] == 'C')
							|| (osvi.szCSDVersion[1] == 'B'))
							result = WIN_95_OSR2 ;
						else
							result = WIN_95 ;
					} // end if it's not NT
					else {
						// it's NT 4
						result = WIN_NT_4 ;
						
						// let's get any service pack
						if (osvi.szCSDVersion[0] != 0) 
							g_NT_4_SP_Num = osvi.szCSDVersion[strlen(osvi.szCSDVersion) - 1]- '0' ;
					} // end else it's NT 4
					break ;
				
				// 98 or 98 SE
				case 10:
					// if it's SE
					if (osvi.szCSDVersion[1] == 'A' )
						result = WIN_98_SE ;
					else
						result = WIN_98 ;
					break ;
					
				// ME
				case 90:
					result = WIN_ME ;
					break ;
					
				// unknown
				default:
					result = WIN_UNKNOWN_V4 ;
					break ;
					
			} // end switch on minor version #
			break ;
		
		// 2000, XP, or .Net Server	
		case 5:					
			// switch out on minor version #
			switch (osvi.dwMinorVersion) {
				
				// 2000
				case 0:
					result = WIN_2K ;
					break ;
					
				// XP or .Net Server
				case 1:
					// if we have EX data (we should)
					if (bOsVersionInfoEx) { 
						// if we're nt workstation
						if(osvi.wProductType == VER_NT_WORKSTATION)
							result = WIN_XP ;
						else
							result = WIN_DOT_NET_SERVER ;
					} else 
						result = WIN_UNKNOWN_V5 ;
					break ;
					
				default:
					result = WIN_UNKNOWN_V5;
					break ;
			} // end switch on minor version #
			break ;
			
		// future stuff
		case 6:
			result = WIN_UNKNOWN_V6 ;
			break ;
			
		case 7:
			result = WIN_UNKNOWN_V7 ;
			break ;
		
		case 8:
			result = WIN_UNKNOWN_V8 ;
			break ;
		
		case 9:
			result = WIN_UNKNOWN_V9 ;
			break ;
		
		case 10:
			result = WIN_UNKNOWN_V10 ;
			break ;
		
		// very unknown
		// majorVersion < 3 or > 10
		default:
			result = WIN_VERY_UNKNOWN ;
			break ;
			
	} // end switch on major version #

	// done with determination
	
	// done
	return result ;
	
} // end function determineWindowsVersion


int getAutoExecEnvVar (char * varName, char * varValueBuffer) {
	
	strcpy (varValueBuffer, "e:\\JOE188\\") ;
	return 1 ;
	
}  // end function getAutoExecEnvVar


int getRegistryEnvVar (char * varName, char * varValue, environment_target) {
	
	
	return 1 ;
	
} //  end function getRegistryEnvVar


// is there another instance of the app running ??
// returns 1 if yes, 0 if no
// if yes, mailslotAddress is set to app instance's mailslot
int otherAppInstanceRunning (char * mailslotAddress) {
	// local vars
	int result = 0;
	
	// if there are entries in the Mailslots subkey
	
		// if we can grab the newest one
		
			// copy its mailslot address
			
				// cool
	
	// if we get here, either nobody else is running,
	// or there were problems getting its mailslot address
	return 0 ;
	
} // end function otherAppInstanceRunning


int sendDocToAppInstance(char * docPath, char * mailslotAddress) {
	// local vars
	int result = 0 ;
	
	// build up the message string
	// File:Open
	// docPath
	
	// send it
	
	return result ;
} // end function sendDocToApp


int extractDocFromCmdLine(char * cmdLine, char * docPath) {
	// deal with a null cmdLine
	if (*cmdLine == 0) {
		*docPath = 0 ;
		return 1 ;
	} // end if
	
	// copy command line to docPath
	strcpy(docPath, cmdLine) ;
		
	// strip any enclosing quotes and double-backslashes and white space
	stripPathCrud(docPath) ;
	
	// if the command line contains a file that exists ...
	if (fileExists(docPath)) {
		
		// done well
		return 1 ;
	} else
		// done not so well
		return 0 ;
		
} // end function extractDocFromCmdLine


// does a file exist ??
int fileExists (char * fullpath) {
	// try to open the file for reading 
	FILE * fp = fopen(fullpath, "r") ;
	
	// if we get a handle
	if (fp != NULL) {
		// it exists
		// close it
		fclose(fp) ;
		// done
		return 1 ;
	} else
		// doesn't exist
		return 0 ;
} // end function fileExists


// command line file paths come in here with 
// extra quotes and backslashes
int stripPathCrud (char * someString) {
	// local vars
	int placeIndex = 0 ;
	int testIndex = 0 ;
	char someChar ;
	
	// move through any white space
	// TBD
	
	// if first non-white space char is a quote
	if (someString[placeIndex] == QUOTE_CHAR)
		// we'll skip it
		testIndex ++ ;
	
	// char by char, til the end of the string
	while ((someChar = someString[testIndex]) != 0) {
		
		// copy a char
		someString[placeIndex] = someChar ;
		
		// up the indices
		placeIndex ++ ;
		testIndex ++ ;
			
		// if we're a double backslash, cut to 1
		if ((someChar == BACKSLASH_CHAR) &&
			(someString[testIndex] == BACKSLASH_CHAR))
			testIndex ++ ;
			
	} // end while body
		
	// if last char is a quote ...
	if (someString[placeIndex - 1] == QUOTE_CHAR)
		someString[placeIndex - 1] = 0 ;
	else
		someString[placeIndex] = 0 ;
		
	// done
	return 1 ;
	
} // end function stripEnclosingQuotes
