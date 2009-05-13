See documentation on build.xml how product is build.

How to build packages
=====================

1. See build/GWT-VERSION.properties file
- states the GWT version used within this project

2. Checkout correct GWT distribution version for your platform from http://dev.itmill.com/svn/gwt/
- note, you have to checkout GWT for all platforms when doing release builds

3. Extract or link GWT distribution under build/gwt/<platform> directory / directories
- <platform> = linux|windows|mac

4. Run ant package-<platform> in build directory

5. Enjoy build/result/vaadin-<platform>-<version>.tar.gz



Complete example for building from scratch
==========================================
This is run in an empty directory on Ubuntu 8.10 to checkout sources from svn and
to build a complete IT Mill Toolkit package.

svn co http://dev.itmill.com/svn/trunk/
svn co http://dev.itmill.com/svn/gwt/
cd gwt
tar xfj gwt-linux-1.5.3.tar.bz2
cd ../trunk/build
mkdir gwt
cd gwt
ln -sf ../../../gwt/gwt-linux-1.5.3 linux
cd ..
ant package-linux