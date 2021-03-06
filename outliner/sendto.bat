@echo off

REM The path below needs to be changed to the directory
REM you installed outliner into.
d:
cd \sourceforge\outliner\

set PATH=%PATH%;.\lib\

set root_path=.\lib\
set module_root_path=.\modules\

set CP=%root_path%com.organic.maynard.jar
set CP=%CP%;%root_path%outliner.jar
set CP=%CP%;%root_path%com.yearahead.io.webfile.jar
set CP=%CP%;%root_path%com.psm.wiki.jar
set CP=%CP%;%root_path%xp.jar
set CP=%CP%;%root_path%xmlrpc.jar
set CP=%CP%;%root_path%bsh.jar
set CP=%CP%;%root_path%jakarta-oro.jar
set CP=%CP%;%root_path%jazzy-core.jar

REM Classpath for XML Module
set CP=%CP%;%module_root_path%xml\lib\outliner.module.xml.jar
set CP=%CP%;%module_root_path%xml\lib\xerces.jar
set CP=%CP%;%module_root_path%xml\lib\xalan.jar
set CP=%CP%;%module_root_path%xml\lib\fop.jar
set CP=%CP%;%module_root_path%xml\lib\jep210.jar

REM java -cp %CP% -Duser.language=ja -Duser.region=JP -Xincgc com.organic.maynard.outliner.Outliner "%1"
REM java -cp %CP% -Duser.language=de -Duser.region=DE -Xincgc com.organic.maynard.outliner.Outliner "%1"
REM java -cp %CP% -Duser.language=es -Duser.region=ES -Xincgc com.organic.maynard.outliner.Outliner "%1"
java -cp %CP% -Xincgc com.organic.maynard.outliner.Outliner "%1"

@echo on