#!/bin/bash

# Find eclipse binary
if [ "$ECLIPSE" = "" ]
then
	ECLIPSE=`which eclipse`
fi

if [ "$ECLIPSE" = "" ]
then
	echo "Could not find 'eclipse' in PATH"
	echo "Either add it to the PATH or set the ECLIPSE variable to point to the Eclipse binary"
	echo "e.g. ECLIPSE=\"/some/where/eclipse\" $0"
	exit 1
fi

# Resolve project root directory
basedir=`dirname $0`/..
pushd "$basedir" > /dev/null
basedir=`pwd`
popd > /dev/null

SRC=`ls -d $basedir/*/src $basedir/*/tests/src`

# Use project formatting settings
config="$basedir/.settings/org.eclipse.jdt.core.prefs"

"$ECLIPSE" -nosplash -application org.eclipse.jdt.core.JavaCodeFormatter -config "$config" $SRC
