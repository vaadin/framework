import argparse, requests, json, subprocess, re

def createTableRow(*columns):
	html = "<tr>"
	for column in columns:
		html += "<td>" + column + "</td>"
	return html + "</tr>"


def getBuildStatusHtml():
	build_steps_request_string = "http://{}/app/rest/problemOccurrences?locator=build:{}".format(args.teamcityUrl, args.buildId)
	build_steps_request = requests.get(build_steps_request_string, auth=(args.teamcityUser, args.teamcityPassword), headers={'Accept':'application/json'})
	if build_steps_request.status_code != 200:
		return createTableRow(traffic_light.format(color="black"), "Build status: unable to retrieve status of build")
	else:
		build_steps_json = build_steps_request.json()
		if build_steps_json["count"] == 0:
			return createTableRow(traffic_light.format(color="green"), "Build status: all build steps successful")
		else:
			return createTableRow(traffic_light.format(color="red"), "Build status: there are failing build steps, <a href={}>check the build report</a>".format(args.buildResultUrl))

def getTestStatusHtml():
	test_failures_request_string = "http://{}/app/rest/testOccurrences?locator=build:{},status:FAILURE".format(args.teamcityUrl, args.buildId)
	test_failures_request = requests.get(test_failures_request_string, auth=(args.teamcityUser, args.teamcityPassword), headers={'Accept':'application/json'})
	if test_failures_request.status_code != 200:
		return createTableRow(traffic_light.format(color="black"), "Test status: unable to retrieve status of tests")
	else:
		test_failures_json = test_failures_request.json()
		if test_failures_json["count"] == 0:
			return createTableRow(traffic_light.format(color="green"), "Test status: all tests passing")
		else:
			return createTableRow(traffic_light.format(color="red"), "Test status: there are " + str(test_failures_json["count"]) + " failing tests")
"""
			if "testOccurrence" in test_failures_json:
				ret += "<p> Failures:"
				ret += "<ul>"
				for test_failure in test_failures_json["testOccurrence"]:
					ret += "<li><a href=\"http://" + args.teamcityUrl + test_failure["href"] + "\">" + test_failure["name"] + "</a></li>"
			return ret
"""

def getDirs(url):
    page = requests.get(url)
    # files = tree.xpath('//tr/td/a/text()')
    files = re.findall('<a href=.*>(.*)</a>', page.text)
    dirs = filter(lambda x: x.endswith('/'), files)
    return list(map(lambda x: x.replace('/', ''), dirs))

def dirTree(url):
    dirs = getDirs(url)
    result = []
    for d in dirs:
        result.append(d)
        subDirs = list(map(lambda x: d + '/' + x, dirTree(url + '/' + d)))
        result.extend(subDirs)
    return result

def getAllowedArtifactPaths(allowedArtifacts):
    result = []
    for artifact in allowedArtifacts:
        parts = artifact.split('/', 1)
        result.append(parts[0])
        if len(parts) > 1:
            subart = getAllowedArtifactPaths([ parts[1] ])
            subArtifacts = list(map(lambda x: parts[0] + '/' + x, subart))
            result.extend(subArtifacts)
    return result

def checkStagingContents(url, allowedArtifacts):
    dirs = dirTree(url)
    allowedDirs = getAllowedArtifactPaths(allowedArtifacts)
    return set(dirs) == set(allowedDirs)

def getStagingContentsHtml(repoUrl, allowedArtifacts, name):
	if checkStagingContents(repoUrl, allowedArtifacts):
		return createTableRow(traffic_light.format(color="green"), "No extra artifacts found in the {} staging repository".format(name))
	else:
		return createTableRow(traffic_light.format(color="red"), "Extra artifacts found in the {} staging repository".format(name))

def completeArtifactName(artifactId, version):
    return 'com/vaadin/' + artifactId + '/' + version

def completeArtifactNames(artifactIds, version):
    return list(map(lambda x: completeArtifactName(x, version), artifactIds))

parser = argparse.ArgumentParser()
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")

parser.add_argument("teamcityUser", type=str, help="Teamcity username to use")
parser.add_argument("teamcityPassword", type=str, help="Password for given teamcity username")

parser.add_argument("teamcityUrl", type=str, help="Address to the teamcity server")
parser.add_argument("buildId", type=str, help="ID of the build to generate this report for")

