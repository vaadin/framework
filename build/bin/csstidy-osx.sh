#!/bin/sh
bin/csstidy-osx $1 $1 1>>$1.log 2>>$1.log
grep -i invalid $1.log
grep -i error $1.log
grep -i warning $1.log
rm $1.log
exit 0
