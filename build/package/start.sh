#!/bin/sh

if [ "$1" != "" ] ; then
  cd $1
fi

java -cp WebContent/demo/lib/jetty/jetty-6.1.5.jar:WebContent/demo/lib/jetty/jetty-util-6.1.5.jar:WebContent/demo/lib/jetty/servlet-api-2.5-6.1.5.jar:WebContent/WEB-INF/classes com.itmill.toolkit.launcher.ITMillToolkitDesktopMode
