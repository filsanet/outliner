@echo off

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

@echo on
REM java -cp %CP% -Duser.language=ja -Duser.region=JP -Xincgc com.organic.maynard.outliner.Outliner ja
java -cp %CP% -Xincgc com.organic.maynard.outliner.Outliner en