See documentation on build.xml how product is build.


1. See build/GWT-VERSION.properties file
- states the GWT version used within this project

2. Checkout correct GWT distribution version for your platform
- note, you have to checkout GWT for all platforms when doing release builds

3. Extract or link GWT distribution under build/gwt/<platform> directory / directories
- <platform> = linux|windows|mac

4. Update Eclipse project classpath build/gwt/<platform>/gwt-user.jar and gwt-dev-<platform>.jar

Note: All GWT distributions have been moved to 
http://dev.itmill.com/svn/gwt/

