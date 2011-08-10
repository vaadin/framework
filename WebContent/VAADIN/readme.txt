======================
Themes and Widget Sets
======================

The WebContent/VAADIN directory contains Themes and Widgetsets.

------
Themes
------

Themes may be edited freely. They contain static images, CSS,
and layouts for Vaadin applications.

---------------------------
Building Custom Widget Sets
---------------------------
The 'widgetsets' directory is generated automatically, so we suggest that you
do not edit it. You can rebuild the widgetsets directory by running the
command below in the top project directory:

  ant -f build-widgetsets.xml

See build-widgetsets.xml and the Reference Manual for more information
regarding widget sets.

---------------------------
Vaadin Development
---------------------------
When developing the Vaadin Library itself, change to "build" directory and
run "ant widgetsets" to compile all widgetsets or "ant widgetset-default",
or "ant widgetset-colorpicker" etc. to compile individual widgetsets. You
must have GWT installed under build/gwt.

See http://dev.vaadin.com/wiki/DevDocs/StartingDevelopment for instructions for
installing GWT and compiling widgetsets for Vaadin development.