parser.add_argument("frameworkRepoUrl", type=str, help="URL to the framework staging repository")
parser.add_argument("archetypeRepoUrl", type=str, help="URL to the archetype staging repository")
parser.add_argument("pluginRepoUrl", type=str, help="URL to the plugin staging repository")
args = parser.parse_args()

allowedPluginArtifacts = completeArtifactNames([ 'vaadin-maven-plugin' ], args.version)
allowedArchetypeArtifacts = completeArtifactNames([ 'vaadin-archetype-application', 'vaadin-archetype-application-multimodule', 'vaadin-archetype-application-example', 'vaadin-archetype-widget', 'vaadin-archetype-liferay-portlet' ], args.version)
allowedFrameworkArtifacts = completeArtifactNames([ 'vaadin-root', 'vaadin-bom', 'vaadin-shared', 'vaadin-server', 'vaadin-client', 'vaadin-client-compiler', 'vaadin-client-compiled', 'vaadin-push', 'vaadin-themes', 'vaadin-widgets', 'vaadin-compatibility-shared', 'vaadin-compatibility-server', 'vaadin-compatibility-client', 'vaadin-compatibility-client-compiled', 'vaadin-compatibility-themes' ], args.version)

content = "<html><head></head><body><table>"
traffic_light = "<svg width=\"20px\" height=\"20px\" style=\"padding-right:5px\"><circle cx=\"10\" cy=\"10\" r=\"10\" fill=\"{color}\"/></svg>"

# Build step status
content += getBuildStatusHtml()

# Test failures
content += getTestStatusHtml()

# Missing @since tags
try:
	p1 = subprocess.Popen(['find', '.', '-name', '*.java'], stdout=subprocess.PIPE)
	p2 = subprocess.Popen(['xargs', 'egrep', '-n', '@since ?$'], stdin=p1.stdout, stdout=subprocess.PIPE)
	missing = subprocess.check_output(['egrep', '-v', '/(test|tests|target)/'], stdin=p2.stdout)
	content += createTableRow(traffic_light.format(color="red"), "Empty @since:<br><pre>%s</pre>" % (missing))

except subprocess.CalledProcessError as e:
    if e.returncode == 1:
        content += createTableRow(traffic_light.format(color="green"), "No empty @since")
    else:
        raise e

# framework repo: verify pending-release tickets have milestones

content += getStagingContentsHtml(args.frameworkRepoUrl, allowedFrameworkArtifacts, "framework")
content += getStagingContentsHtml(args.archetypeRepoUrl, allowedArchetypeArtifacts, "archetype")
content += getStagingContentsHtml(args.pluginRepoUrl, allowedPluginArtifacts, "plugin")

# Next steps
content += createTableRow("", "<a href=\"http://r2d2.devnet.vaadin.com/repository/download/Vaadin80_Releases_BuildTestAndStageRelease/{}:id/release-notes/release-notes.html\">Check release notes</a>".format(args.buildId))
content += createTableRow("", "<a href=\"http://r2d2.devnet.vaadin.com/repository/download/Vaadin80_Releases_BuildTestAndStageRelease/{}:id/apidiff/\">Check API diff</a>".format(args.buildId))

# create milestone for next release
content += createTableRow("", "<a href=\"https://dev.vaadin.com/milestone?action=new\">Create milestone for nex release</a>")
# closed fixed tickets without a milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=!Vaadin {version}&col=id&col=summary&col=component&col=status&col=type&col=priority&col=milestone&order=priority\">Closed fixed tickets without milestone {version}</a>".format(version=args.version))
# closed tickets with milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type\">Closed tickets with milestone {version}</a>".format(version=args.version))
# pending release tickets with milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type\">Pending-release tickets with milestone {version}</a>".format(version=args.version))
# pending release tickets without milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&milestone=\">Pending-release tickets without milestone</a>")

content += createTableRow("", "<h2>Preparations before publishing</h2>")
# close trac milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/milestone/Vaadin {version}\">Close Trac Milestone</a>".format(version=args.version))
# verify pending release tickets still have milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&col=id&col=summary&col=component&col=milestone&col=status&col=type\">Verify pending release tickets still have milestone {version}</a>".format(version=args.version))
# add version to trac
content += createTableRow("", "<a href=\"https://dev.vaadin.com/admin/ticket/versions\">Add version {version} to Trac".format(version=args.version))


content += "</table></body></html>"
f = open("report.html", 'w')
f.write(content)


