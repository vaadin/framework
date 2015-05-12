#!/bin/bash

if [ "$#" != "2" ]
then
    echo "Usage: $0 <runtime version> <js version>"
    echo "If the runtime version contains the string 'vaadin', then a vaadin atmosphere version will be assumed, otherwise an upstream atmosphere version".
    echo "If a version is set to -, the version will not be updated"
    exit 1
fi

pushd `dirname $0`/.. > /dev/null
basedir=`pwd`
popd > /dev/null

currentRuntime=`grep ENTITY "$basedir"/push/ivy.xml|grep runtime.version|cut -d\" -f 2`
currentJs=`grep ENTITY "$basedir"/push/ivy.xml|grep js.version|cut -d\" -f 2`

sed=`which sed`

uname|grep Darwin > /dev/null
if [ "$?" = "0" ]
then
	# Mac if uname output contains Darwin
	sed=`which gsed`
	if [ "$sed" = "" ]
	then
		echo "Install gnu sed (gsed) using e.g. brew install gnu-sed"
		exit 2
	fi
fi

echo "Currently using runtime $currentRuntime and JS $currentJs"

newRuntime=$1
newJs=$2

if [ "$newRuntime" != "-" ]
then
    echo "Updating runtime to $newRuntime..."
    $sed -i "s#$currentRuntime#$newRuntime#" "$basedir"/push/ivy.xml
    $sed -i "s/$currentRuntime/$newRuntime/g" "$basedir"/push/build.xml
    $sed -i "s/$currentRuntime/$newRuntime/g" "$basedir"/server/src/com/vaadin/server/Constants.java
    if [[ $newRuntime == *"vaadin"* ]]
    then
        $sed -i "s/org.atmosphere/com.vaadin.external.atmosphere/g" "$basedir"/push/ivy.xml
    else
        $sed -i "s/com.vaadin.external.atmosphere/org.atmosphere/g" "$basedir"/push/ivy.xml
    fi
fi

if [ "$newJs" != "-" ]
then
    echo "Updating JS to $newJs..."
    $sed -i "s/$currentJs/$newJs/g" "$basedir"/push/ivy.xml
fi
