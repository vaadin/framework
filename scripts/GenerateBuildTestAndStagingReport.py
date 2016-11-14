from BuildDemos import demos
from BuildArchetypes import archetypes, getDeploymentContext
import argparse, requests, json, subprocess, re, pickle

parser = argparse.ArgumentParser()
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")

parser.add_argument("teamcityUser", type=str, help="Teamcity username to use")
parser.add_argument("teamcityPassword", type=str, help="Password for given teamcity username")

parser.add_argument("teamcityUrl", type=str, help="Address to the teamcity server")
parser.add_argument("buildTypeId", type=str, help="The ID of this build step")
parser.add_argument("buildId", type=str, help="ID of the build to generate this report for")

parser.add_argument("frameworkRepoUrl", type=str, help="URL to the framework staging repository")
parser.add_argument("archetypeRepoUrl", type=str, help="URL to the archetype staging repository")
parser.add_argument("pluginRepoUrl", type=str, help="URL to the plugin staging repository")
args = parser.parse_args()

buildResultUrl = "http://{}/viewLog.html?buildId={}&tab=buildResultsDiv&buildTypeId={}".format(args.teamcityUrl, args.buildId, args.buildTypeId)

def createTableRow(*columns):
    html = "<tr>"
    for column in columns:
        html += "<td>" + column + "</td>"
    return html + "</tr>"

def getHtmlList(array):
    html = "<ul>"
    for item in array:
        html += "<li>" + item + "</li>"
    return html + "</ul>"

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
            return createTableRow(traffic_light.format(color="red"), "Build status: there are failing build steps, <a href={}>check the build report</a>".format(buildResultUrl))

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
            return createTableRow(traffic_light.format(color="red"), "Test status: there are " + str(test_failures_json["count"]) + " failing tests, <a href={}>check the build report</a>".format(buildResultUrl))

def getDemoValidationStatusHtml():
    status = pickle.load(open("result/demo_validation_status.pickle", "rb"))
    if status["error"]:
        return createTableRow(traffic_light.format(color="red"), getHtmlList(status["messages"]))
    else:
        return createTableRow(traffic_light.format(color="green"), getHtmlList(status["messages"]))

def getDemoLinksHtml():
    demos_html = "Try demos"
    link_list = list(map(lambda demo: "<a href='{url}/{demoName}-{version}'>{demoName}</a>".format(url=args.deployUrl, demoName=demo, version=args.version), demos))
    return demos_html + getHtmlList(link_list)

def getArchetypeLinksHtml():
    archetypes_html = "Try archetypes"
    link_list = list(map(lambda archetype: "<a href='{url}/{context}'>{archetypeName}</a>".format(url=args.deployUrl, archetypeName=archetype, context=getDeploymentContext(archetype, args.version)), archetypes))
    return archetypes_html + getHtmlList(link_list)

def getDirs(url):
    page = requests.get(url)
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
        return createTableRow(traffic_light.format(color="green"), "No extra artifacts found in the {} staging repository. <a href=\"{}\">Link to the repository.</a>".format(name, repoUrl))
    else:
        return createTableRow(traffic_light.format(color="red"), "Extra artifacts found in the {} staging repository. <a href=\"{}\">Link to the repository.</a>".format(name, repoUrl))

def completeArtifactName(artifactId, version):
    return 'com/vaadin/' + artifactId + '/' + version

def completeArtifactNames(artifactIds, version):
    return list(map(lambda x: completeArtifactName(x, version), artifactIds))


allowedPluginArtifacts = completeArtifactNames([ 'vaadin-maven-plugin' ], args.version)
allowedArchetypeArtifacts = completeArtifactNames([ 'vaadin-archetype-application', 'vaadin-archetype-application-multimodule', 'vaadin-archetype-application-example', 'vaadin-archetype-widget', 'vaadin-archetype-liferay-portlet' ], args.version)
allowedFrameworkArtifacts = completeArtifactNames([ 'vaadin-root', 'vaadin-bom', 'vaadin-shared', 'vaadin-server', 'vaadin-client', 'vaadin-client-compiler', 'vaadin-client-compiled', 'vaadin-push', 'vaadin-themes', 'vaadin-widgets' ], args.version)

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
    missing = subprocess.check_output(['egrep', '-v', '/(testbench|test|tests|target)/'], stdin=p2.stdout)
    content += createTableRow(traffic_light.format(color="red"), "Empty @since:<br><pre>%s</pre>" % (missing))

except subprocess.CalledProcessError as e:
    if e.returncode == 1:
        content += createTableRow(traffic_light.format(color="green"), "No empty @since")
    else:
        raise e

# check staging repositories don't contain extra artifacts
content += getStagingContentsHtml(args.frameworkRepoUrl, allowedFrameworkArtifacts, "framework")
content += getStagingContentsHtml(args.archetypeRepoUrl, allowedArchetypeArtifacts, "archetype")
content += getStagingContentsHtml(args.pluginRepoUrl, allowedPluginArtifacts, "plugin")

content += createTableRow("", "<h2>Manual checks before publishing</h2>")
# try demos
content += createTableRow("", getDemoLinksHtml())
content += createTableRow("", getArchetypeLinksHtml())

# link to release notes
content += createTableRow("", "<a href=\"http://{}/repository/download/{}/{}:id/release-notes/release-notes.html\">Check release notes</a>".format(args.teamcityUrl, args.buildTypeId, args.buildId))
# link to api diff
content += createTableRow("", "<a href=\"http://{}/repository/download/{}/{}:id/apidiff/changes.html\">API Diff</a>".format(args.teamcityUrl, args.buildTypeId, args.buildId))

# check that trac tickets are in the correct status
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=closed&status=pending-release&component=Core+Framework&resolution=fixed&group=milestone&col=id&col=summary&col=component&col=status&col=type&col=priority&col=milestone&order=priority\">Check that trac tickets have correct status</a>")
# pending release tickets without milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&milestone=\">Pending-release tickets without milestone</a>")

content += createTableRow("", "<h2>Preparations before publishing</h2>")
# close trac milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/milestone/Vaadin {version}\">Close Trac Milestone (deselect \"retarget tickets\")</a>".format(version=args.version))
# verify pending release tickets still have milestone
content += createTableRow("", "<a href=\"https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&col=id&col=summary&col=component&col=milestone&col=status&col=type\">Verify pending release tickets still have milestone {version}</a>".format(version=args.version))
# link to build dependencies tab to initiate publish step
content += createTableRow("", "<a href=\"http://{}/viewLog.html?buildId={}&buildTypeId={}&tab=dependencies\"><h2>Start Publish Release from dependencies tab</h2></a>".format(args.teamcityUrl, args.buildId, args.buildTypeId))

content += "</table></body></html>"
f = open("result/report.html", 'w')
f.write(content)
