@echo off

REM The path below needs to be changed to the directory
REM you installed outliner into.
REM c:\Progra~1\Metrowerks\projects\outliner\
cd c:\cvsroot\outliner\

set PATH=%PATH%;.\lib\

set root_path=.\lib\

set CP=%root_path%com.organic.maynard.jar
set CP=%CP%;%root_path%outliner.jar
set CP=%CP%;%root_path%com.yearahead.io.webfile.jar
set CP=%CP%;%root_path%sax.jar
set CP=%CP%;%root_path%xp.jar
set CP=%CP%;%root_path%helma.xmlrpc.jar
set CP=%CP%;%root_path%bsh-1_2b1.jar
set CP=%CP%;%root_path%jakarta-oro-2.0.4.jar
set CP=%CP%;%root_path%jmousewheel.jar

REM java -cp %CP% -Duser.language=ja -Duser.region=JP -Xincgc com.organic.maynard.outliner.Outliner ja %1
REM java -cp %CP% -Duser.language=de -Duser.region=DE -Xincgc com.organic.maynard.outliner.Outliner de %1
REM java -cp %CP% -Duser.language=es -Duser.region=ES -Xincgc com.organic.maynard.outliner.Outliner es %1
java -cp %CP% -Xincgc com.organic.maynard.outliner.Outliner en %1

@echo on
