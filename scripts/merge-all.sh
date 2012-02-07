#!/bin/bash
FROM=$1
AUTOMERGE=$2
if [ "$FROM" = "" ]
then
	echo "Usage: $0 <from version> [automerge]"
	exit 3
fi
if [ "$AUTOMERGE" = "automerge" ]
then
	AUTOCOMMIT="autocommit"
fi

svn up
localchanges=`svn stat|wc -l`
if [ "$localchanges" != "0" ] && [ "$IGNOREDIRTY" != "ignoredirty" ]
then
	echo "You must have a clean working space copy"
	exit 4
fi

currentrepowithoutversion=`svn info|grep URL|sed "s/URL: //"|sed "s/\/[^\/]*$//"`
sourceurl="$currentrepowithoutversion/$FROM"
unmerged=`svn mergeinfo --show-revs eligible $sourceurl|sed "s/r//g"`

if [ "$unmerged" = "" ]
then
	echo "No changes to merge"
	exit 0
fi
echo "Unmerged changes"
echo "================"
for revision in $unmerged
do
	echo -n "[$revision] "
	svn log $sourceurl -r $revision --xml|grep "<msg>"|sed "s/<msg>//"|sed "s/<\/msg>//"
done

cmd=""
for revision in $unmerged
do
	thiscmd=`dirname $0`"/merge.sh $FROM $revision $AUTOCOMMIT $IGNOREDIRTY"
	cmd="$cmd $thiscmd && "
	if [ "$AUTOMERGE" = "automerge" ]
	then
		echo "Merging [$revision]..."
		$thiscmd
		if [ "$?" != "0" ]
		then
			echo "Merge of [$revision] failed, aborting..."
			exit 1
		fi
	fi
done
cmd="$cmd true"
if [ "$AUTOMERGE" != "automerge" ]
then
	echo
	echo "Merge command:"
	echo 
	echo $cmd
fi

exit 0
