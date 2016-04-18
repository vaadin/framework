This Vaadin zip contains all the jars of the Vaadin Framework. Additionally all dependencies are provided in the lib folder.

To use in a web project:
1. Copy following files from the archive root into WEB-INF/lib in your project:
   - vaadin-server-*.jar
   - vaadin-shared-*.jar
   - vaadin-themes-*.jar
   - vaadin-push-*.jar (if you need push)

2. Copy following files from the archive /lib into WEB-INF/lib in your project:
   - vaadin-slf4j-jdk14-*.jar
   - flute-*.jar
   - guava-*.jar
   - js-*.jar
   - jsoup-*.jar
   - sac-*.jar
   - streamhtmlparser-jsilver-*.jar
   - atmosphere-runtime-*.jar  (if you need push)

3. Copy vaadin-client and vaadin-client-compiler to a lib folder which is on your classpath but will not be deployed.
These files are only needed when compiling a module (widget set) to Javascript.

4. Read https://vaadin.com/docs/-/part/framework/application/application-overview.html#application.overview to start your development


