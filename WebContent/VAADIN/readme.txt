======================
Themes and Widget Sets
======================

The WebContent/ITMILL directory contains Themes and Widgetsets.

------
Themes
------

Themes may be edited freely. They contain static images, CSS,
and layouts for Toolkit applications.

---------------------------
Building Custom Widget Sets
---------------------------
The 'widgetsets' directory is generated automatically, so we suggest that you
do not edit it. You can rebuild the widgetsets directory by running the
command below in the top project directory:

  ant -f build-widgetsets.xml

See build-widgetsets.xml, com.vaadin.demo.colorpicker package, and the
Reference Manual for more information regarding widget sets.

---------------------------
IT Mill Toolkit Development
---------------------------
When developing the IT Mill Toolkit Library itself, change to "build" directory and
run "ant widgetsets" to compile all widgetsets or "ant widgetset-default",
"ant-widgetset-reserver", or "ant widgetset-colorpicker" to compile individual
widgetsets. You must have GWT installed under build/gwt, under the proper
platform directory.

See http://dev.itmill.com/wiki/DevDocs/StartingDevelopment for instructions for
installing GWT and compiling widgetsets for Toolkit development.
