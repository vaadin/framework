How to build Vaadin Framework
=============================

Building the distribution packages is as easy as running ant without 
parameters in this directory. While as official packages will be built
with Java 1.5, you can force build with 1.6 by adding the option 
-Dignoreversion=1

Some of the most common targets to build:
- Distribution ZIP-file will be built with target package-zip
- Distribution JAR-file will be built with target package-jar
- Demo package will be built with target package-war

For more detailed info, see build.xml
