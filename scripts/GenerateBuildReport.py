#coding=UTF-8

from BuildDemos import demos
import argparse

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
<tr><td><a href="https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Closed tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Pending-release tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&milestone=">Pending-release tickets without milestone</a></td></tr>
<tr><td><a href="apidiff/changes.html">API Diff</a></td></tr>
<tr><td><a href="release-notes/release-notes.html">Release Notes</a></td></tr>
""".format(version=args.version)

for demo in demos:
	content += "<tr><td><a href='{url}/{demoName}-{version}'>{demoName}</a></td></tr>\n".format(url=args.deployUrl, demoName=demo, version=args.version)

content += """</table>
</body>
</html>"""

f = open("result/report.html", 'w')
f.write(content)
