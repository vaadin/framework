==============================================================================
Vaadin <version></version> Readme
==============================================================================

-------------
How to Start?
-------------

Start Terminal.app and run the start.sh script in the installation directory. It will
start a web application at port 8888 on the local host and opens a web
browser window to display the start page of Vaadin Content Browser.

The installation directory is a web application as itself and is directly importable to
Eclipse IDE by selecting "File / Import / Existing Projects into Workspace" from Eclipse
main menu. See the manual for detailed instructions.

---------------------------------
What's Inside the Vaadin Package?
---------------------------------

You should start exploring Vaadin through the provided Content Browser web
application within this package; see 'How to Start?' above.

The WebContent directory contains the content available through the Content Browser: the
Vaadin Library, demos, documentation, and other useful information to get started
with Vaadin.

Below is a list of most important locations and files:

Start Vaadin
- start exploring Vaadin by double-clicking this icon

COPYING
- license file

WebContent/vaadin-<version></version>.jar
- Vaadin Library containing Java source and compiled files

WebContent/doc/manual.pdf
- Vaadin Reference Manual in PDF format

WebContent/doc/manual/index.html
- Vaadin Reference Manual in HTML format

WebContent/doc/api/index.html
- Vaadin API Documentation as JavaDoc reference

WebContent/WEB-INF/src
- Content Browser source code, compiled into WebContent/WEB-INF/classes
 
WebContent/doc/example-source
- example source code in HTML format

WebContent/demo
- files required by the demos

Vaadin Hosted Mode Browser.launch
Vaadin Development Server.launch
- launch configurations for Eclipse workspace

WebContent/doc/example-source/build-widgetset.xml
- example on how to build GWT widget sets for Vaadin application

WebContent/VAADIN
- widget sets and themes

gwt
- Google Web Toolkit is required for building new widget sets
