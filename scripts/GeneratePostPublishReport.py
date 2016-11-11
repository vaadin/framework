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

def createTableRow(*columns):
    html = "<tr>"
    for column in columns:
        html += "<td>" + column + "</td>"
    return html + "</tr>"

traffic_light = "<svg width=\"20px\" height=\"20px\" style=\"padding-right:5px\"><circle cx=\"10\" cy=\"10\" r=\"10\" fill=\"{color}\"/></svg>"

content = "<html><head></head><body><table>"

# Batch update tickets in trac
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type\">Batch update tickets in Trac</a>")

# Create milestone for next release
content += createTableRow("", "<a href=\"https://dev.vaadin.com/milestone?action=new\">Create milestone for next release</a>")

# Tag and pin build
content += createTableRow("", "<a href=\"{url}\">Tag and pin build</a>".format(url=buildResultUrl))

# Traffic light for archetype metadata
archetypeMetadataUrl = ""
if not prerelease:
    archetypeMetadataUrl = "http://vaadin.com/download/maven-archetypes.xml"
else:
    archetypeMetadataUrl ="http://vaadin.com/download/maven-archetypes-prerelease.xml"

archetype_metadata_request = requests.get(archetypeMetadataUrl)
if archetype_metadata_request.status_code != 200:
    content += createTableRow(traffic_light.format(color="black"), "<a href='{url}'>Check archetype metadata: unable to retrieve metadata</a>".format(url=archetypeMetadataUrl))
else:
    if "version=\"{version}\"".format(version=args.version) in archetype_metadata_request.content:
        content += createTableRow(traffic_light.format(color="green"), "<a href='{url}'>Check archetype metadata: metadata is correct</a>".format(url=archetypeMetadataUrl))
    else:
        content += createTableRow(traffic_light.format(color="red"), "<a href='{url}'>Check archetype metadata: metadata is incorrect</a>".format(url=archetypeMetadataUrl))

# TODO GitHub milestones

# Inform marketing and PO
content += createTableRow("", "Inform marketing and PO about the release")

# Link to version update in teamcity
content += createTableRow("", "<a href=\"http://{}/admin/editProject.html?projectId={}&tab=projectParams\">Update vaadin.version.latest and vaadin.version.next parameters in TeamCity</a>".format(args.teamcityUrl, args.projectId))

# Link to GH release notes
content += createTableRow("", "<a href=\"https://github.com/vaadin/vaadin/releases/new\">Write release notes in GH</a>")

content += "</table></body></html>"

with open("result/report.html", "wb") as f:
    f.write(content)
