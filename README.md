Vaadin
======
*[Vaadin](https://vaadin.com) is a Java framework for building modern web applications that look great, perform well and make you and your users happy.*

For instructions about _using_ Vaadin to develop applications, please refer to
https://vaadin.com/learn

To contribute, first refer to https://vaadin.com/wiki/-/wiki/Main/Contributing+Code
for general instructions and requirements for contributing code to the Vaadin framework.

Instructions on how to set up a working environment for developing the Vaadin
framework follow below.

Cloning the project repositories
======

Vaadin 7 consists of three separate repositories
* https://github.com/vaadin/vaadin.git
* https://github.com/vaadin/gwt.git
* https://github.com/vaadin/gwt-tools.git

Start by cloning these repositories **into the same folder**:
<pre><code>git clone https://github.com/vaadin/vaadin.git
git clone https://github.com/vaadin/gwt.git
git clone https://github.com/vaadin/gwt-tools.git</code></pre>

The *vaadin* and *gwt* repositories contain project code. The *gwt-tools* project only contain dependency jars used by the other projects.

Do not rename the repositories as the rest of this document relies on using the standard naming.

Setting up Eclipse to Develop Vaadin 7
=========
Assuming you have cloned the repositories as described in “Cloning the project repositories” above, you can import the *vaadin* and *gwt* projects into Eclipse as follows:

Start Eclipse
-------------
Start Eclipse and **use the root checkout folder** (the one containing the *vaadin*, *gwt* and *gwt-tools* folders) **as the workspace folder**

Install IvyDE
---------
You'll need the Apache Ivy plug-in for Eclipse to build the project later on, in “Compiling the Default Widget Set and Themes”.

1. Go to *Help* -> *Install New Software...*
1. Enter `http://www.apache.org/dist/ant/ivyde/updatesite` in the "Work with:" text field
1. Select and install all items

If you have installed IvyDE via the Eclipse Marketplace previously, **make sure** that you also have *Apache Ivy Ant Tasks* installed, which is not included in that IvyDE installation:

1. Go to *Help* -> *Install New Software...*
1. Click the hyperlink in the "What is already installed?" sentence near the bottom right-hand corner
1. Verify that the list includes *Apache Ivy Ant Tasks*

If it isn't included, follow the installation process above, but select only *Apache Ivy library* > *Apache Ivy Ant Tasks*

Set up the Workspace and define required variables for projects
--------
1. Open *Window* -> *Preferences* (Windows) or *Eclipse* -> *Preferences* (Mac)
1. Go to *General* ->  *Workspace*
 1. Set *Text file encoding* to *UTF-8*
 1. Set *New text file line delimiter* to *Unix*
1. Go to *General* ->  *Workspace* -> *Linked Resources*
1. Add a new Path Variable **GWT_ROOT** referring to the gwt folder containing the gwt project
![GWT_ROOT](http://f.cl.ly/items/430q0H0z3t362Z1A1n3L/LinkedResources.png "Defining GWT_ROOT")
1. Go to *Java* -> *Build Path* -> *Classpath Variables*
1. Add two new variables
  1. GWT_TOOLS referring to the gwt-tools folder containing the dependency jars
  1. JDK_HOME referring to your jdk installation directory
     ![GWT_TOOLS](http://f.cl.ly/items/1k2Z1n2v0p0y3l0X0D1G/ClasspathVars.png "Defining GWT_TOOLS")
1. Go to Java -> Compiler
  1. Check that the compliance level has been set to 1.6
1. Go to XML -> XML Files -> Editor
 1. Ensure the settings are follows:
<pre><code>Line width: 72
Format comments: true
Join lines: true
Insert whitespace before closing empty end-tags: true
Indent-using spaces: true
Indentation size: 4
</code></pre>

Import the Projects into the Workspace
------------
1. Do *File* -> *Import* -> *General* -> *Existing Projects into Workspace*
![ImportProject](http://f.cl.ly/items/0G361519182v1z2T1o1O/Import.png "Import project")
1. Select the workspace folder as root directory
1. Click “deselect all” and select
  1. gwt-dev
  2. gwt-user
1. Click “finish” to complete the import of GWT
1. Then repeat by doing *File* -> *Import* -> *General* -> *Existing Projects into Workspace*
1. Select the workspace folder as root directory
1. Click “deselect all” and select
  1. vaadin
1. Click “finish” to complete the import of Vaadin Framework

![FinishImportProject](http://cl.ly/image/2W3S0P2c2p1t/Import2.png "Finishing Project Import")

You should now have three projects in your workspace. If the vaadin project does not compile without errors, choose *Ivy* -> *Resolve* from the vaadin project popup menu. Now all projects should compile without errors (there might be warnings).

Note that the first compilation takes a while to finish as Ivy downloads dependencies used in the projects.

Compiling the Default Widget Set and Themes
--------
Compile the default widget set by executing the default target in build/ide.xml in the vaadin project.
In Eclipse this is done by opening build/ide.xml, right clicking on it and choosing *Run As* -> *Ant Build*.
![CompileWidgetSet](http://cl.ly/image/1R43162b282e/build.png "Compiling the Widget Set")

Running a UI test
------
The *vaadin* project includes an embedded Jetty which is used for running the UI tests.
It is a standard Java application: *com.vaadin.launcher.DevelopmentServerLauncher*.
Launch it in debug mode in Eclipse by right clicking on it and selecting *Debug As* -> *Java Application*.

This launches a Jetty on port 8888 which allows you to run any UI class in the project by opening http://localhost:8888/run/&lt;UI class name&gt;?restartApplication in your browser, e.g. [http://localhost:8888/run/com.vaadin.tests.components.label.LabelModes?restartApplication](http://localhost:8888/run/com.vaadin.tests.components.label.LabelModes?restartApplication) (Add ?restartApplication to ensure).

Running JUnit tests
=====
The JUnit tests for the projects can be run using
<pre><code>ant test</code></pre>

Running this in the *gwt* directory will run the GWT JUnit tests.
Running it in the *vaadin* directory will run the Vaadin JUnit tests.

Note that the included Vaadin TestBench (browser) tests currently requires access to a TestBench cluster, which is currently only available internally at Vaadin Ltd.

Building a package
=====
The distribution files can be built in a few steps. First build the *gwt* project by running
<pre><code>ant</code></pre>
in the *gwt* directory. The elemental package needs to be built separately:
<pre><code>ant elemental</code></pre>
Building the elemental package is not possible on Windows as it requires gcc.

Move to the *vaadin* project directory and unpack the previously built gwt jars
<pre><code>ant -f gwt-files.xml unpack.gwt</code></pre>
Then build the *vaadin* project by running
<pre><code>ant</code></pre>
in the *vaadin* directory.
