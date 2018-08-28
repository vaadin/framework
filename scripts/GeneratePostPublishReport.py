import argparse, requests

parser = argparse.ArgumentParser(description="Post-publish report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("teamcityUrl", type=str, help="Address to the teamcity server")
parser.add_argument("buildTypeId", type=str, help="The ID of this build step")
parser.add_argument("buildId", type=str, help="ID of the build to generate this report for")
parser.add_argument("projectId", type=str, help="The ID of this project")
args = parser.parse_args()

buildResultUrl = "http://{}/viewLog.html?buildId={}&tab=buildResultsDiv&buildTypeId={}".format(args.teamcityUrl, args.buildId, args.buildTypeId)

(major, minor, maintenance) = args.version.split(".", 2)
prerelease = "." in maintenance

def checkUrlStatus(url):
	r = requests.get(url)
	return r.status_code == 200

def createTableRow(*columns):
	html = "<tr>"
	for column in columns:
		html += "<td>" + column + "</td>"
	return html + "</tr>"

traffic_light = "<svg width=\"20px\" height=\"20px\" style=\"padding-right:5px\"><circle cx=\"10\" cy=\"10\" r=\"10\" fill=\"{color}\"/></svg>"

def getTrafficLight(b):
	return traffic_light.format(color="green") if b else traffic_light.format(color="red")

def checkArchetypeMetaData(archetypeMetadataUrl, version):
	archetype_metadata_request = requests.get(archetypeMetadataUrl)
	if archetype_metadata_request.status_code != 200:
		return createTableRow(traffic_light.format(color="black"), "Check archetype metadata: <a href='{url}'>unable to retrieve metadata from {url}</a>".format(url=archetypeMetadataUrl))
	else:
		if "version=\"{version}\"".format(version=version) in archetype_metadata_request.content:
			return createTableRow(traffic_light.format(color="green"), "Check archetype metadata: <a href='{url}'>metadata is correct for {url}</a>".format(url=archetypeMetadataUrl))
		else:
			return createTableRow(traffic_light.format(color="red"), "Check archetype metadata: <a href='{url}'>metadata seems to be incorrect for {url}</a>".format(url=archetypeMetadataUrl))

content = "<html><head></head><body><table>"

tagOk = checkUrlStatus("https://github.com/vaadin/framework/releases/tag/{ver}".format(ver=args.version))
content += createTableRow(getTrafficLight(tagOk), "Tag ok on github.com")

# Tag and pin build
content += createTableRow("", "<a href=\"{url}\">Tag and pin build</a>".format(url=buildResultUrl))

# Traffic light for archetype metadata
content += checkArchetypeMetaData("http://vaadin.com/download/eclipse-maven-archetypes.xml", args.version)
if prerelease:
	content += checkArchetypeMetaData("http://vaadin.com/download/maven-archetypes-prerelease.xml", args.version)
content += createTableRow("", "Optionally check that <a href=\"http://vaadin.com/download/maven-archetypes.xml\">old Eclipse metadata</a> still refers to Vaadin 7")
content += createTableRow("", "Note that archetype metadata checks do not verify that the relevant sections are not commented out when changing from pre-release to stable and back!")

content += createTableRow("", "Build and deploy new sampler if necessary")

# Inform marketing and PO
content += createTableRow("", "Inform marketing and PO about the release")

# Link to version update in teamcity
content += createTableRow("", "<a href=\"http://{}/admin/editProject.html?projectId={}&tab=projectParams\">Update vaadin.version.latest and vaadin.version.next parameters in TeamCity</a>".format(args.teamcityUrl, args.projectId))

# Link to GH release notes
content += createTableRow("", "<a href=\"https://github.com/vaadin/framework/releases\">Finish and publish release notes in GH</a>")

content += "</table></body></html>"

with open("result/report.html", "wb") as f:
	f.write(content)
