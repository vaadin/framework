#!/bin/bash

# Must use gsed on Mac
if [[ "$OSTYPE" == "darwin"* ]]
then
        export SED=`which gsed`
else
        export SED=`which sed`
fi

if [ ! -x "$SED" ]
then
        echo "Sed not found, install gsed on Mac or sed on Linux"
        exit 1
fi
