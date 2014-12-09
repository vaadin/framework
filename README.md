Vaadin
======
*[Vaadin](https://vaadin.com) is a Java framework for building modern web applications that look great, perform well and make you and your users happy.*

For instructions about _using_ Vaadin to develop applications, please refer to
https://vaadin.com/learn

To contribute, first refer to https://vaadin.com/wiki/-/wiki/Main/Contributing+Code
for general instructions and requirements for contributing code to the Vaadin framework.

Instructions on how to set up a working environment for developing the Vaadin
framework follow below.

Quick Setup
======
1. <code>git clone https://github.com/vaadin/vaadin.git</code>
1. Install IvyDE, including Ant Tasks, if needed (http://www.apache.org/dist/ant/ivyde/updatesite)
1. Import the project into Eclipse
1. Run build/ide.xml in Eclipse

For more details, see below

Cloning the project repositories
======
The Vaadin repository can be cloned using
<pre><code>git clone https://github.com/vaadin/vaadin.git</code></pre>

or using your favorite Git tool.

If using Windows, you might want to add these Git settings: core.autocrlf=false and core.fileMode=false.

Setting up Eclipse to Develop Vaadin 7
=========

Start Eclipse
-------------
Start Eclipse with the workspace you would like to use. It is usually a good idea to use the parent folder of the Git repository as the workspace folder.

Install IvyDE
---------
You'll need the Apache Ivy plug-in for Eclipse to build the project:

1. Go to *Help* -> *Install New Software...*
1. Enter `http://www.apache.org/dist/ant/ivyde/updatesite` in the "Work with:" text field
1. Select and install all items

If you have installed IvyDE via the Eclipse Marketplace previously, **make sure** that you also have *Apache Ivy Ant Tasks* installed, which is not included in that IvyDE installation:

1. Go to *Help* -> *Install New Software...*
1. Click the hyperlink in the "What is already installed?" sentence near the bottom right-hand corner
1. Verify that the list includes *Apache Ivy Ant Tasks*
1. If it isn't included, follow the installation process above, but select only *Apache Ivy library* > *Apache Ivy Ant Tasks*


Import the Project into the Workspace
------------
1. Do *File* -> *Import* -> *General* -> *Existing Projects into Workspace*
![ImportProject](http://f.cl.ly/items/0G361519182v1z2T1o1O/Import.png "Import project")
1. Select the *vaadin* folder (where you cloned the project)
1. Ensure the *vaadin* project is checked
1. Click “finish” to complete the import of Vaadin Framework

The project should compile without further configuration. If the project does not compile without errors, choose *Ivy* -> *Resolve* from the vaadin project popup menu to ensure all dependencies have been resolved.

Note that the first compilation takes a while to finish as Ivy downloads dependencies used in the projects.

Compiling the Default Widget Set and Themes
--------
Compile the default widget set by executing the default target in build/ide.xml in the vaadin project.
In Eclipse this is done by opening build/ide.xml, right clicking on it and choosing *Run As* -> *Ant Build*.
![CompileWidgetSet](http://cl.ly/image/1R43162b282e/build.png "Compiling the Widget Set")

Set up extra workspace preferences
--------
The following preferences need to be set to keep the project consistent. You need to do this especially to be able to contribute changes to the project.

1. Open *Window* -> *Preferences* (Windows) or *Eclipse* -> *Preferences* (Mac)
1. Go to *General* ->  *Workspace*
 1. Set *Text file encoding* to *UTF-8*
 1. Set *New text file line delimiter* to *Unix*
1. Go to XML -> XML Files -> Editor
 1. Ensure the settings are follows:
<pre><code>Line width: 72
Format comments: true
Join lines: true
Insert whitespace before closing empty end-tags: true
Indent-using spaces: true
Indentation size: 4
</code></pre>

Running a UI test
------
The *vaadin* project includes an embedded Jetty (*com.vaadin.launcher.DevelopmentServerLauncher*) which is used for running the UI tests.
In Eclipse you can launch it using the included launch configuration: Right click on *eclipse/Development Server (vaadin).launch" and select *Debug As* -> *Development Server (vaadin)*.

This launches a Jetty on port 8888 which allows you to run any UI class in the project by opening http://localhost:8888/run/&lt;UI class name&gt;?restartApplication in your browser, e.g. [http://localhost:8888/run/com.vaadin.tests.components.label.LabelModes?restartApplication](http://localhost:8888/run/com.vaadin.tests.components.label.LabelModes?restartApplication) (Use ?restartApplication to ensure the correct UI is shown).

Running JUnit tests
=====
The unit tests for the projects can be run using
<pre><code>ant test</code></pre>

Note that the included Vaadin TestBench (browser) tests require access to a TestBench cluster, currently only available internally at Vaadin Ltd.

Building a package
=====
The distribution files can be built in two steps.

1. Unpack required gwt jars into the project
<pre><code>ant -f gwt-files.xml unpack.gwt</code></pre>
2. Build the project by running
<pre><code>ant</code></pre>
in the project root directory (add -Dvaadin.version=1.2.3 to use a specific version number).

Setting up other IDEs to Develop Vaadin 7
=========
- Unofficial instructions
  - IntelliJ IDEA: http://github.com/Saulis/vaadin-idea-workspace/
