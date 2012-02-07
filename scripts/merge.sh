#!/bin/bash
FROM=$1
REVISION=$2
AUTOCOMMIT=$3

if [ "$FROM" = "" ] || [ "$REVISION" = "" ]
then
	echo "Usage: $0 <from version> <changeset> [autocommit]"
	exit 2
fi

localchanges=`svn stat|wc -l`
if [ "$localchanges" != "0" ]
then
	echo "You must have a clean working space copy"
	exit 2
fi

if [ "$SVN_PASS_FILE" != "" ]
then
       SVN_PASS=`cat "$SVN_PASS_FILE"`
fi
       
svn up

currentrepowithoutversion=`svn info|grep URL|sed "s/URL: //"|sed "s/\/[^\/]*$//"`
sourceurl="$currentrepowithoutversion/$FROM"

msg=`svn log $sourceurl -r $REVISION --xml|grep "<msg>"|sed "s/<msg>//"|sed "s/<\/msg>//"`
svn merge $sourceurl . -c $REVISION
if [ "$?" != "0" ]
then
	echo "Merge failed. Conflicts must be resolved manually!"
	exit 3
fi

msg="[merge from $FROM] $msg"
if [ "$AUTOCOMMIT" = "autocommit" ]
then
	echo "Trying to commit..."
	if [ "$SVN_USER" != "" ]
	then
		svn commit -m "$msg" --username $SVN_USER --password $SVN_PASS
	else
		svn commit -m "$msg"
	fi
	
	RET=$?
	if [ "$RET" != "0" ]
	then
		exit 1
	fi
	exit 0
else
	echo "Run the following command to commit..."
	echo svn commit -m \"$msg\"
	exit 1
fi

