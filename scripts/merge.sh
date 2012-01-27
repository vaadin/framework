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

svn up

msg=`svn log http://dev.vaadin.com/svn/versions/$FROM -r $REVISION --xml|grep "<msg>"|sed "s/<msg>//"|sed "s/<\/msg>//"`
svn merge http://dev.vaadin.com/svn/versions/$FROM . -c $REVISION
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

