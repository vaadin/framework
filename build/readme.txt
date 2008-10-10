See documentation on build.xml how product is build.

How to build packages
=====================

1. See build/GWT-VERSION.properties file
- states the GWT version used within this project

2. Checkout correct GWT distribution version for your platform from http://dev.itmill.com/svn/gwt/
- note, you have to checkout GWT for all platforms when doing release builds

3. Extract or link GWT distribution under build/gwt/<platform> directory / directories
- <platform> = linux|windows|mac|mac_leopard

4. Run ant package-<platform> in build directory

5. Enjoy build/result/itmill-toolkit-<platform>-<version>.tar.gz



Complete example for building from scratch
==========================================
This is run in an empty directory on Mac OS X 10.5.3 to checkout sources from svn and
to build a complete IT Mill Toolkit package.

svn co http://dev.itmill.com/svn/trunk/
svn co http://dev.itmill.com/svn/gwt/
cd gwt
tar xfz gwt-leopard-1.4.62.tar.gz
cd ../trunk/build
mkdir gwt
cd gwt
ln -sf ../../../gwt/gwt-mac-1.4.62 mac_leopard
cd ..
ant package-mac_leopard