#!/bin/sh 

ulimit -s 2048 

INSTALL_DIR=/home/simon/.bin/outliner 

CWD=`pwd` 

# Make the filename absolute if it isn't already, and then normalize
# it, such that a//b, a/./b and a/foo/../b all become a/b.
if [ $# != 0 ]; then
FILENAME=`(echo "$1" | grep "^/" || echo "$CWD/$1") | \
    sed -e "s:/\{1,\}:/:g" \
        -e "s:/\./:/:g" \
        -e "s:/[^/]*/\.\./:/:g"`
fi

cd $INSTALL_DIR 

RP=lib 

CP=$RP/com.organic.maynard.jar 
CP=$CP:$RP/outliner.jar 
CP=$CP:$RP/com.yearahead.io.webfile.jar 
CP=$CP:$RP/com.psm.wiki.jar 
CP=$CP:$RP/sax.jar 
CP=$CP:$RP/xp.jar 
CP=$CP:$RP/xmlrpc.jar 
CP=$CP:$RP/bsh.jar 
CP=$CP:$RP/jakarta-oro.jar  
CP=$CP:$RP/jazzy-core.jar 

java -cp $CP -Xincgc com.organic.maynard.outliner.Outliner $FILENAME 

cd $CWD