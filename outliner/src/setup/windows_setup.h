/* windows_setup.h
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

// function decls
int determineWindowsVersion () ;
int getAutoExecPath(char *);
int getShortPathCurDir (char *) ;
int getWord (int, char *, char *) ;
int setAutoExecEnvVar (char *, char *, char *);
int setEnvVar (char *, char *, char *);
int setRegistryEnvVar (char *, char *);
int set_JOE_HOME () ;
int strToUpper (char *) ;
int trimFileOffPath (char *) ;
int osFeedback () ;
int sevFeedback (int, char *, char *) ;
int userReport (int) ;

// constants
#define MAX_LINE 1024
#define JOE_HOME "JOE_HOME"

// windows version info
enum windows_version {	// these are the major functionally-different versions
	WIN_95,
	WIN_95_OSR2,
	WIN_98,
	WIN_98_SE,
	WIN_ME,
	WIN_XP,
	WIN_NT_351,
	WIN_NT_4,
	WIN_2K,
	WIN_DOT_NET_SERVER,
	WIN_UNKNOWN_V3,
	WIN_UNKNOWN_V4,
	WIN_UNKNOWN_V5,
	WIN_UNKNOWN_V6,
	WIN_UNKNOWN_V7,
	WIN_VERY_UNKNOWN } gWindowsVersion = WIN_VERY_UNKNOWN;

int g_NT_4_SP_Num = 0 ;	// NT 4 Service Pack #

char * windows_version_strings [] = {
	"Windows 95",
	"Windows 95 OSR2",
	"Windows 98",
	"Windows 98 SE",
	"Windows ME",
	"Windows XP",
	"Windows NT 3.51",
	"Windows NT 4",
	"Windows 2000",
	"Windows .Net Server",
	"Unknown V3 Windows",
	"Unknown V4 Windows",
	"Unknown V5 Windows",
	"Unknown V6 Windows",
	"Unknown V7 Windows",
	"Very Unknown Windows" }; 
		

