#coding=UTF-8

from BuildDemos import demos
import argparse

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
<tr><td><a href="https://dev.vaadin.com/milestone?action=new">Create milestone for next release</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Closed tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Pending-release tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&milestone=">Pending-release tickets without milestone</a></td></tr>
<tr><td><a href="apidiff/changes.html">API Diff</a></td></tr>
<tr><td><a href="release-notes/release-notes.html">Release Notes</a></td></tr>
<tr><td>Try demos<ul>
""".format(version=args.version)

for demo in demos:
	content += "<li><a href='{url}/{demoName}-{version}'>{demoName}</a></li>\n".format(url=args.deployUrl, demoName=demo, version=args.version)

content += """</ul></td></tr>
<tr><td><a href="{url}">Build result page (See test results, pin and tag build and dependencies)</a></td></tr>
</table>
</body>
</html>""".format(url=args.buildResultUrl)

f = open("result/report.html", 'w')
f.write(content)
