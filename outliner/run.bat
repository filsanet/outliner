@echo off

set root_path=.\lib\

set CP=%root_path%com.organic.maynard.jar
set CP=%CP%;%root_path%outliner.jar
set CP=%CP%;%root_path%sax.jar
set CP=%CP%;%root_path%xp.jar
set CP=%CP%;%root_path%xmlrpc.jar

@echo on
java -cp %CP% -Xincgc com.organic.maynard.outliner.Outliner