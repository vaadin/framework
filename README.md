Vaadin
======
*[Vaadin](https://vaadin.com) is a Java framework for building modern web applications that look great, perform well and make you and your users happy.*

For instructions about _using_ Vaadin to develop applications, please refer to
https://vaadin.com/learn

To contribute, first refer to [Contributing Code](https://vaadin.com/wiki/-/wiki/Main/Contributing+Code)
for general instructions and requirements for contributing code to the Vaadin framework.

Instructions on how to set up a working environments for developing the Vaadin
framework follow below.

Eclipse Quick Setup
======
1. Run
<code>git clone https://github.com/vaadin/vaadin.git</code>
command or clone the repository your favorite Git tool.
If using Windows, you might want to add these Git settings: `core.autocrlf=false` and `core.fileMode=false`.
1. Run <code>mvn install</code> in the project root.
Note that the first compilation takes a while to finish as maven downloads dependencies used in the projects.
1. Start Eclipse with the workspace you would like to use. It is usually a good idea to use the parent folder of the Git repository as the workspace folder.
1. Import the project into Eclipse as a maven project. Use *File* -> *Import* -> *Maven* -> *Existing Maven Projects*.
1. Select the *vaadin* folder (where you cloned the project)
1. Click “Finish” to complete the import of Vaadin Framework

Now the project should compile without further configuration.


Compiling the Default Widget Set and Themes
--------
* Compile the default widgetset by running <code>install</code> maven goal in `vaadin-client-compiled` module root.
In Eclipse this is done by right clicking on vaadin-client-compiled project it and choosing *Run As* -> *Maven Build...*.
* Compile the default themes by running <code>install</code> maven goal in `vaadin-themes` module root.
In Eclipse this is done by right clicking on vaadin-themes project it and choosing *Run As* -> *Maven Build...*.

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
1. In a Project Explorer  right-click *vaadin-uitest*
1. Open *Run As* -> *Maven build...*
1. Type in <code>jetty:run-exploded</code> into *Goals* and click *Run*
1. Open URL *http://localhost:8080/run/<testUI>*

Building a package
=====
The distribution files can be built by running maven goal
<pre><code>maven clean install</code></pre>
in the project root directory.

To use a specific version number, modify <code>&lt;version&gt;</code> tag in root pom.xml file.
This goal runs all project tests TestBench tests, which require access to a a TestBench cluster, currently only available internally at Vaadin Ltd.

Setting up IntelliJ IDEA to Develop Vaadin 7
=========
1. Intall and run IDEA. Ultimate Edition is better but Community Edition should also work.
1. Ensure if Git and Maven plugins are installed, properly configured and enabled.
1. Clone the repository, using menu VCS -> Checkout from Version Control -> Git -> Git Repository URL -> https://github.com/vaadin/vaadin.git.
  When the repository is cloned, do **NOT** open it as a project.
1. Open cloned repository as a maven object. Use File -> Open and choose root _pom.xml_ file
1. Have a coffee break while IDEA is loading dependencies and indexing the project
1. Run Maven targets <code>clean</code> and <code>install</code> using *Maven Projects* tool window to compile the whole project

Running a UI test
------
1. Open *Maven Projects*
1. Open *vaadin-uitest* -> *Plugins* -> *jetty* -> *jetty:run-exploded*
1. Open URL *http://localhost:8080/run/<testUI>*
