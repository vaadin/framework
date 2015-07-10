#coding=UTF-8

from BuildArchetypes import archetypes, getDeploymentContext
import argparse, cgi

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")
parser.add_argument("stagingRepo", type=str, help="URL for the staging repository")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
"""

content += "<tr><td>Try archetype demos<ul>"

for archetype in archetypes:
	content += "<li><a href='{url}/{context}'>{demo}</a></li>\n".format(url=args.deployUrl, demo=archetype, context=getDeploymentContext(archetype, args.version))

content += """</ul></td></tr>
<tr><td><a href="{repoUrl}">Staging repository</a></td></tr>
<tr><td>Eclipse Ivy Settings:<br><pre>"""
content += cgi.escape("""	<ibiblio name="vaadin-staging" usepoms="true" m2compatible="true" 
		root="{repoUrl}" />""".format(repoUrl=args.stagingRepo))
content += """</pre>
</td></tr>
<tr><td><a href="https://dev.vaadin.com/milestone/Vaadin {version}">Trac Milestone</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/admin/ticket/versions">Add version {version} to Trac</td></tr>
<tr><td><a href="{url}">Staging result page (See test results, pin and tag build and dependencies)</a></td></tr>
</table>
</body>
</html>""".format(url=args.buildResultUrl, repoUrl=args.stagingRepo, version=args.version)

f = open("result/report.html", 'w')
f.write(content)
