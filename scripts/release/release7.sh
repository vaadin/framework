#!/bin/bash

STAGING=/home/vaadin/staging
VERSION=$1

if [ "$VERSION" = "" ]
then
	echo "Usage: $0 <version>"
	exit
fi

