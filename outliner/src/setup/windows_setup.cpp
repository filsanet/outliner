/*
 * windows_setup.cpp
 * 
 * installs JOE on a Win32 system
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
#include "windows_setup.h"

// TBD [srk] make this a very simple GUI app
// 	that uses a series of simple dialogs 
// 	to communicate with the user as the 
//	program goes thru main's setup steps

int main(int argc, char* argv[]){
	
	// if any of these steps fail, we leave immediately, returning 0
	// if we make it thru all the steps, we return 1
	
	// try to determine which version of Windows we're running under
	// if (! determineWindowsVersion()) return 0 ;
	
	// on some systems, user must be logged on as an adminstrator to
	// set environment variables -- check for that, and prompt as
	// necessary
	// if (! dealWithAdministratorCrap()) return 0 ;
	
	// try to make sure we've got a proper Java 2 Runtime Environment
	// if (! ensureJ2RE()) return 0 ;
	
	// try to adjust run.bat if there's more than one JRE
	// if (! adjustRunBat()) return 0 ;
	
	// set the JOE_HOME environment var
	if (! set_JOE_HOME ()) return 0 ;
	
	// per user choice, set up JOE as the handler of OPML files
	// if (! setJoeAsOpmlHandler()) return 0 ;
	
	// per user choice, copy JOE.pif to
	// Programs menu, Start menu top, desktop, 
	// quickstart toolbar of taskbar, folders on desktop
	// if (! copyJoePifPerUserPrefs()) return 0 ;
	
	// suggest a reboot for Windows 9x systems
	// if (! suggestWindowsReboot()) return 0 ;
	
	return 1 ;

} // end main


int set_JOE_HOME () {
	// local vars
	char shortPathBuffer[MAX_PATH] ;
	char introLines[] = "\n\nrem Java Outline Editor [JOE] home directory\n";
	
	// if we can't obtain short pathname for current directory
	if (! getShortPathCurDir (shortPathBuffer))
		// leave in failure
		return 0 ;

	// append a backslash
	strcat (shortPathBuffer, "\\") ;
	
	// return result of trying to set 
	// environment var JOE_HOME to that value
	return setEnvVar(JOE_HOME, shortPathBuffer, introLines);
	
} // end set_JOE_HOME


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
int setEnvVar (char * varName, char * varValue, char * introLines) {
	// local vars
	int result = 0 ;
	// switch out on windows version
	// switch (gWindowsVersion) {
		// case WINDOWS_9X:
		// default:
			// win 9x uses autoexec.bat and a reboot
			return setAutoExecEnvVar (varName, varValue, introLines) ;
			// break ;
		// case WINDOWS_NT:
		// case WINDOWS_2K:
		// case WINDOWS_ME:
		// case WINDOWS_XP:
			// win nt/2k/me/xp use the registry for env vars
			// result = setRegistryEnvVar (varName, varValue) ;
			// if (result) result = broadcast the news
			// return result 
			// break ;
		// } // end switch
		
		// for win nt, 2k, me, xp, use registry
//		
	
} // end setEnvVar


// set an environment variable via the registry
// used for Windows NT, 2K, ME, and XP
int setRegistryEnvVar (char * varName, char * varValue) {
	
/* The key key

	makes system changes (vs user changes)
	user must be logged on as an administrator
	if user's not logged on as an admin, could offer to install as user,
	rather than as system, or suggest reboot

	HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\SessionManager\Environment

	get the elements in the key
	search for a varName element
	if found, check its value
	if value's cool, cool
	if value needs changing, do so
	if not found, add name/value
	
	if everything was not perfect (we had to change or add)
		broadcast the new setting to see if we can avoid the need to reboot
*/
	return 1 ;

	
} // end function setRegistryEnvVar


// set an environment variable via autoexec.bat
// used for Windows 95, 98, 98 SE
int setAutoExecEnvVar (char * varName, char * varValue, char * introLines) {
	
	// local vars
	char autoExecPathBuffer [MAX_PATH] ;
	char lineBuffer [LINE_MAX] ;
	char lineTestBuffer [LINE_MAX] ;
	char tempFilePathBuffer [LINE_MAX] ;
	char wordBuffer [LINE_MAX] ;
	int result = 0 ;
	int position ;
	int coolAsIs = 0 ;
	int madeAChange = 0 ;
	int writeToTemp = 1 ;
	int tempIndex ;
	char newSettingBuffer [LINE_MAX] ;
	char oldAutoExecNewNameBuffer [LINE_MAX] ;
	
	// if we can't get autoexec.bat's path, leave in failure
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
	while (fgets(lineBuffer, LINE_MAX, autoExec)) {
		
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

// determine the path to the system's boot drive's autoexec.bat file
// if one doesn't exist, try to create one
// if all goes well, returns 1
// if not, returns 0	
int getAutoExecPath(char * pathBuffer) {
	
	// fake for now
	// TBD make real
	strcpy (pathBuffer, "c:\\autoexec.bat") ;
	
	return 1 ;
	
	// real 
	// determine boot volume
	// does it have an autoexec.bat ??
	// if it does, cool
	// if it doesn't, create one
	
	
} // end function getAutoExecPath


int strToUpper (char * someString) {
	
	int pointer = 0 ;
	char curChar ;
	
	while ((curChar = someString[pointer]) != 0) {
		
		someString[pointer] = toupper(curChar) ;
		
		pointer++ ;
	}
	
	return pointer ;
	
} // end function  strToUpper


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
	
} // end getFirstWord


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
