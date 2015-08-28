#coding=UTF-8

import argparse, cgi
from os.path import exists, isdir
from os import makedirs

parser = argparse.ArgumentParser(description="Post-publish report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")

args = parser.parse_args()

resultPath = "result"
if not exists(resultPath):
	makedirs(resultPath)
elif not isdir(resultPath):
	print("Result path is not a directory.")
	sys.exit(1)

(major, minor, maintenance) = args.version.split(".", 2)
prerelease = "." in maintenance
if prerelease:
	maintenance = maintenance.split('.')[0]

content = """<html>
<head></head>
<body>
<table>
"""

if not prerelease:
	content += "<tr><td><a href='http://vaadin.com/download/release/{maj}.{min}/{ver}/'>Check {ver} is published to vaadin.com/download</td></tr>".format(maj=major, min=minor, ver=args.version)
	content += "<tr><td><a href='http://repo1.maven.org/maven2/com/vaadin/vaadin-server/{ver}'>Check {ver} is published to maven.org (might take a while)</td></tr>".format(ver=args.version)
else:
	content += "<tr><td><a href='http://vaadin.com/download/prerelease/{maj}.{min}/{maj}.{min}.{main}/{ver}'>Check {ver} is published as prerelease to vaadin.com/download</td></tr>".format(maj=major, min=minor, main=maintenance, ver=args.version)
	content += "<tr><td><a href='http://maven.vaadin.com/vaadin-prereleases/com/vaadin/vaadin-server/{ver}'>Check {ver} is published as prerelease to maven.vaadin.com</td></tr>".format(ver=args.version)


content += """
<tr><td>Verify Latest Vaadin 7: <iframe src="http://vaadin.com/download/LATEST7"></iframe></td></tr>
<tr><td>Verify Vaadin 7 Version List: <iframe src="http://vaadin.com/download/VERSIONS_7"></iframe></td></tr>
<tr><td>Verify Latest Vaadin 7.5: <iframe src="http://vaadin.com/download/release/7.5/LATEST"></iframe></td></tr>
<tr><td>Verify Latest Vaadin 7.6: <iframe src="http://vaadin.com/download/release/7.6/LATEST"></iframe></td></tr>
<tr><td>Verify Latest Vaadin 6: <iframe src="http://vaadin.com/download/LATEST"></iframe></td></tr>
<tr><td>Verify Latest Vaadin 7 Prerelease: <iframe src="http://vaadin.com/download/PRERELEASES"></iframe></td></tr>"""

if not prerelease:
	content += '<tr><td><a href="https://dev.vaadin.com/admin/ticket/versions">Set latest version to default</a></td></tr>'

content += """
<tr><td><a href="http://test.vaadin.com/{version}/run/LabelModes?restartApplication">Verify uploaded to test.vaadin.com</a></td></tr>
<tr><td><a href="https://github.com/vaadin/vaadin/tags">Verify tags pushed to GitHub</a></td></tr>""".format(version=args.version)

if not prerelease:
	content += '<tr><td><a href="http://vaadin.com/api">Verify API version list updated</a></td></tr>'

content += """
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Batch update tickets in Trac</a></td></tr>
<tr><td><a href="{url}">Publish result page (See test results, pin and tag build and dependencies)</a></td></tr>
</table>
</body>
</html>""".format(url=args.buildResultUrl, version=args.version)

f = open("result/report.html", 'w')
f.write(content)
