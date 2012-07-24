#!/bin/bash
SINCE=$1
UNTIL=$2

if [ "$SINCE" = "" ] || [ "$UNTIL" = "" ]
then
	echo "Usage: $0 <since> <until>"
	exit 3
fi

command="git --no-pager log --no-color $SINCE..$UNTIL"
# TODO Why do I get whitespace in the beginning of the wc output?
change_count=`$command --oneline|wc -l|tr -d ' '`

if [ "$change_count" = "0" ]
then
	echo "No unmerged commits"
	exit 0
fi

echo "There are $change_count commits that have not been merged from $UNTIL to $SINCE: "
echo ""
$command
exit 1