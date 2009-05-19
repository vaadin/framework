#!/bin/sh

if [ "$1" != "" ] ; then
  cd $1
fi

java -cp WebContent/demo/lib/jetty/jetty-6.1.7.jar:WebContent/demo/lib/jetty/jetty-util-6.1.7.jar:WebContent/demo/lib/jetty/servlet-api-2.5-6.1.7.jar:WebContent/WEB-INF/classes:WebContent/WEB-INF/src com.vaadin.launcher.DemoLauncher $VAADIN_PARAMETERS
