/*
 * windows_setup.cpp
 * 
 * installs a Java 2 application on a Win32 system
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
#include <shlwapi.h>
#include "windows_setup.h"
#include "resource.h"

// installs a Java 2 application on a Windows system
// returns 1 if all goes well, 0 if it doesn't

// if all goes well, the user may choose to reboot
// the system if that's necessary, or the user may
// choose to start the app

int WINAPI WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance,
PSTR szCmdLine, int iCmdShow) {
	// local vars
	int reboot = 0;
	int startApp = 0;
	
	// set up a log file
	// TBD
	
	// if the user doesn't choose to Cancel at the welcome screen ...
	if (welcome() 
	
	// and this system is suitable for running the app ...
	&& weCanRunOnThisSystem() 

	// and we've got a proper Java 2 Runtime Environment
	&& weHaveJ2RE()
	
	// and we're able to plug ourselves into this system
	&& wePlugIntoSystem () ){
		
		// all went well, and we're installed
		// give the user a chance to examine the results
		
		// if the system needs a reboot to run the app
		if (rebootRequired(g_Windows_Version))
			// mention that in success message
			reboot = successFeedbackReboot();
			
		// else the system does not need a reboot to run the app	
		else
			// mention option of starting the app in success message
			startApp = successFeedbackNoReboot() ;
	} else {
		// we failed
		failureFeedback () ;
		return 0 ;
		
	} // end if-else

	// if we're rebooting
	if (reboot) {
		
		ExitWindowsEx(EWX_REBOOT, 0 ) ; 
			// TBD -- get reason.h file,
			// then use these instead of 0
			// SHTDN_REASON_MAJOR_APPLICATION | SHTDN_REASON_MINOR_INSTALLATION) ;

	// else if we're starting up the app
	} else if (startApp) {
		

		
	} // end else-if
	
	// close the log file
	// TBD
	
	// if we get here, we're cool
	return 1 ;
	

} // end function main


int welcome () {
	char welcomeString [MAX_LINE] ;
	
	// build up the string
	strcpy(welcomeString, WELCOME_STRING_0) ;
	strcat(welcomeString, WELCOME_STRING_1) ;
	
	// run the dialog
	return DisplayInfoContCancel (welcomeString) ;
	
} // end function welcome

// see if this system meets the requirements for running the app
// returns 1 if it does, 0 if it doesn't
int weCanRunOnThisSystem () {
	
	// if we can determine the version of Windows that's running ...
	if ( determineWindowsVersion(& g_Windows_Version)
	
	// and that there's a Java 2 Runtime available for that version ...
	&& java2available(g_Windows_Version) 
	
	// and the machine has the power needed ...
	// (memory, processor speed)
	&& machineHasNuffOomph() ) 
	
		// all is cool
		return 1 ;
		
	else
		// all is not cool
		return 0 ;
		
} // end function weCanRunOnThisSystem
	

// Make sure that a Java 2 Runtime Environment is properly installed on this system
// returns 1 if it is, 0 if it isn't
int weHaveJ2RE () {
	// local vars
	int allIsCool = 1 ;
	
	// app launchers in place ??
	allIsCool &= javaAppLaunchersCool() ;
	
	// are the required reg entries in place and correct ??
	allIsCool &= javaRegEntriesCool () ;

	// gut check
	if (allIsCool) return 1 ;

	// all is not cool

	// let's try to install JRE 

	
	
	
	// TBD
	// fake for now
	// check registry for J2RE vars
	// check for std location of j2re
	// check for other env vars
	// if not here
		// if there's a JRE in Outliner dir
			// suggest install from there
		
		// else suggest a download off of net
			// suggest install once downloaded

		// do check again
		
		
		
	// 
//	for each possible location
//		see if we have a java home that's in standard spot
//		if (RegOpenKeyEx (hKey,lpSubKey, ulOptions, samDesired, phkResult) {
//			
//			
//		} // end if
//    // handle to open key
//
//		if we do, and it's new enuf version, that's cool
//		if we do, but it's an old version, install new version
//		if we don't, install new version
//		if install went okay, return 1
//		if install went bad, return 0
	return 1 ;
	
} // end weHaveJ2RE


// Plug app into the system
int wePlugIntoSystem () {
	// local vars
	int result = 1 ;
	
	// if we're running from a CD,
	
//	if (runningFromCD()) 
//		// copy folder to hard drive
//		if (! copyAppFoldersToLocalDrive())
//			return 0 ;
	// TBD
	
	// if we're running on another machine
	// if (runningFromOtherMachine())
//	if (runningFromOtherMachine())
		// copy folder to local hard drive
//		if (! copyAppFoldersToLocalDrive())
//			return 0 ;
	// TBD
	
	// if we can determine paths
	if (setAllPaths() 
	
	// and can set environment variables
	&& setAllEnvVars() 
	
	// join the registry
	&& joinRegistry() 
	
	// place shortcuts
	&& placeShortcuts() 
	
	// per user choice set app to
	// handle particular types of documents
	&& hookupAllDocTypes() )
	
	
	// add to Add/Remove Programs listing
	// wiring up windows_uninstall.exe
	// TBD

		return 1 ;
	else
		return 0 ;
		
} // end wePlugIntoSystem


// returns 1 for a Reboot Now, 0 for a Reboot Later
int successFeedbackReboot () {
	char feedbackString [MAX_LINE] ;
	
	// start to build feedback string
	strcpy (feedbackString, SUCCESS_FEEDBACK_0) ;
	
	// add a reboot suggestion
	strcat (feedbackString, "\n\n") ;
	strcat (feedbackString, REBOOT_SUGGESTION) ;
	
	// return the dialog result
	return DisplayInfoReboot (feedbackString) ;
	
} // end function successFeedbackReboot


// returns 1 for a Start Up, 0 for an Exit Setup
int successFeedbackNoReboot () {
	char feedbackString [MAX_LINE] ;
	
	// start to build feedback string
	strcpy (feedbackString, SUCCESS_FEEDBACK_0) ;
	
	// return the dialog result
	return DisplayInfoExitStart (feedbackString) ;
	
} // end function successFeedback

// provide some feedback RE installation failure
int failureFeedback () {
	// local vars
	char feedbackString [MAX_LINE] ;

	// build the feedback string
	strcpy (feedbackString, FAILURE_FEEDBACK_0) ;
	
	// print the feedback string
	return DisplayInfoExit (feedbackString) ;

} // end function failureFeedback 


// figure out what version of Windows we're running
// returns a windows_version value
// returns 0 aka CANNOT_DETERMINE if it cannot determine the Windows version
int determineWindowsVersion (windows_version * ptrWinVersion) {
	// local vars
	OSVERSIONINFOEX osvi;
	BOOL bOsVersionInfoEx;

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
			* ptrWinVersion =  CANNOT_DETERMINE;
	} // end if

	// okay, we have version info in regular or EX form
	
	// switch out on major version #
	switch (osvi.dwMajorVersion) {
		
		// NT 351
		case 3:
			// switch out on minor version #
			switch (osvi.dwMinorVersion) {
				case 51:
					* ptrWinVersion = WIN_NT_351 ;
					break ;
				default:
					* ptrWinVersion = WIN_UNKNOWN_V3 ;
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
							* ptrWinVersion = WIN_95_OSR2 ;
						else
							* ptrWinVersion = WIN_95 ;
					} // end if it's not NT
					else {
						// it's NT 4
						* ptrWinVersion = WIN_NT_4 ;
						
						// let's get any service pack
						if (osvi.szCSDVersion[0] != 0) 
							g_NT_4_SP_Num = osvi.szCSDVersion[strlen(osvi.szCSDVersion) - 1]- '0' ;
					} // end else it's NT 4
					break ;
				
				// 98 or 98 SE
				case 10:
					// if it's SE
					if (osvi.szCSDVersion[1] == 'A' )
						* ptrWinVersion = WIN_98_SE ;
					else
						* ptrWinVersion = WIN_98 ;
					break ;
					
				// ME
				case 90:
					* ptrWinVersion = WIN_ME ;
					break ;
					
				// unknown
				default:
					* ptrWinVersion = WIN_UNKNOWN_V4 ;
					break ;
					
			} // end switch on minor version #
			break ;
		
		// 2000, XP, or .Net Server	
		case 5:					
			// switch out on minor version #
			switch (osvi.dwMinorVersion) {
				
				// 2000
				case 0:
					* ptrWinVersion = WIN_2K ;
					break ;
					
				// XP or .Net Server
				case 1:
					// if we have EX data (we should)
					if (bOsVersionInfoEx) { 
						// if we're nt workstation
						if(osvi.wProductType == VER_NT_WORKSTATION)
							* ptrWinVersion = WIN_XP ;
						else
							* ptrWinVersion = WIN_DOT_NET_SERVER ;
					} else 
						* ptrWinVersion = WIN_UNKNOWN_V5 ;
					break ;
					
				default:
					* ptrWinVersion = WIN_UNKNOWN_V5;
					break ;
			} // end switch on minor version #
			break ;
			
		// future stuff
		case 6:
			* ptrWinVersion = WIN_UNKNOWN_V6 ;
			break ;
			
		case 7:
			* ptrWinVersion = WIN_UNKNOWN_V7 ;
			break ;
		
		case 8:
			* ptrWinVersion = WIN_UNKNOWN_V8 ;
			break ;
		
		case 9:
			* ptrWinVersion = WIN_UNKNOWN_V9 ;
			break ;
		
		case 10:
			* ptrWinVersion = WIN_UNKNOWN_V10 ;
			break ;
		
		// very unknown
		// majorVersion < 3 or > 10
		default:
			* ptrWinVersion = WIN_VERY_UNKNOWN ;
			break ;
			
	} // end switch on major version #

	// done with determination
	
	// provide some feedback RE the Windows version
	return osFeedback (* ptrWinVersion);
	
} // end function determineWindowsVersion


// determine whether there's a Java 2 Runtime Environment for this OS
// returns 1 if OS is suitable, 0 if it's unsuitable
int java2available (windows_version windowsVersion) {
	// local vars
	int result = 0 ;
	char feedbackString [MAX_LINE] ;
	
	// switch out on windows version
	switch (windowsVersion) {
		case WIN_95:
		case WIN_95_OSR2:
		case WIN_98:
		case WIN_98_SE:
		case WIN_ME:
		case WIN_XP:
		case WIN_NT_4:
		case WIN_2K:
		case WIN_DOT_NET_SERVER:
		case WIN_UNKNOWN_V4:
		case WIN_UNKNOWN_V5:
		case WIN_UNKNOWN_V6:
		case WIN_UNKNOWN_V7:
			
			// all these are cool
			result = 1 ;
			break ;
			
		case WIN_NT_351:
		case WIN_UNKNOWN_V3:
		case WIN_VERY_UNKNOWN:
		default:
			// problems
			result = 0 ;
			
			// build up a feedback string
			strcpy (feedbackString, NO_JAVA_0) ;
			strcat (feedbackString, "\n") ;
			strcat (feedbackString, NO_JAVA_1) ;
			strcat (feedbackString, "\n\n") ;

			// print the feedback string
			DisplayInfoContCancel (feedbackString) ;
						
			break ;
			
		} // end switch out on Windows version
	
	// done
	return result;
	
} // end function java2available 


int machineHasNuffOomph () {
	
	// if we have enough memory
	// TBD
	
	// and a fast enuf processsor
	// TBD
	
		// we cool
		return 1 ;
		
	// else
		// we ain't
		// return 0 ;
		
	// end if-else
	
} // end function machineHasNuffOomph


// sets up paths that are useful to us
int setAllPaths () {
	
	// sets the following paths
	//	g_AppHomePath
	
	// if installed from CD, we are already set
	// if we installed from another drive, we are already set/
	// we are installing from dir on hard drive
	
	char shortPathBuffer[MAX_PATH] ;
	
	// if we can't obtain short pathname for current directory
	if (! getShortPathCurDir (shortPathBuffer))
		// leave in failure
		return 0 ;

	// append a backslash
	strcat (shortPathBuffer, "\\") ;
	
	strcpy (g_App_Home_Path, shortPathBuffer) ;
	
	return 1 ;

} // end function setAllPaths


// sets up all environment variables
// we try to keep to a minimal set
int setAllEnvVars () {
	
	return ( set_APP_HOME() 
		// & set_xxx_YYY()
		// & set_xxx_YYY()
		// & set_xxx_YYY() 
		) ;
	
} // end function setAllEnvVars


// sets app up in the Windows registry
// we try to keep to a minimal set of entries
int joinRegistry () {
	// local vars
	HKEY rootKey = APP_REGISTRY_ROOT_KEY ;
	char appKeyPath [MAX_REG_PATH] ;
	
	HKEY appKey = NULL ;
	DWORD createKeyDisposition = 0 ;
	
	strcpy(appKeyPath, APP_REGISTRY_PATH) ;
	strcat(appKeyPath, APP_REG_KEY_STRING) ;
	
	
	return 1 ;
	
//	#define APP_REGISTRY_ROOT_KEY  HKEY_LOCAL_MACHINE
//	#define APP_REGISTRY_KEY_PATH  "Software\\"
//
//	#define APP_REG_KEY_STRING  "JOE"
	
//	// local vars
//	HKEY rootKey = NULL ;
//	char keyPath [MAX_REG_PATH] ;
//	
//	HKEY enviroKey = NULL;
//	DWORD createKeyDisposition = 0;
//
//	LONG setResult = 0;
//	LONG closeResult = 0;
//	
//	// are we setting this for all users, or just the current user ?
//	switch (envTarget) {
//		case USER:
//			rootKey = USER_ENVIRONMENT_ROOT_KEY ;
//			strcpy (keyPath, USER_ENVIRONMENT_KEY_PATH) ;
//			break ;
//			
//		case SYSTEM:
//		default:
//			rootKey = SYSTEM_ENVIRONMENT_ROOT_KEY ;
//			strcpy (keyPath, SYSTEM_ENVIRONMENT_KEY_PATH) ;
//			break ;
//	} // end switch target
//		
//	// try to open up the environment key
//	// if it doesn't exist, will try to create it
//	if (RegCreateKeyEx (rootKey, keyPath, 0,
//				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
//				& enviroKey, & createKeyDisposition)== ERROR_SUCCESS) {
//	
//		// okay, we've got the environment key
//		
//		// try to write the name/value pair to it
//		// will create n/v pair if it doesn't already exist
//		setResult = RegSetValueEx (enviroKey, envVarName, 0, REG_SZ,
//				(LPBYTE)envVarValue, strlen(envVarValue) + 1) ;
//		
//		// close the environment key
//		closeResult = RegCloseKey (enviroKey) ;
//		
//		// done
//		return ((setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS)) ;
//		
//	// else we couldn't get at the environment key
//	} else {
//		// sigh
//		return 0 ;
//	} // end if-else

		
} // end function joinRegistry


// place app shortcuts
// this is based on user choices
int placeShortcuts(){
	// local vars
	shortcut_placement shortcutPlacement ;
	int result = 1 ;
	int shortcutCount = 0 ;
	
	// if we can't get user choices, leave
	if (! getUserChoicesReShortcutPlacement(& shortcutPlacement)) return 0 ; 
	
	// if user chose to put shortcut in program menu
	if (shortcutPlacement.programsMenu){
		result &= shortcutToProgramsMenu() ;
		if (! result) return result ;
	} // end if
	
	// if user chose to put shortcut in upper part of start menu
	if (shortcutPlacement.startMenu){
		result &= shortcutToStartMenu() ;
		if (! result) return result ;
	} // end if
	
	// if user chose to put shortcut to desktop
	if (shortcutPlacement.desktop){
		result &= shortcutToDesktop() ;
		if (! result) return result ;
	} // end if
	
	// if user chose to put shortcut in the Quick Launch toolbar of the Taskbar
	if (shortcutPlacement.quickLaunch){
		result &= shortcutToQuickLaunch() ;
		if (! result) return result ;
	} // end if
	
	// if user chose to put shortcut in popup context menu
	if (shortcutPlacement.contextMenu){
		result &= shortcutToContextMenu() ;
		if (! result) return result ;
	} // end if
	
	// done
	return result ;
	
} // end function placeShortcuts


// set up the APP_HOME environment variable
// returns 1 if it succeeds, 0 if it fails
int set_APP_HOME () {
	// local vars
	char introLines[] = APP_HOME_REM;
	
	// return result of trying to set 
	// environment var JOE_HOME to g_App_Home_Path
	return setEnvVar(APP_HOME, g_App_Home_Path, introLines, g_Windows_Version);
	
} // end set_app_HOME


// get a shortpath version of the current directory
int getShortPathCurDir (char * shortPathBuffer) {
	// local vars
	char longPathBuffer[MAX_PATH]; 
	int longPathLength = 0 ;
	
	// seek the long pathname for the current directory
	longPathLength = GetCurrentDirectory(MAX_PATH, longPathBuffer); 

	// if there was problem getting the long pathname
	if (longPathLength == 0)
		// return in failure
		return 0 ;
		
	// try to get the short form of that pathname
	return GetShortPathName(longPathBuffer, shortPathBuffer, MAX_PATH) ;
	
} // end function getShortPathCurDir


// set an environment variable
// returns 1 if successfully set and user chooses to continue, 0 if not
int setEnvVar (char * varName, char * varValue, char * introLines, windows_version windowsVersion) {
	// local vars
	int result ;
	DWORD broadcastResult ;
	
	// switch out on windows version
	switch (windowsVersion) {
		case WIN_95:
		case WIN_95_OSR2:
		case WIN_98:
		case WIN_98_SE:
			// win 9x OSes use autoexec.bat and a reboot
			result = setAutoExecEnvVar (varName, varValue, introLines) ;
			
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
			
			// if we can't set it for all users, our first choice,
			if (! (result = setRegistryEnvVar (varName, varValue, SYSTEM)))
				
				// we'll set it for just the current user
			
			// if we succeeded
			if (result) 
				// broadcast the news
				// thereby ??? avoiding the need to reboot
				SendMessageTimeout(HWND_BROADCAST, WM_SETTINGCHANGE, 0,
				(LPARAM) "Environment", SMTO_ABORTIFHUNG,
				5000, &broadcastResult);
			
			break ;
			
		case WIN_NT_351:
		case WIN_UNKNOWN_V3:
		case WIN_VERY_UNKNOWN:
		default:
			// we don't work on any other windows systems (NT 3.5 and less)
			result = 0 ;
			break ;
	} // end switch
		
	// provide feedback
	return (result & sevFeedback(result, varName, varValue)) ;
	
} // end setEnvVar


// set an environment variable via the registry
// used for Windows NT, 2K, ME, XP, and beyond
int setRegistryEnvVar (char * envVarName, char * envVarValue, environment_target envTarget) {

	// local vars
	HKEY rootKey = NULL ;
	char keyPath [MAX_REG_PATH] ;
	
	HKEY enviroKey = NULL;
	DWORD createKeyDisposition = 0;

	LONG setResult = 0;
	LONG closeResult = 0;
	
	// are we setting this for all users, or just the current user ?
	switch (envTarget) {
		case USER:
			rootKey = USER_ENVIRONMENT_ROOT_KEY ;
			strcpy (keyPath, USER_ENVIRONMENT_KEY_PATH) ;
			break ;
			
		case SYSTEM:
		default:
			rootKey = SYSTEM_ENVIRONMENT_ROOT_KEY ;
			strcpy (keyPath, SYSTEM_ENVIRONMENT_KEY_PATH) ;
			break ;
	} // end switch target
		
	// try to open up the environment key
	// if it doesn't exist, will try to create it
	if (RegCreateKeyEx (rootKey, keyPath, 0,
				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
				& enviroKey, & createKeyDisposition)== ERROR_SUCCESS) {
	
		// okay, we've got the environment key
		
		// try to write the name/value pair to it
		// will create n/v pair if it doesn't already exist
		setResult = RegSetValueEx (enviroKey, envVarName, 0, REG_SZ,
				(LPBYTE)envVarValue, strlen(envVarValue) + 1) ;
		
		// close the environment key
		closeResult = RegCloseKey (enviroKey) ;
		
		// done
		return ((setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS)) ;
		
	// else we couldn't get at the environment key
	} else {
		// sigh
		return 0 ;
	} // end if-else

} // end function setRegistryEnvVar


// set an environment variable via autoexec.bat
// used for Windows 95, 98, 98 SE
int setAutoExecEnvVar (char * varName, char * varValue, char * introLines) {
	
	// local vars
	char autoExecPathBuffer [MAX_PATH] ;
	char lineBuffer [MAX_LINE] ;
	char lineTestBuffer [MAX_LINE] ;
	char tempFilePathBuffer [MAX_LINE] ;
	char wordBuffer [MAX_LINE] ;
	int result = 0 ;
	int position ;
	int coolAsIs = 0 ;
	int madeAChange = 0 ;
	int writeToTemp = 1 ;
	int tempIndex ;
	char newSettingBuffer [MAX_LINE] ;
	char oldAutoExecNewNameBuffer [MAX_LINE] ;
	
	// if we can't get msdos.sys's path, leave in failure
	if (getAutoExecPath(autoExecPathBuffer) == 0) return result ;
	
	// try to open autoexec.bat for reading
	FILE * autoExec = fopen(autoExecPathBuffer, "r" ) ;
	
	// if we failed, leave in failure
	if (autoExec == 0) return 0 ;
	
	// try to create  temp file in same directory for writing 
	strcpy(tempFilePathBuffer, autoExecPathBuffer) ;
	tempIndex = trimFileOffPath(tempFilePathBuffer) ;
	
	// get a unique file name for that dir
	GetTempFileName(tempFilePathBuffer, "joe", 0, tempFilePathBuffer) ;
	
	// try to open that file 
	FILE * tempFile = fopen(tempFilePathBuffer, "w") ;
	
	// if we failed, leave in failure
	if (tempFile == 0) return 0 ;
	
	// okay, everybody's open
	
	// for each line in autoexec.bat
	while (fgets(lineBuffer, MAX_LINE, autoExec)) {
		
		// make a copy of the line
		strcpy (lineTestBuffer, lineBuffer) ;
		
		// convert copy to uppercase for ease of comparison
		strToUpper(lineTestBuffer) ;
		
		// try to get first word of line
		// if there are no words
		if ((getWord (1, lineTestBuffer, wordBuffer))< 0) {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		// if first word is not set
		if (strcmp(wordBuffer,"SET") != 0)  {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		
		// try to get second word of line
		// if not able to
		if ((getWord (2, lineTestBuffer, wordBuffer))< 0)  {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		
		// if second word is not varName
		if (strcmp(wordBuffer,varName) != 0)  {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		
		// try to get third word of line
		// if not able to
		if ((getWord (3, lineTestBuffer, wordBuffer))< 0)  {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		
		// if third word is not equals sign
		if (strcmp(wordBuffer,"=") != 0)  {
			
			// write line to temp file
			if (writeToTemp) fputs(lineBuffer, tempFile) ;
			
			// next line
			continue ; 
		} // end if
		
		
		// try to get fourth word of line
		position = getWord (4, lineTestBuffer, wordBuffer);
		
		// fourth word is current setting of varName
		// if it doesn't exist or it doesn't match varValue
		if ((position < 0) || (strcmp(wordBuffer,varValue)!= 0)) {
			
			// if we're writing to the temp file
			if (writeToTemp) {
				
				// create new setting string
				strcpy (newSettingBuffer, "SET ") ;
				strcat (newSettingBuffer, varName) ;
				strcat (newSettingBuffer, "=") ;
				strcat (newSettingBuffer, varValue) ;
				strcat (newSettingBuffer, "\n") ;
				
				// write that to the temp file
				fputs(newSettingBuffer, tempFile) ;
				
				// note that we made a change
				madeAChange = 1 ;
			} // end if 
			
			// next line
			continue ;
			
		} else {
			// we have a match
			coolAsIs = 1 ;
			writeToTemp = 0 ;
			
			// no need to write line to temp file,
			// cuz we'll be discarding temp file
		}// end else
		
	}  // end while

	// close the autoexec.bat file
	fclose (autoExec) ;
	
	// if we didn't find it in autoexec.bat
	if (! (coolAsIs || madeAChange)) {
		
		// write some intro lines
		fputs(introLines, tempFile) ;

		// create new setting string
		strcpy (newSettingBuffer, "SET ") ;
		strcat (newSettingBuffer, varName) ;
		strcat (newSettingBuffer, "=") ;
		strcat (newSettingBuffer, varValue) ;
		
		// write setting to temp file
		fputs(newSettingBuffer, tempFile) ;
		
	} // end if we didn't find it in autoexec.bat
	
	// close the temp file
	fclose (tempFile) ;
	
	// if we were cool as is
	if (coolAsIs) {
		// discard temp file
		remove(tempFilePathBuffer) ;
		
	// else we made a change or it wasn't in autoexec.bat
	} else {
		// build up new autoexec name
		strcpy (oldAutoExecNewNameBuffer, autoExecPathBuffer) ;
		strcat (oldAutoExecNewNameBuffer, ".pre-Joe") ;
		
		// make sure no old copies of it are around
		remove (oldAutoExecNewNameBuffer) ;
		
		// rename existing autoexec.bat to that name
		rename(autoExecPathBuffer, oldAutoExecNewNameBuffer) ;
		
		// rename temp file as autoexec.bat
		rename (tempFilePathBuffer, autoExecPathBuffer) ;
		
	} // end else
	

	// done
	return 1 ; // <<temp result ;
	

} // end function setEnvVarWin9x

// determine the path to a 9x system's boot drive's autoexec.bat file
// if one doesn't exist, try to create one
// if all goes well, returns 1
// if not, returns 0	
int getAutoExecPath(char * pathBuffer) {
	
	// local vars
	FILE * newFile ;
	
	// TBD fix this
	// msDosSys is on 95/98/ME systems
	// autoexec.bat is used by install on 95/98/ME systems
	// TBD [srk] check this with compressed boot volumes
	// although those are rare these days
	// this may not work correctly on them
	
	// if there's an msdos.sys file boot drive indicator
	if (msDosSysExtract(PATHS, HOST_WIN_BOOT_DRV, pathBuffer)) {
		
		// use that to build a path
		strcat(pathBuffer, ":\\") ;
		strcat(pathBuffer, AUTOEXEC_BAT) ;
		
	// else we use the standard path, good for most systems
	} else {
		strcpy(pathBuffer, STD_AUTOEXEC_PATH) ;
	} // end if-else
	
	// if the pathed file exists ...
	if (fileExists(pathBuffer))
		return 1 ;
		
	else {
		// try to create the file
		newFile = fopen(pathBuffer, "a+") ; 
		
		// if we failed ...
		if (newFile == NULL)
			return 0 ;
			
		// else we succeeded
		else {
			// close the file
			fclose(newFile) ;
			
			return 1 ;
		} // end else we succeeded

	} // end else
	
} // end function getAutoExecPath


// try to determine the path to a 9x system's msdos.sys file
// if all goes well, returns 1
// if not, returns 0	
int getMsDosSysPath(char * pathBuffer) {
	
	// TBD make this real
	// msdos.sys is in root folder

	// use the standard path, good for most systems
	strcpy(pathBuffer, STD_MS_DOS_SYS_PATH) ;

	// if the pathed file exists ...
	if (fileExists(pathBuffer))
		return 1 ;
		
	else 
		return 0 ;
	
} // end function getMsDosSysPath

// extract a value from an msdos.sys file on a 9x system
int msDosSysExtract(ms_dos_sys_section section, char * name, char * valueBuffer) {

	// local vars
	char msDosSysPathBuffer [MAX_PATH] ;
	
	// if we can't get msdos.sys's path, leave in failure
	if (getMsDosSysPath(msDosSysPathBuffer) == 0) return 0 ;
	
	// if the file exists
		// if it contains ascii data
			// get to section
				// until get to section or out of lines
					// read a line
					// is it section ?
			// for each line
				// is the first word in that line what we are looking for
	// TBD
	// fake for now
	strcpy (valueBuffer, "c") ;
	
	return 1 ;
	
} // end function msDosSysExtract

// uppercase a string
int strToUpper (char * someString) {
	
	int pointer = 0 ;
	char curChar ;
	
	while ((curChar = someString[pointer]) != 0) {
		
		someString[pointer] = toupper(curChar) ;
		
		pointer++ ;
	}
	
	return pointer ;
	
} // end function  strToUpper


// get a word from an text file line
// used here on msdos.sys and autoexec.bat files
// within a line, words are separated by spaces, tabs, or equal signs
// equal signs are considered words
int getWord (int whichWord, char * sourceString, char * wordBuffer) {
	
	// note: whichWord is a 1-based selector 
	
	// local vars
	int charIndex ;
	int wordIndex ;
	char curChar ;
	int wordCharIndex = 0;
	int thisIsIt ;
	int wordStart = -1 ;
	
	// repeat until at specified word or done with string
	for (charIndex = 0, curChar = sourceString[charIndex], wordIndex = 0, thisIsIt = 0;
		(curChar != 0) && (thisIsIt != 1);
		){
		
		// move thru tabs and spaces
		while ((curChar == ' ') || (curChar == '\t')) {
			charIndex ++ ;
			curChar = sourceString[charIndex] ;
		} // end while
	
		// if we have a newline, we're gone
		if (curChar == '\n') break ;
		
		// we're at the start of a word
		wordIndex ++ ;
		
		// determine if this is the word we're looking for 
		thisIsIt = (wordIndex == whichWord) ;
		
		// if this is it, record the start position
		if (thisIsIt) wordStart = charIndex ;
		
		// plow thru the word, storing it if it's the one
		
		// while we're in the word
		while (1) {
			
			// if this is our word
			if (thisIsIt) {
				// write the char
				wordBuffer[wordCharIndex++] = curChar ;
			} // end if this is our word

			// grab the next char
			curChar = sourceString [++charIndex] ;
			
			// if it's a standard word breaker 
			if ((curChar == ' ') || (curChar == '\t')
				|| (curChar == '\n') || (curChar == 0)) {
				
				// if this is our word
				if (thisIsIt) {
					// finish off the word string
					wordBuffer[wordCharIndex] = 0 ;
				} // end if
				
				// break out of this loop
				break ;
			} // end if it's a word breaker
			
			// if it's not a standard word breaker, but follows an
			// = sign, it's a word breaker
			if (sourceString[charIndex - 1] == '=') {
				
				// if this is our word
				if (thisIsIt) {
					// finish off the word string
					wordBuffer[wordCharIndex] = 0 ;
				} // end if
				
				// break out of this loop
				break ;
			} // end if it's a word breaker
			
			// if it's an = sign
			if (curChar == '=') {
				
				// if this is our word
				if (thisIsIt) {
					// finish off the word string
					wordBuffer[wordCharIndex] = 0 ;
				} // end if
				
				// break out of this loop
				break ;
			} // end if it's an = sign
			
		} // end for each char in the word
		
	} // end for
		
	// return starting position of found word
	// -1 if word not found
	return wordStart ;
	
} // end getWord


// given a pathname, remove the filename
// returns the length of the trimmed path
int trimFileOffPath (char * path) {
	// local vars
	char * trimPtr ;
	
	// find the last occurrence of \ in the path
	trimPtr = strrchr(path, '\\') ;
	
	// if there were no occurrences of \, the whole thing gets nuked
	if (trimPtr == NULL) {
		path[0] = 0 ;
		return 0 ;
	} // end if
	
	// point just past the slash
	trimPtr ++ ;
	
	// place a string terminator there
	* trimPtr = 0 ;
	
	// return the new string's length
	return strlen(path) ;
	
} // end trimFileOffPath



// provide some feedback RE the OS we're attempting to install under
// returns 1 if user chooses to continue, 0 if user chooses to cancel
int osFeedback (windows_version windowsVersion) {
	// local vars
	char feedbackString [MAX_LINE] ;
	int result = 1 ;
	
	// build the feedback string
	strcpy (feedbackString, OS_FEEDBACK_STRING_0) ;
	strcat (feedbackString, WINDOWS_VERSION_STRINGS[windowsVersion]) ;
	strcat (feedbackString, OS_FEEDBACK_STRING_1) ;
	strcat (feedbackString, "\n\n") ;

	// print the feedback string
	return DisplayInfoContCancel (feedbackString) ;

} // end function osFeedback 


// provide some feedback RE setting an environment variable
// returns 1 if user chooses to Continue, 0 if user chooses to Cancel
int sevFeedback (int result, char * varName, char * varValue) {
	
	char feedbackString [MAX_LINE] ;
	
	if (result) 
		strcpy (feedbackString, SEV_FEEDBACK_STRING_0) ;
	else 
		strcpy (feedbackString, SEV_FEEDBACK_STRING_1) ;
		
	strcat (feedbackString, SEV_FEEDBACK_STRING_2) ;
	strcat (feedbackString, varName) ;
	strcat (feedbackString, "\n  ") ;
	strcat (feedbackString, SEV_FEEDBACK_STRING_3) ;
	strcat (feedbackString, varValue) ;
	strcat (feedbackString, "\n\n") ;
	return DisplayInfoContCancel (feedbackString) ;
	
} // end sevFeedback


int ensureSuitableEnvironment () {
	// make sure system has enough memory
	// 48 mb is a minimum
	// TBD
	// fake for now
	return 1 ;
} // end function ensureSuitableEnvironment


// do we need to reboot ??
// (we do if we've written to autoexec.bat)
int rebootRequired (windows_version windowsVersion) {
	int result = 0 ;
	
	switch (windowsVersion) {
		case WIN_95:
		case WIN_95_OSR2:
		case WIN_98:
		case WIN_98_SE:
			// win 9x use autoexec.bat and a reboot
			result = 1 ;
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
			// these use registry, broadcast, no reboot
			result = 0 ;
			break;
			
		default: // should never get here
			result = 0 ;
			break ;
		
	} // end switch
	
	// done
	return result ;
	
} // end function rebootRequired


// see if the user wants to place program shortcuts in
// any of the standard locations
// returns 1 if there are no problems, 0 if there are
// user prefs placed into a shortcut_placement data structure
int getUserChoicesReShortcutPlacement (shortcut_placement * ptrShortcutPlacment) {
	// TBD
	
	// fake for now
	ptrShortcutPlacment->programsMenu = 1 ;
	ptrShortcutPlacment->startMenu = 0 ;
	ptrShortcutPlacment->desktop = 1 ;
	ptrShortcutPlacment->quickLaunch = 0 ;
	ptrShortcutPlacment->contextMenu = 0 ;
	
	// done
	return 1 ;
	
} // end function getUserChoicesReShortcutPlacement


// add a shortcut to the app to the Programs menu
// returns 1 if successful and user chooses to continue, 0 if not
int shortcutToProgramsMenu() {
	// local vars
	char programsMenuPath [MAX_PATH] ;
	int result = 0 ;
	char ourInvPath [MAX_PATH] ;
	BOOL copyCancel = FALSE ;
	char feedbackString [MAX_LINE] ;
	
	// get location of windows menu
	if (GetWindowsDirectory(programsMenuPath, MAX_PATH + 1)== 0) return 0 ;
	
	// build path to programs menu directory
	strcat (programsMenuPath, PROGRAMS_MENU_SUBPATH) ;
	
	// add the shortcut filename to the programs menu path
	strcat (programsMenuPath, SHORTCUT_NAME) ;
		
	// build the source path to the shortcut
	strcpy (ourInvPath, g_App_Home_Path) ;
	strcat (ourInvPath, SHORTCUT_PATH) ;
	strcat (ourInvPath, SHORTCUT_NAME) ;
		
	// store a copy of it in the programs menu dir
	result = (CopyFile(ourInvPath, programsMenuPath, FALSE) != 0) ;
		
	// feedback
	if (result) 
		strcpy (feedbackString, SHORTCUT_ADDED) ;
	else
		strcpy (feedbackString, SHORTCUT_NOT_ADDED) ;
	strcat(feedbackString, APP_NAME_STRING) ;
	strcat(feedbackString, SHORTCUT_TO_PROG_MENU) ;
	strcat(feedbackString, "\n") ;
	
	return (result & DisplayInfoContCancel (feedbackString)) ;
		
} // end function shortCutToProgramsMenu


// add a shortcut to the app to the top part of the Start menu
// returns 1 if successful and user chooses to continue, 0 if not
int shortcutToStartMenu() {
	// local vars
	char startMenuPath [MAX_PATH] ;
	int result = 0 ;
	char ourInvPath [MAX_PATH] ;
	BOOL copyCancel = FALSE ;
	char feedbackString [MAX_LINE] ;
	
	// get location of windows menu
	if (GetWindowsDirectory(startMenuPath, MAX_PATH + 1)== 0) return 0 ;
	
	// build path to start menu directory
	strcat (startMenuPath, START_MENU_SUBPATH) ;
	
	// add the shortcut filename 
	strcat (startMenuPath, SHORTCUT_NAME) ;
		
	// build the source path to the shortcut
	strcpy (ourInvPath, g_App_Home_Path) ;
	strcat (ourInvPath, SHORTCUT_PATH) ;
	strcat (ourInvPath, SHORTCUT_NAME) ;
		
	// store a copy of it in the programs menu dir
	result = (CopyFile(ourInvPath, startMenuPath, FALSE) != 0) ;

	// feedback
	if (result) 
		strcpy (feedbackString, SHORTCUT_ADDED) ;
	else
		strcpy (feedbackString, SHORTCUT_NOT_ADDED) ;
	strcat(feedbackString, APP_NAME_STRING) ;
	strcat(feedbackString, SHORTCUT_TO_START_MENU) ;
	strcat(feedbackString, "\n") ;
	
	return (result & DisplayInfoContCancel (feedbackString)) ;
	
} // end function shortcutToStartMenu


// add a shortcut to the app to the desktop
// returns 1 if successful, 0 if not
int shortcutToDesktop() {
	// local vars
	char desktopMenuPath [MAX_PATH] ;
	int result = 0 ;
	char ourInvPath [MAX_PATH] ;
	BOOL copyCancel = FALSE ;
	char feedbackString [MAX_LINE] ;
	
	// get location of windows menu
	if (GetWindowsDirectory(desktopMenuPath, MAX_PATH + 1)== 0) return 0 ;
	
	// build path to desktop directory
	strcat (desktopMenuPath, DESKTOP_MENU_SUBPATH) ;
	
	// add the shortcut filename 
	strcat (desktopMenuPath, SHORTCUT_NAME) ;
		
	// build the source path to the shortcut
	strcpy (ourInvPath, g_App_Home_Path) ;
	strcat (ourInvPath, SHORTCUT_PATH) ;
	strcat (ourInvPath, SHORTCUT_NAME) ;
		
	// store a copy of it in the programs menu dir
	result = (CopyFile(ourInvPath, desktopMenuPath, FALSE) != 0) ;
		
	// feedback
	if (result) 
		strcpy (feedbackString, SHORTCUT_ADDED) ;
	else
		strcpy (feedbackString, SHORTCUT_NOT_ADDED) ;
	strcat(feedbackString, APP_NAME_STRING) ;
	strcat(feedbackString, SHORTCUT_TO_DESKTOP) ;
	strcat(feedbackString, "\n") ;

	return (result & DisplayInfoContCancel (feedbackString)) ;
	
} // end function shortcutToDesktop


// add a shortcut to the app to the Quick Launch toolbar of the Taskbar
// returns 1 if successful, 0 if not
int shortcutToQuickLaunch() {
	// TBD
	
	// fake for now
	
	// done 
	return 1 ;
	
} // end function shortcutToQuickLaunch


// add a shortcut to the app to the Context menu
// returns 1 if successful, 0 if not
int shortcutToContextMenu() {
	// TBD
	
	// fake for now
	
	// done 
	return 1 ;
	
} // end function shortcutToContextMenu


// per user choice, hook the app up so 
// that it handles some doc types
int hookupAllDocTypes() {
	
//typedef struct {
//	char type_path [6] ;  // example: ".opml"
//	char app_doc_path [100] ; // example: "JOE.OPML.document"
//	char app_doc_open_cmd_path [100] ; // example: "\\Shell\\Open\\Command"
//	
//	} doc_type_info	

	// local vars
	int result = 1 ;
	doc_type_info someDocTypeInfo ;
	
	// TBD generalize this
	// an array of doc types info in the .h file
	// for each doc type in that array
		// TBD - fake for now
		strcpy(someDocTypeInfo.type_path, DOC_TYPES_OPML_PATH) ;
		strcpy(someDocTypeInfo.app_doc_path, DOC_TYPES_JOE_OPML_PATH) ;
		strcpy(someDocTypeInfo.app_doc_open_cmd_path, DOC_TYPES_JOE_OPML_OPEN_CMD_PATH) ;
		result &= hookupDocType(& someDocTypeInfo) ;

	// after all doc types are done ...
	printf ("\n") ;
	
	// done
	return result ;
	
} // end function hookupAllDocTypes


// hook up one doc type
int hookupDocType(doc_type_info * ptr2DocTypeInfo) {

	// local vars
	HKEY rootKey = DOC_TYPES_ROOT_KEY ;
	HKEY utilKey ;
	DWORD createKeyDisposition = 0;
	char utilString [MAX_REG_PATH] ;
	LONG setResult = 0;
	LONG closeResult = 0;
	int resultPartOne = 0 ;
	int resultPartTwo = 0 ;
	int result = 0 ;
	char feedbackString [MAX_LINE] ;
	
	
	// delete any existing .doc key
	allWin32RegDeleteKey(rootKey, ptr2DocTypeInfo->type_path) ;

	// try to create a new .doc key
	if (RegCreateKeyEx (rootKey, ptr2DocTypeInfo->type_path, 0,
				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
				& utilKey, & createKeyDisposition)== ERROR_SUCCESS) {
	
		// okay, we've got it
		
		// try to write our default value string
		strcpy(utilString, ptr2DocTypeInfo->app_doc_path) ;
		setResult = RegSetValueEx (utilKey, 0, 0, REG_SZ,
				(LPBYTE)utilString, strlen(utilString) + 1) ;
		
		// close the key
		closeResult = RegCloseKey (utilKey) ;
		
		// done
		resultPartOne = (setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS) ;
		
	// else we couldn't create a new .doc key
	} else {
		// sigh
		resultPartOne = 0 ;
	} // end if-else
	

	// delete any existing app.type key
	allWin32RegDeleteKey(rootKey, ptr2DocTypeInfo->app_doc_path) ;

	// build up app.type keypath string
	strcpy(utilString, ptr2DocTypeInfo->app_doc_path) ;
	strcat(utilString, ptr2DocTypeInfo->app_doc_open_cmd_path) ;
	
	// try to create a new app.type key
	if (RegCreateKeyEx (rootKey, utilString, 0,
				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
				& utilKey, & createKeyDisposition)== ERROR_SUCCESS) {
	
		// okay, we've got it

		// build up a string
		strcpy (utilString, g_App_Home_Path) ;
		strcat (utilString, EXE_NAME) ;
		strcat (utilString, " \"%1\"") ;
		
		// try to write that default value string
		setResult = RegSetValueEx (utilKey, 0, 0, REG_SZ,
				(LPBYTE)utilString, strlen(utilString) + 1) ;
		
		// close the key
		closeResult = RegCloseKey (utilKey) ;
		
		// done
		resultPartTwo = (setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS) ;
		
	// else we couldn't create a new .opml key
	} else {
		// sigh
		resultPartTwo = 0 ;
	} // end if-else
	
	// how'd we do ?
	result = resultPartOne & resultPartTwo ;
	
	// feedback
	if (result) 
		strcpy (feedbackString, DOC_TYPE_HOOKED) ;
	else
		strcpy (feedbackString, DOC_TYPE_NOT_HOOKED) ;
	strcat(feedbackString, ptr2DocTypeInfo->type_path) ;
	strcat(feedbackString, DOC_TYPE_HOOKER_0) ;
	strcat(feedbackString, APP_NAME_STRING) ;
	strcat(feedbackString, ".") ;
	strcat(feedbackString, "\n") ;
	
	return result & DisplayInfoContCancel (feedbackString) ;
	
	// done
} // end function hookupDocType


// delete a registry key and all its contents and subkeys
// takes care of win32 differences in basic registry api's
int allWin32RegDeleteKey (HKEY rootKey, char * keyPath) {
	// local vars
	int result ;
	
	// case out on the version of windows
	switch (g_Windows_Version) {
		case WIN_95:
		case WIN_95_OSR2:
			result = (RegDeleteKey(rootKey, keyPath)== ERROR_SUCCESS) ;
			break ;
			
		case WIN_98:
		case WIN_98_SE:
		case WIN_ME:
		case WIN_XP:
		case WIN_2K:
		case WIN_DOT_NET_SERVER:
		case WIN_UNKNOWN_V4:
		case WIN_UNKNOWN_V5:
		case WIN_UNKNOWN_V6:
		case WIN_UNKNOWN_V7:
			result = (SHDeleteKey (rootKey, keyPath) == ERROR_SUCCESS) ;
			break ;
			
		case WIN_NT_4:
			// if we have the api available, use SH
			// else do the recursive RegDeleteKey ;
			// TBD -- fake for now
			result = (SHDeleteKey (rootKey, keyPath) == ERROR_SUCCESS) ;
			break ;
			
		case WIN_NT_351:
		case WIN_UNKNOWN_V3:
		case WIN_VERY_UNKNOWN:
		default:
			// problems
			result = 0 ;
			
			break ;
			
		} // end switch out on Windows version
		
	return result ;
	
} // end function 


// determine whether the java app launchers
// are in a good place
int javaAppLaunchersCool() {
	// local vars
	char launcherPath0[MAX_PATH + 1] ;
	char launcherPath1[MAX_PATH + 1] ;
	char windowsDirPath[MAX_PATH + 1] ;
	
	// build up string to standard location
	
	// get location of windows dir
	if (GetWindowsDirectory(windowsDirPath, MAX_PATH + 1)== 0) return 0 ;
	
	// let's see if we're in there
	strcpy(launcherPath0, windowsDirPath) ;
	strcat(launcherPath0, "\\") ;
	strcpy(launcherPath1, launcherPath0) ;

	// bifurcate
	strcat(launcherPath0, LAUNCHER_0) ;
	strcat(launcherPath1, LAUNCHER_1) ;
	
	// are they there ?
	if (fileExists(launcherPath0) & fileExists(launcherPath1))
		return 1 ;
		
	// they're not there
	// let's try the system dirs
	strcpy(launcherPath0, windowsDirPath) ;

	// case out on windows version
	switch (g_Windows_Version) {
		
	case WIN_95:
	case WIN_95_OSR2:
	case WIN_98:
	case WIN_98_SE:
	case WIN_ME:
		strcat (launcherPath0, SYSTEM_DIR_9X) ;
		break ;
	
	case WIN_XP:
	case WIN_NT_4:
	case WIN_2K:
	case WIN_DOT_NET_SERVER:
	case WIN_UNKNOWN_V4:
	case WIN_UNKNOWN_V5:
	case WIN_UNKNOWN_V6:
	case WIN_UNKNOWN_V7:
	case WIN_UNKNOWN_V8:
	case WIN_UNKNOWN_V9:
	case WIN_UNKNOWN_V10:
		strcat (launcherPath0, SYSTEM_DIR_NT) ;
		break ;
		
	default:
		// we shouldn't get here
		return 0 ;
		break ;
		
	} // end switch
	
	
	// final touches
	strcat(launcherPath0, "\\") ;

	// copy
	strcpy(launcherPath1, launcherPath0) ;

	// bifurcate
	strcat(launcherPath0, LAUNCHER_0) ;
	strcat(launcherPath1, LAUNCHER_1) ;
	
	// are they there ?
	if (fileExists(launcherPath0) & fileExists(launcherPath1))
		return 1 ;
		
	// done, not found
	return 0 ;

}  // end function javaAppLaunchersCool


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


// are the proper Java registry entries in place ?
// and are they correct ??
int javaRegEntriesCool() {

	// TBD
//
//
//
//#define JAVA_ROOT_KEY  HKEY_LOCAL_MACHINE
//#define JRE_HOME_PATH  "Software\\JavaSoft\\Java Runtime Environment"
//
//

	// is there a JRE key ??
	// does it have subkeys ??
	// is one of em at least 1.3 ??
	// if it is ... does it have a JavaHome setting
	// if so ... does it point to a reality ??
	// if so .... we cool
	// otherwise we no cool

	// local vars
	HKEY rootKey = JAVA_ROOT_KEY ;
	char keyPath [MAX_REG_PATH] ;
	
	HKEY jreInfoKey = NULL;

	LONG getResult = 0;
	LONG closeResult = 0;
	int funcResult = 0 ;
	
	// set up path to JRE info
	strcpy(keyPath, JRE_HOME_PATH) ;
	
	// if we can open the JRE info key
	if (RegOpenKeyEx(rootKey, keyPath, 0, KEY_READ, & jreInfoKey) == ERROR_SUCCESS) {
		
		// we need to find the freshest entry
		
			// enumerate keys
			// latest will be last in alfa order
			// TBD
		
		// if latest is fresh enuf ??
		
		
			// if it has a valid javahome entry  
			
				funcResult = 1 ;	
				
			// else
				// funcResult = 0 ;
				
		// else latest is NOT fresh enuf
		
			// funcResult = 0 ;
		
		
		// close the JRE info key
		closeResult = RegCloseKey (jreInfoKey) ;



//		// okay, we've got the environment key
//		
//		// try to write the name/value pair to it
//		// will create n/v pair if it doesn't already exist
//		setResult = RegSetValueEx (enviroKey, envVarName, 0, REG_SZ,
//				(LPBYTE)envVarValue, strlen(envVarValue) + 1) ;
//		
//		// close the environment key
//		closeResult = RegCloseKey (enviroKey) ;
//		
//		// done
//		return ((setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS)) ;
	

		
		
		
	// else we couldn't open the JRE info
	} else {
		
		funcResult = 0;
		
		
	} // end if-else
	
	
	// done
	return funcResult ;
	
//	// if it doesn't exist, will try to create it
//	if (RegCreateKeyEx (rootKey, keyPath, 0,
//				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
//				& enviroKey, & createKeyDisposition)== ERROR_SUCCESS) {
//	
//		// okay, we've got the environment key
//		
//		// try to write the name/value pair to it
//		// will create n/v pair if it doesn't already exist
//		setResult = RegSetValueEx (enviroKey, envVarName, 0, REG_SZ,
//				(LPBYTE)envVarValue, strlen(envVarValue) + 1) ;
//		
//		// close the environment key
//		closeResult = RegCloseKey (enviroKey) ;
//		
//		// done
//		return ((setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS)) ;
	
	
}  // end function javaRegEntriesCool



//	// local vars
//	HKEY rootKey = NULL ;
//	char keyPath [MAX_REG_PATH] ;
//	
//	HKEY enviroKey = NULL;
//	DWORD createKeyDisposition = 0;
//
//	LONG setResult = 0;
//	LONG closeResult = 0;
//	
//	// are we setting this for all users, or just the current user ?
//	switch (envTarget) {
//		case USER:
//			rootKey = USER_ENVIRONMENT_ROOT_KEY ;
//			strcpy (keyPath, USER_ENVIRONMENT_KEY_PATH) ;
//			break ;
//			
//		case SYSTEM:
//		default:
//			rootKey = SYSTEM_ENVIRONMENT_ROOT_KEY ;
//			strcpy (keyPath, SYSTEM_ENVIRONMENT_KEY_PATH) ;
//			break ;
//	} // end switch target
//		
//	// try to open up the environment key
//	// if it doesn't exist, will try to create it
//	if (RegCreateKeyEx (rootKey, keyPath, 0,
//				0,REG_OPTION_NON_VOLATILE, KEY_WRITE, 0, 
//				& enviroKey, & createKeyDisposition)== ERROR_SUCCESS) {
//	
//		// okay, we've got the environment key
//		
//		// try to write the name/value pair to it
//		// will create n/v pair if it doesn't already exist
//		setResult = RegSetValueEx (enviroKey, envVarName, 0, REG_SZ,
//				(LPBYTE)envVarValue, strlen(envVarValue) + 1) ;
//		
//		// close the environment key
//		closeResult = RegCloseKey (enviroKey) ;
//		
//		// done
//		return ((setResult == ERROR_SUCCESS) & (closeResult == ERROR_SUCCESS)) ;
//		
//	// else we couldn't get at the environment key
//	} else {
//		// sigh
//		return 0 ;
//	} // end if-else
//
//} // end function setRegistryEnvVar
//


int DisplayInfoContCancel(LPSTR info)
{
	// copy the string into global holding area	
	strcpy(g_Current_Display_Message, info) ;
	
	// run the dialog box 
	// returns 1 on Continue, 0 on Cancel
	// WM_INITDIALOG handler will center it and pick up the string
	return DialogBox (GetModuleHandle(NULL), MAKEINTRESOURCE(IDD_CONT_CANCEL), 
			NULL, DisplayMyMessageDlgProc) ;

} // end function DisplayInfoContCancel


int DisplayInfoReboot(LPSTR info){
	// copy the string into global holding area	
	strcpy(g_Current_Display_Message, info) ;
	
	// run the dialog box
	// returns 1 for a Reboot Now, 0 for a Reboot Later
	// WM_INITDIALOG handler will center it and pick up the string
	return DialogBox (GetModuleHandle(NULL), MAKEINTRESOURCE(IDD_REBOOT_LATER_NOW), 
			NULL, DisplayMyMessageDlgProc) ;

} // end function DisplayInfoReboot


// returns 1 to start the app, 0 for a quiet exit
int DisplayInfoExitStart(LPSTR info){
	// copy the string into global holding area	
	strcpy(g_Current_Display_Message, info) ;
	
	// run the dialog box
	// WM_INITDIALOG handler will center it and pick up the string
	return DialogBox (GetModuleHandle(NULL), MAKEINTRESOURCE(IDD_EXIT_START), 
			NULL, DisplayMyMessageDlgProc) ;
			
} // end function DisplayInfoFinish


// returns 0 always
int DisplayInfoExit(LPSTR info){
	// copy the string into global holding area	
	strcpy(g_Current_Display_Message, info) ;
	
	// run the dialog box
	// WM_INITDIALOG handler will center it and pick up the string
	return DialogBox (GetModuleHandle(NULL), MAKEINTRESOURCE(IDD_EXIT), 
			NULL, DisplayMyMessageDlgProc) ;
			
} // end function DisplayInfoFinish


BOOL CALLBACK DisplayMyMessageDlgProc 
	(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam){
		
	// case out on the message
	switch (message) {

	case WM_INITDIALOG :
		// set the static text item
		SetDlgItemText(hDlg, IDC_STATIC_01, g_Current_Display_Message) ;
		
		// center the dialog on the screen
		centerWindowOnScreen(hDlg) ;
		
		return TRUE ;
	
	case WM_COMMAND :
		// TBD: make these real
		switch (LOWORD (wParam)){
		
		case ID_CONTINUE :
			EndDialog (hDlg, 1) ;
			return TRUE ;
			
		case IDCANCEL :
			EndDialog (hDlg, 0) ;
			return TRUE ;
			
		case ID_REBOOT_LATER :
			EndDialog (hDlg, 0) ;
			return TRUE ;
			
			
		case ID_REBOOT_NOW :
			EndDialog (hDlg, 1) ;
			return TRUE ;

		case ID_EXIT_SETUP :
			EndDialog (hDlg, 0) ;
			return TRUE ;
			
		case ID_START_APP :
			EndDialog (hDlg, 1) ;
			return TRUE ;
			
		} // end switch
		
	} // end switch 

	// if we get here, we haven't handled anything
	return FALSE ;

} // end function


int centerWindowOnScreen (HWND someWindow) {
	
	// local vars
	RECT windowRect ;
	int left, top, width, height ;
	int screenWidth, screenHeight ;
	
	// get the window's rectangle
	GetWindowRect(someWindow, &windowRect) ;
	width = windowRect.right - windowRect.left ;
	height = windowRect.bottom - windowRect.top ;
	
	// get the screen's rectangle
	// TBD make this real
	screenWidth = 1152 ;
	screenHeight = 864 ;
	
	// set window positioning info
	left = (screenWidth - width) / 2 ;
	top = (screenHeight - height) / 2 ;
	
	SetWindowPos(someWindow, NULL, left, top, 0, 0, SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE) ;
	
	return 1 ;
	
} // end function 

