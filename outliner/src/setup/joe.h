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
 *notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *copyright notice, this list of conditions and the following 
 *disclaimer in the documentation and/or other materials provided 
 *with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *contributors may be used to endorse or promote products derived 
 *from this software without specific prior written permission. 
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


#define ROOT_PATH  ".\\LIB\\"

#define MAX_LINE 1024

#define APP_HOME  "JOE_HOME"

#define QUOTE_CHAR '\"'
#define BACKSLASH_CHAR '\\'




// constants

// international versions can be created by translating
// the string constants in this section


#define EXE_NAME  "JOE.pif"
#define EXE_IS_PIF  1
#define EXE_IS_EXE  0

#define APP_NAME_STRING  "JOE"
#define APP_VERSION_STRING  "1.8.8"
#define WELCOME_STRING  "Windows Setup Program"

#define APP_HOME_REM  "\n\nrem Java Outline Editor [JOE] home directory\n"

#define START_MENU_SUBPATH  "\\Start Menu\\"
#define PROGRAMS_MENU_SUBPATH  "\\Start Menu\\Programs\\"
#define DESKTOP_MENU_SUBPATH  "\\Desktop\\"

#define SYSTEM_DIR_NT  "\\system32"
#define SYSTEM_DIR_9X  "\\system"

// Java Runtime Environment
#define LAUNCHER_0  "java.exe"
#define LAUNCHER_1  "javaw.exe"
#define MINIMUM_JRE  "1.3"
#define JRE_ENCLOSURE_VERSION  "1.3.1_02"

#define NO_JAVA_0  "There's no Java 2 Runtime Environment"
#define NO_JAVA_1  "for this OS. JOE cannot run on this system."

#define JRE_INFO_0  "Java Runtime Environment [JRE] version "
#define JRE_INFO_1   " is installed on your computer system."
#define JRE_UPGRADE_0  " will not run correctly under this JRE."
#define JRE_UPGRADE_1  "Would you like to install a newer JRE (version "



// operating system info
#define OS_FEEDBACK_STRING_0  "Your computer is running the "
#define OS_FEEDBACK_STRING_1  " operating system."

// environment variables
#define SEV_FEEDBACK_STRING_0  "Set "
#define SEV_FEEDBACK_STRING_1  "Unable to set "
#define SEV_FEEDBACK_STRING_2  "the environment variable "
#define SEV_FEEDBACK_STRING_3  "to the value "

// overall results
#define SUCCESS_FEEDBACK_0  "JOE installed successfully on your system."
#define SUCCESS_FEEDBACK_1  "Press the Enter key to finish: "
#define REBOOT_SUGGESTION  "You'll need to reboot your system before running JOE."

#define FAILURE_FEEDBACK_0  "INSTALLATION FAILED."
#define FAILURE_FEEDBACK_1  "Press Enter key to finish: "

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
	"Very Unknown Windows" }; // end string array

// machine requirements
#define BARE_MIN_MEMORY  32 ;  // barest minimum RAM required to run app
#define RECMND_MIN_MEMORY  64 ;  // recommended minimum RAM required to run app

#define RECMND_MIN_SPEED 400 ;  // recommended minimum processor speed

// important files
#define AUTOEXEC_BAT  "autoexec.bat"
#define CONFIG_SYS  "config.sys"
#define MS_DOS_SYS  "msdos.sys"
#define STD_AUTOEXEC_PATH  "c:\\autoexec.bat"
#define STD_MS_DOS_SYS_PATH  "c:\\msdos.sys"

// msdos.sys
#define HOST_WIN_BOOT_DRV  "HostWinBootDrv"
#define UNINSTALL_DIR  "UninstallDir"
#define WIN_DIR  "WinDir"
#define WIN_BOOT_DIR  "WinBootDir"
const char * MS_DOS_SYS_SECTION_STRINGS [] = {
	"[PATHS]",
	"[OPTIONS]"
	} ; // end string array

// registry
#define SYSTEM_ENVIRONMENT_ROOT_KEY  HKEY_LOCAL_MACHINE
#define SYSTEM_ENVIRONMENT_KEY_PATH "System\\CurrentControlSet\\Control\\Session Manager\\Environment"  

#define USER_ENVIRONMENT_ROOT_KEY HKEY_CURRENT_USER
#define USER_ENVIRONMENT_KEY_PATH "Environment"

#define JAVA_ROOT_KEY  HKEY_LOCAL_MACHINE
#define JRE_HOME_PATH  "Software\\JavaSoft\\Java Runtime Environment"

#define MAX_REG_PATH 255

#define DOC_TYPES_ROOT_KEY  HKEY_CLASSES_ROOT

#define DOC_TYPES_OPML_PATH  ".opml"
#define DOC_TYPES_JOE_OPML_PATH  "JOE.OPML.document"
#define DOC_TYPES_JOE_OPML_OPEN_CMD_PATH  "\\Shell\\Open\\Command"

#define DOC_TYPE_HOOKED "Hooked up "
#define DOC_TYPE_NOT_HOOKED "Unable to hook up "
#define DOC_TYPE_HOOKER_0 " documents to "

#define SHORTCUT_ADDED  "Added a "
#define SHORTCUT_NOT_ADDED  "Unable to add "
#define SHORTCUT_TO_PROG_MENU  " shortcut to the Programs menu."
#define SHORTCUT_TO_START_MENU  " shortcut to the top of the Start menu."
#define SHORTCUT_TO_DESKTOP  " shortcut to the Desktop."
#define SHORTCUT_TO_QUICK_LAUNCH  " shortcut to the Quick Launch toolbar."


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

enum ms_dos_sys_section {
	PATHS,
	OPTIONS,
	OTHER
	}; // end enum

typedef struct {
	int programsMenu;
	int startMenu;
	int desktop;
	int quickLaunch;
	int contextMenu;
	} shortcut_placement ; // end struct

typedef struct {
	char type_path [6] ;  // example: ".opml"
	char app_doc_path [100] ; // example: "JOE.OPML.document"
	char app_doc_open_cmd_path [100] ; // example: "\\Shell\\Open\\Command"
	
	} doc_type_info ; // end struct


// ---------- global variables

int g_NT_4_SP_Num = 0 ;	// NT 4 Service Pack #
int g_Windows_Version = CANNOT_DETERMINE ;


// ---------- functions

int buildClassPath(char *) ;
int buildCommandLine (char *, char *, char *) ;
windows_version determineWindowsVersion() ;
int getAppHome (char *) ;
int getAutoExecEnvVar (char *, char *) ;
int getEnvVar (char *, char *) ;
int getRegistryEnvVar(char *, char *, environment_target) ;
int otherAppInstanceRunning (char *) ;
int sendDocToAppInstance(char *, char *) ;
int extractDocFromCmdLine(char *, char *) ;
int fileExists(char *) ;
int stripPathCrud(char *) ;