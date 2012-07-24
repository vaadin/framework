#!/bin/bash
SINCE=$1
UNTIL=$2

if [ "$SINCE" = "" ] || [ "$UNTIL" = "" ]
then
	echo "Usage: $0 <since> <until>"
	exit 3
fi

testname="merge check for `pwd|sed "s/.*\///"`"
echo "##teamcity[testStarted name='$testname' captureStandardOutput='<true/false>']"

command="git --no-pager log --no-color $SINCE..$UNTIL"
# TODO Why do I get whitespace in the beginning of the wc output?
change_count=`$command --oneline|wc -l|tr -d ' '`

if [ "$change_count" = "0" ]
then
	echo "No unmerged commits"
else 
	command="$command --format=short"
	message="There are $change_count commits that have not been merged from $UNTIL to $SINCE"
	echo $message
	echo ""
	$command
	details=`$command|perl -p -e 's/\n/|n/' | sed "s/['\|\[\]]/|\&/g"`
	echo "##teamcity[testFailed name='$testname' message='$message' details='|n$details']"
fi

echo "##teamcity[testFinished name='$testname']"
