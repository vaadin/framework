#!/bin/bash

curl -s https://raw.githubusercontent.com/FortAwesome/Font-Awesome/v4.5.0/scss/_variables.scss|grep fa-var-|sed "s/.*fa-var-//"|sed "s/-/_/g"|tr a-z A-Z|sed 's/: "\\/(0X/'|sed 's#";#), //#'|sed "s/^\\([0-9]\\)/_\\1/"
echo ";"