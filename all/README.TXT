This Vaadin zip contains all the jars of the Vaadin Framework. Additionally all dependencies are provided in the lib folder.

To use in a web project:
1. Copy all vaadin-* files except vaadin-client and vaadin-client-compiler to WEB-INF/lib in your project
2. Copy lib/*.jar to WEB-INF/lib in your project
3. Copy vaadin-client and vaadin-client-compiler to a lib folder which is on your classpath but will not be deployed. These files are only needed when compiling a module (widget set) to Javascript.

If you are using Eclipse and a standard WTP (Dynamic Web Project or Vaadin Project), step 3 can be:
Create a /lib folder, copy the files there and right click the lib folder and select "Build Path" -> "Add to Build Path".
Once this is done, the "Compile widgetset" button provided by the Vaadin Plug-in for Eclipse will work correctly.



