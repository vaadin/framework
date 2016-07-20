#!/usr/bin/env bash

# Disable all Travis default repositories 
sed -i "s/activeByDefault>true</activeByDefault>false</g"  ~/.m2/settings.xml
