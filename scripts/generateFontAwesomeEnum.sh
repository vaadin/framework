#!/bin/bash

curl -s https://raw.githubusercontent.com/FortAwesome/Font-Awesome/v4.4.0/scss/_variables.scss|grep fa-var-|sed "s/.*fa-var-//"|sed "s/-/_/g"|tr a-z A-Z|sed 's/: "\\/(0X/'|sed 's#";#), //#'
echo ";"