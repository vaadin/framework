#coding=UTF-8

from BuildArchetypes import archetypes, getDeploymentContext
from BuildDemos import demos
import argparse, subprocess, cgi

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")

parser.add_argument("stagingRepo", type=str, help="URL for the staging repository")
parser.add_argument("pluginRepo", type=str, help="URL for the maven plugin staging repository")
parser.add_argument("tbapiUrl", type=str, help="URL for the TestBench API build")

parser.add_argument("frameworkRevision", type=str, default="[fill in framework repository revision]", nargs="?")
parser.add_argument("screenshotRevision", type=str, default="[fill in screenshot repository revision]", nargs="?")
parser.add_argument("archetypeRevision", type=str, default="[fill in maven-integration repository revision]", nargs="?")
parser.add_argument("mavenPluginRevision", type=str, default="[fill in maven-plugin repository revision]", nargs="?")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
<tr><td><h2>Checks that might need a second build</h2></td></tr>
<tr><td><a href="https://dev.vaadin.com/milestone?action=new">Create milestone for next release</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=!Vaadin {version}&col=id&col=summary&col=component&col=status&col=type&col=priority&col=milestone&order=priority">Closed fixed tickets without milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Closed tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Pending-release tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&milestone=">Pending-release tickets without milestone</a></td></tr>
<tr><td><a href="apidiff/changes.html">API Diff</a></td></tr>
<tr><td><a href="release-notes/release-notes.html">Release Notes</a></td></tr>
""".format(version=args.version)

try:
	p1 = subprocess.Popen(['find', '.', '-name', '*.java'], stdout=subprocess.PIPE)
	p2 = subprocess.Popen(['xargs', 'egrep', '-n', '@since ?$'], stdin=p1.stdout, stdout=subprocess.PIPE)
	missing = subprocess.check_output(['egrep', '-v', '/(test|tests|target)/'], stdin=p2.stdout)
	content += "<tr><td>Empty @since:<br>\n<pre>%s</pre></td></tr>\n" % (missing)
except subprocess.CalledProcessError as e:
	if e.returncode == 1:
		content += "<tr><td>No empty @since</td></tr>\n"
	else:
		raise e

content += """<tr><td><a href="{url}">Build result page (See test results, pin and tag build and dependencies)</a></td></tr>""".format(url=args.buildResultUrl)
content += "<tr><td>Try demos<ul>"

for demo in demos:
    content += "<li><a href='{url}/{demoName}-{version}'>{demoName}</a></li>\n".format(url=args.deployUrl, demoName=demo, version=args.version)

content += "</ul></td></tr>\n<tr><td>Try archetype demos<ul>"

for archetype in archetypes:
    content += "<li><a href='{url}/{context}'>{demo}</a></li>\n".format(url=args.deployUrl, demo=archetype, context=getDeploymentContext(archetype, args.version))

content += """</ul></td></tr>
    <tr><td><a href="{repoUrl}">Staging repository</a></td></tr>
    <tr><td><a href="{pluginRepoUrl}">Maven Plugin Staging repository</a></td></tr>
    <tr><td>Eclipse Ivy Settings:<br><pre>""".format(repoUrl=args.stagingRepo, pluginRepoUrl=args.pluginRepo)
content += cgi.escape("""	<ibiblio name="vaadin-staging" usepoms="true" m2compatible="true"
    root="{repoUrl}" />""".format(repoUrl=args.stagingRepo))
content += """</pre>
    </td></tr>
    <tr><td><h2>Preparations before publishing</h2></td></tr>
    <tr><td><a href="https://dev.vaadin.com/milestone/Vaadin {version}">Close Trac Milestone</a></td></tr>
    <tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&col=id&col=summary&col=component&col=milestone&col=status&col=type">Verify pending release tickets still have milestone {version}</a></td></tr>
    <tr><td><a href="https://dev.vaadin.com/admin/ticket/versions">Add version {version} to Trac</td></tr>
    <tr><td><a href="{url}">Staging result page (See test results, pin and tag build and dependencies)</a></td></tr>
    <tr><td>Commands to tag all repositories (warning: do not run as a single script but set variables and check before any push commands - this has not been tested yet and the change IDs are missing)</td></tr>
    <tr><td><pre>
    VERSION={version}
    
    GERRIT_USER=[fill in your gerrit username]
    FRAMEWORK_REVISION={frameworkRevision}
    SCREENSHOTS_REVISION={screenshotRevision}
    ARCHETYPES_REVISION={archetypeRevision}
    PLUGIN_REVISION={mavenPluginRevision}
    
    git clone ssh://$GERRIT_USER@dev.vaadin.com:29418/vaadin
    cd vaadin
    git tag -a -m"$VERSION" $VERSION $FRAMEWORK_REVISION
    git push --tags
    cd ..
    
    git clone ssh://$GERRIT_USER@dev.vaadin.com:29418/vaadin-screenshots
    cd vaadin-screenshots
    git tag -a -m"$VERSION" $VERSION $SCREENSHOTS_REVISION
    git push --tags
    cd ..
    
    git clone ssh://$GERRIT_USER@dev.vaadin.com:29418/maven-integration
    cd maven-integration
    git tag -a -m"$VERSION" $VERSION $ARCHETYPES_REVISION
    git push --tags
    cd ..
    
    git clone ssh://$GERRIT_USER@dev.vaadin.com:29418/maven-plugin
    cd maven-plugin
    git tag -a -m"$VERSION" $VERSION $PLUGIN_REVISION
    git push --tags
    cd ..
    </pre></td></tr>
    <tr><td><a href="{tbapi}">Build and publish TestBench API for version {version} if proceeding</a></td></tr>
</table>
</body>
</html>""".format(url=args.buildResultUrl, repoUrl=args.stagingRepo, version=args.version, tbapi=args.tbapiUrl, frameworkRevision=args.frameworkRevision, screenshotRevision=args.screenshotRevision, archetypeRevision=args.archetypeRevision, mavenPluginRevision=args.mavenPluginRevision)

f = open("result/report.html", 'w')
f.write(content)
