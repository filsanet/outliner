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

// constants

// international versions can be created by translating
// the string constants in this section

#define MAX_LINE 1024

#define EXE_NAME  "JOE.pif"

#define APP_NAME_STRING  "JOE"
#define APP_VERSION_STRING  "1.8.8"
#define WELCOME_STRING  "Windows Setup Program"

#define APP_HOME_REM  "\n\nrem Java Outline Editor [JOE] home directory\n"
#define APP_HOME  "JOE_HOME"

#define OS_FEEDBACK_STRING_0  "Your computer is running the "
#define OS_FEEDBACK_STRING_1  " operating system."

#define SEV_FEEDBACK_STRING_0  "Successfully "
#define SEV_FEEDBACK_STRING_1  "Failed to "
#define SEV_FEEDBACK_STRING_2  "set the environment variable "
#define SEV_FEEDBACK_STRING_3  "to the value "

#define SUCCESS_FEEDBACK_0  "JOE installed successfully on your system."
#define SUCCESS_FEEDBACK_1  "Press the Enter key to finish: "
#define REBOOT_SUGGESTION  "You'll need to reboot your system before running JOE."

#define FAILURE_FEEDBACK_0  "INSTALLATION FAILED."
#define FAILURE_FEEDBACK_1  "Press Enter key to finish: "

#define NO_JAVA_0  "There's no Java 2 Runtime Environment"
#define NO_JAVA_1  "for this OS. JOE cannot run on this system."

const char * WINDOWS_VERSION_STRINGS [] = {
	"<cannot determine Windows version>",
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

// machine requirements
#define BARE_MIN_MEMORY  32 ;  // barest minimum RAM required to run app
#define RECMND_MIN_MEMORY  64 ;  // recommended minimum RAM required to run app

#define RECMND_MIN_SPEED 400 ;  // recommended minimum processor speed

// registry
#define SYSTEM_ENVIRONMENT_ROOT_KEY HKEY_LOCAL_MACHINE
#define SYSTEM_ENVIRONMENT_KEY_PATH "System\\CurrentControlSet\\Control\\Session Manager\\Environment"  

#define USER_ENVIRONMENT_ROOT_KEY HKEY_CURRENT_USER
#define USER_ENVIRONMENT_KEY_PATH "Environment"

#define MAX_REG_PATH 255


// ---------- datatypes

// windows version info
enum windows_version {	// these are the major functionally-different versions
	CANNOT_DETERMINE = 0,
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
	WIN_UNKNOWN_V8,
	WIN_UNKNOWN_V9,
	WIN_UNKNOWN_V10,
	WIN_VERY_UNKNOWN 
	} ; // end enum

enum environment_target {
	USER,
	SYSTEM
	} ; // end enum

typedef struct _shortcut_placement {
	int programMenu;
	int startMenu;
	int desktop;
	int quickLaunch;
	int contextMenu;
	} shortcut_placement ; // end struct

// ---------- global variables

windows_version g_Windows_Version = CANNOT_DETERMINE;

int g_NT_4_SP_Num = 0 ;	// NT 4 Service Pack #


// ---------- functions

int ensureSuitableEnvironment() ;
int getAutoExecPath(char *);
int getShortPathCurDir(char *) ;
int getWord(int, char *, char *) ;
int java2available(windows_version) ;
int machineHasNuffOomph() ;
int osFeedback(windows_version) ;
int rebootRequired(windows_version) ;
int setAutoExecEnvVar(char *, char *, char *);
int setAllEnvVars() ;
int setEnvVar(char *, char *, char *, windows_version);
int setRegistryEnvVar(char *, char *, environment_target);
int set_APP_HOME() ;
int sevFeedback(int, char *, char *) ;
int strToUpper(char *) ;
int trimFileOffPath(char *) ;
int weCanRunOnThisSystem() ;
int weHaveJ2RE() ;
int wePlugIntoSystem() ;
void failureFeedback() ;
void successFeedback() ;
void welcome() ;
windows_version determineWindowsVersion() ;
int placeShortcuts() ;
int getUserChoicesReShortcutPlacement (shortcut_placement *) ;
int shortcutToProgramMenu() ;
int shortcutToStartMenu() ;
int shortcutToDesktop() ;
int shortcutToQuickLaunch() ;
int shortcutToContextMenu() ;
int hookupDocTypes() ;
