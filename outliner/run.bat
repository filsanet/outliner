echo off
set root_path=.\lib\

set CLASSPATH=%classpath%;%root_path%com.organic.maynard.jar
set CLASSPATH=%classpath%;%root_path%outliner.jar
set CLASSPATH=%classpath%;%root_path%sax.jar
set CLASSPATH=%classpath%;%root_path%xp.jar
set CLASSPATH=%classpath%;%root_path%xmlrpc.jar

echo on
java -Xincgc com.organic.maynard.outliner.Outliner
