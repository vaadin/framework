#!/bin/bash

version=$1

if [ "$version" = "" ]
then
	echo "Usage: $0 <version to set>"
	exit 1
fi

scriptdir=`dirname $0`
basedir=$scriptdir"/.."

sincefiles=`find $basedir -name "*.java"|xargs egrep -Hi "(@since$|@since $)"|grep -v "./work/"|grep -v "./uitest/"|grep -v "/tests/"|cut -d: -f 1|sort|uniq`

# Stupid feature detection using an invalid parameter.
# Mac requires a parameter for the -i option (creates a backup file with that suffix)
# Linux does not support any parameters for -i
mac=`sed --foobaryeano 2>&1|grep -- "-i extension"`
if [ "$mac" = "" ]
then
	sedCmd="sed -i"
else
	sedCmd="sed -i backup"
fi
for f in $sincefiles
do
	echo "Fixing $f..."
	$sedCmd "s/@since\$/@since $version/g" $f
	$sedCmd "s/@since \$/@since $version/g" $f
done
