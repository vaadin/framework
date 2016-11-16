#coding=UTF-8

try:
	import requests
except Exception as e:
	print("GeneratePublishReportPart1 depends on requests library. Install it with `pip install requests`")
	sys.exit(1)
import argparse, cgi, re
from os.path import exists, isdir
from os import makedirs

metadataChecks = {
	'https://vaadin.com/download/LATEST7': '^7\..*',
	'https://vaadin.com/download/VERSIONS_7': '^7\..*',
	'https://vaadin.com/download/release/7.7/LATEST': '^7\..*',
	'https://vaadin.com/download/LATEST': '^6\..*',
	'https://vaadin.com/download/PRERELEASES': '^8\..*'
}

parser = argparse.ArgumentParser(description="Post-publish report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("teamcityUrl", type=str, help="Address to the teamcity server")
parser.add_argument("buildTypeId", type=str, help="The ID of this build step")
parser.add_argument("buildId", type=str, help="ID of the build to generate this report for")
args = parser.parse_args()

traffic_light = "<svg width=\"20px\" height=\"20px\" style=\"padding-right:5px\"><circle cx=\"10\" cy=\"10\" r=\"10\" fill=\"{color}\"/></svg>"

def getTrafficLight(b):
	return traffic_light.format(color="green") if b else traffic_light.format(color="red")

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

def checkUrlContents(url, regexp):
	r = requests.get(url)
	return re.match(regexp, r.text) != None

def checkUrlStatus(url):
	r = requests.get(url)
	return r.status_code == 200

metadataOk = True
for url in metadataChecks:
	metadataOk = metadataOk and checkUrlContents(url, metadataChecks[url].format(ver=args.version))

tagOk = checkUrlStatus("https://github.com/vaadin/vaadin/releases/tag/{ver}".format(ver=args.version))

if not prerelease:
	downloadPageOk = checkUrlStatus("https://vaadin.com/download/release/{maj}.{min}/{ver}/".format(maj=major, min=minor, ver=args.version))
else:
	downloadPageOk = checkUrlStatus("https://vaadin.com/download/prerelease/{maj}.{min}/{maj}.{min}.{main}/{ver}".format(maj=major, min=minor, main=maintenance, ver=args.version))

content = """<html>
<head></head>
<body>
<table>
<tr><td>{metadataOk}</td><td>Metadata ok on vaadin.com</td></tr>
<tr><td>{tagOk}</td><td>Tag ok on github.com</td></tr>
<tr><td>{downloadPageOk}</td><td>Download folder on vaadin.com contains the version</td></tr>
""".format(metadataOk=getTrafficLight(metadataOk), tagOk=getTrafficLight(tagOk), downloadPageOk=getTrafficLight(downloadPageOk))

mavenUrl = ""
if not prerelease:
	mavenUrl = "http://repo1.maven.org/maven2/com/vaadin/vaadin-server/{ver}".format(ver=args.version)
	content += "<tr><td></td><td><a href='{mvnUrl}'>Check {ver} is published to maven.org (might take a while)</td></tr>".format(ver=args.version, mvnUrl=mavenUrl)
else:
	mavenUrl = "http://maven.vaadin.com/vaadin-prereleases/com/vaadin/vaadin-server/{ver}".format(ver=args.version)
	content += "<tr><td></td><td><a href='{mvnUrl}'>Check {ver} is published as prerelease to maven.vaadin.com</td></tr>".format(ver=args.version, mvnUrl=mavenUrl)

content += "<tr><td></td><td><a href=\"https://dev.vaadin.com/admin/ticket/versions\">Add version {version} to Trac</a></td></tr>".format(version=args.version)

if not prerelease:
	content += '<tr><td></td><td><a href="https://dev.vaadin.com/admin/ticket/versions">Set latest version to default</a></td></tr>'

content += """
<tr><td></td><td><a href="http://test.vaadin.com/{version}/run/LabelModes?restartApplication">Verify uploaded to test.vaadin.com</a></td></tr>
""".format(version=args.version)

if not prerelease:
	content += '<tr><td></td><td><a href="http://vaadin.com/api">Verify API version list updated</a></td></tr>'

content += """
<tr><td></td><td><a href="http://{teamcityUrl}/viewLog.html?buildId={buildId}&buildTypeId={buildTypeId}&tab=dependencies"><h2>Start Post-Publish Release from dependencies tab</a></td></tr>
</table>
</body>
</html>""".format(teamcityUrl=args.teamcityUrl, buildTypeId=args.buildTypeId, buildId=args.buildId, version=args.version)

f = open("result/report.html", 'w')
f.write(content)
