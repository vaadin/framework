#!/bin/bash

display_usage() {
echo -e "\nUsage:\n$0 <version> <framework staging id> <archetypes staging id> <plugin staging id>\n"
echo -e "Example: 7.3.7 1450 1451 1452"
}

display_sed() {
echo -e "\nGNU sed was not found. If you are running Linux, ensure you have sed installed an in your PATH\n"
echo -e "If you are running Mac OS X, install gsed and ensure it is in your PATH"
}

SED=sed
# Ensure we have GNU sed
$SED --version 2>&1|grep "GNU sed" > /dev/null
if [ "$?" != "0" ]
then
	# Try gsed
	SED=gsed
	gsed --version 2>&1|grep "GNU sed" > /dev/null
	if [ "$?" != "0" ]
	then
		display_sed
		exit 2
	fi
fi

# if less than two arguments supplied, display usage
if [  $# -le 3 ]
then
  display_usage
  exit 1
fi


# check whether user had supplied -h or --help . If yes display usage
if [[ ( $# == "--help") ||  $# == "-h" ]]
then
  display_usage
  exit 0
fi

ARCHETYPE_GROUP=com.vaadin
ARCHETYPE_VERSION=$1

REPOBASE=https://oss.sonatype.org/content/repositories/
VAADINREPO=$REPOBASE"comvaadin-$2"
ARCHETYPEREPO=$REPOBASE"comvaadin-$3"
PLUGINREPO=$REPOBASE"comvaadin-$4"

ART=test-$ARCHETYPE_ARTIFACT-$ARCHETYPE_VERSION
ART=`echo $ART|sed "s/\./-/g"`
GROUP=testpkg
