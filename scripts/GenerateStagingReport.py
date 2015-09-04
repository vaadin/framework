#coding=UTF-8

from BuildArchetypes import archetypes, getDeploymentContext
from BuildDemos import demos
import argparse, cgi

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")
parser.add_argument("stagingRepo", type=str, help="URL for the staging repository")
parser.add_argument("tbapiUrl", type=str, help="URL for the TestBench API build")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
"""

content += "<tr><td>Try demos<ul>"

for demo in demos:
	content += "<li><a href='{url}/{demoName}-{version}'>{demoName}</a></li>\n".format(url=args.deployUrl, demoName=demo, version=args.version)

content += "</ul></td></tr>\n<tr><td>Try archetype demos<ul>"

for archetype in archetypes:
	content += "<li><a href='{url}/{context}'>{demo}</a></li>\n".format(url=args.deployUrl, demo=archetype, context=getDeploymentContext(archetype, args.version))

content += """</ul></td></tr>
<tr><td><a href="{repoUrl}">Staging repository</a></td></tr>
<tr><td>Eclipse Ivy Settings:<br><pre>""".format(repoUrl=args.stagingRepo)
content += cgi.escape("""	<ibiblio name="vaadin-staging" usepoms="true" m2compatible="true" 
		root="{repoUrl}" />""".format(repoUrl=args.stagingRepo))
content += """</pre>
</td></tr>
<tr><td><a href="https://dev.vaadin.com/milestone/Vaadin {version}">Close Trac Milestone</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&col=id&col=summary&col=component&col=milestone&col=status&col=type">Verify pending release tickets still have milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/admin/ticket/versions">Add version {version} to Trac</td></tr>
<tr><td><a href="{url}">Staging result page (See test results, pin and tag build and dependencies)</a></td></tr>
<tr><td>Commands to tag all repositories (warning: do not run as a single script but set variables and check before any push commands - this has not been tested yet and the change IDs are missing)</td></tr>
<tr><td><pre>
VERSION={version}

GERRIT_USER=[fill in your gerrit username]
FRAMEWORK_REVISION=[fill in framework revision]
SCREENSHOTS_REVISION=[fill in screenshot repository revision]
ARCHETYPES_REVISION=[fill in maven-integration repository revision]
PLUGIN_REVISION=[fill in maven plug-in repository revision]

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
</html>""".format(url=args.buildResultUrl, repoUrl=args.stagingRepo, version=args.version, tbapi=args.tbapiUrl)

f = open("result/report.html", 'w')
f.write(content)
