// windows_setup.h
//


int set_JOE_HOME () ;
int getShortPathCurDir (char *) ;
int setEnvVar (char *, char *);
int setEnvVarWin9x (char *, char *);
int setAutoExecEnvVar (char *, char *);
int getAutoExecPath(char *);
int strToUpper (char *) ;
int getWord (int, char *, char *) ;
int trimFileOffPath (char *) ;

#define LINE_MAX 1024
#define JOE_HOME "JOE_HOME"

