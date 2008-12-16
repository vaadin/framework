==============================================================================
IT Mill Toolkit <version></version> Readme
==============================================================================

-------------
How to Start?
-------------

Start Terminal.app and run the start.sh script in the installation directory. It will
start a web application at port 8888 on the local host and opens a web
browser window to display the start page of IT Mill Toolkit Content
Browser.

The installation directory is a web application as itself and is directly importable to
Eclipse IDE by selecting "File / Import / Existing Projects into Workspace" from Eclipse
main menu. See the manual for detailed instructions.

------------------------------------------
What's Inside the IT Mill Toolkit Package?
------------------------------------------

You should start exploring IT Mill Toolkit through the provided Content Browser web
application within this package; see 'How to Start?' above.

The WebContent directory contains the content available through the Content Browser: the
IT Mill Toolkit Library, demos, documentation, and other useful information to get started
with IT Mill Toolkit.

Below is a list of most important locations and files:

Start IT Mill Toolkit
- start exploring IT Mill Toolkit by double-clicking this icon

COPYING
- license file

WebContent/itmill-toolkit-<version></version>.jar
- IT Mill Toolkit Library containing Java source and compiled files

WebContent/doc/manual.pdf
- IT Mill Toolkit Reference Manual in PDF format

WebContent/doc/manual/index.html
- IT Mill Toolkit Reference Manual in HTML format

WebContent/doc/api/index.html
- IT Mill Toolkit API Documentation as JavaDoc reference

WebContent/WEB-INF/src
- Content Browser source code, compiled into WebContent/WEB-INF/classes
 
WebContent/doc/example-source
- example source code in HTML format

WebContent/demo
- files required by the demos

IT Mill Toolkit Hosted Mode.launch
IT Mill Toolkit Web Mode.launch
- launch configurations for Eclipse workspace

WebContent/doc/example-source/build-widgetset.xml
- example on how to build GWT widget sets for IT Mill Toolkit application

WebContent/ITMILL
- widget sets and themes

gwt
- Google Web Toolkit is required for building new widget sets
