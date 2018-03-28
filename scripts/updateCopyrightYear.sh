#!/bin/bash

. `dirname $0`/sed.sh

rootdir=`dirname $0`/..
year=$(date +%Y)
old=$(grep Copyright checkstyle/header|sed "s/^ . //")
new=$(echo $old|sed "s/2000-[^ ]* /2000-$year /")

echo "Changing '$old' to '$new'"

$SED -i "s/$old/$new/" checkstyle/header

for javaFile in `find $rootdir -name "*.java"`
do
	$SED -i "s/$old/$new/" $javaFile	
done
