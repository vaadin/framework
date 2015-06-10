#coding=UTF-8

#
# Windows users: 
# You need to setup your environment so that you have mvn on your PATH.
# Maven needs that JAVA_HOME environment is set and points to a JDK
# Python3 is required as this script uses some Python3 specific features.
# Might work with Python2, haven't tested.
#
# python BuildArchetypes.py version fw-repo-id archetype-repo-id plugin-repo-id
#

import platform, subprocess, sys, argparse
from xml.etree import ElementTree
from os.path import join, isdir, isfile, basename
from os import listdir, getcwd, mkdir
from glob import glob
from shutil import copy

## DEFAULT VARIABLES ##

# ArchetypeGroupId
archetypeGroup = "com.vaadin"

# Staging repo base url
repo = "http://oss.sonatype.org/content/repositories/comvaadin-%d"

# List of built archetypes
archetypes = [
	"vaadin-archetype-widget", 
	"vaadin-archetype-application", 
	"vaadin-archetype-application-example", 
	"vaadin-archetype-application-multimodule"
]

# Directory where the resulting war files are stored
# TODO: deploy results
resultPath = "result"

# Maven GroupID
group = "testpkg"

## BUILDING METHODS ##

# Generates and modifies a maven pom file
def buildArchetype(archetype):
	global args, archetypeGroup, resultPath, group, mavenCmd
	
	artifactId = "test-%s-%s" % (archetype, args.version.replace(".", "-"))
	logFile = open(join(resultPath, "%s.log" % (archetype)), 'w')
	
	# Generate the required command line for archetype generation
	cmd = [mavenCmd, "archetype:generate"]
	cmd.append("-DarchetypeGroupId=%s" % (archetypeGroup))
	cmd.append("-DarchetypeArtifactId=%s" % (archetype))
	cmd.append("-DarchetypeVersion=%s" % (args.version))
	cmd.append("-DarchetypeRepository=%s" % (repo % (args.archetype)))
	cmd.append("-DgroupId=%s" % (group))
	cmd.append("-DartifactId=%s" % (artifactId))
	cmd.append("-Dversion=1.0-SNAPSHOT")
	cmd.append("-DinteractiveMode=false")
	
	# Generate pom.xml
	print("Generating pom.xml for archetype %s" % (archetype))
	subprocess.check_call(cmd, stdout=logFile)
	
	print("Add staging repositories to pom.xml")
	updateRepositories(artifactId)
	
	print("Do maven clean package validate")
	subprocess.check_call([mavenCmd, "clean", "package", "validate"], cwd=join(getcwd(), artifactId), stdout=logFile)
	
	# Find resulting .war files
	warFiles = glob(join(getcwd(), artifactId, "target", "*.war"))
	warFiles.extend(glob(join(getcwd(), artifactId, "*", "target", "*.war")))
	for warFile in warFiles:
		if len(warFiles) == 1:
			deployName = "%s.war" % (archetype)
		else:
			deployName = "%s-%d" % (archetype, warFiles.index(warFile))
		print("Copying .war file %s as %s to result folder" % (basename(warFile), deployName))
		copy(warFile, join(resultPath, "%s" % (deployName)))

# Recursive pom.xml update script
def updateRepositories(path):
	global args, repo
	
	# Read pom.xml
	pomXml = join(path, "pom.xml")
	if isfile(pomXml):
		# pom.xml namespace workaround
		root = ElementTree.parse(pomXml).getroot()
		nameSpace = root.tag[1:root.tag.index('}')]
		ElementTree.register_namespace('', nameSpace)
		
		# Read the pom.xml correctly
		tree = ElementTree.parse(pomXml)
		
		# NameSpace needed for finding the repositories node
		repoNode = tree.getroot().find("{%s}repositories" % (nameSpace))
	else:
		return
	
	if repoNode is not None:
		# Add framework staging repository
		addRepo(repoNode, "repository", "vaadin-%s-staging" % (args.version), repo % (args.framework))
		
		# Find the correct pluginRepositories node
		pluginRepo = tree.getroot().find("{%s}pluginRepositories" % (nameSpace))
		if pluginRepo is None:
			# Add pluginRepositories node if needed
			pluginRepo = ElementTree.SubElement(tree.getroot(), "pluginRepositories")
		
		# Add plugin staging repository
		addRepo(pluginRepo, "pluginRepository", "vaadin-%s-plugin-staging" % (args.version), repo % (args.plugin))
		
		# Overwrite the modified pom.xml
		tree.write(pomXml, encoding='UTF-8')
	
	# Recursive pom.xml search.
	for i in listdir(path):
		file = join(path, i)
		if isdir(file):
			updateRepositories(join(path, i))

# Add a repository of repoType to given repoNode with id and URL
def addRepo(repoNode, repoType, id, url):
	newRepo = ElementTree.SubElement(repoNode, repoType)
	idElem = ElementTree.SubElement(newRepo, "id")
	idElem.text = id
	urlElem = ElementTree.SubElement(newRepo, "url")
	urlElem.text = url

# Function for determining the path for maven executable
def getMavenCommand():
	# This method uses .split("\n")[0] which basically chooses the first result where/which returns.
	# Fixes the case with multiple maven installations available on PATH
	if platform.system() == "Windows":
		try:
			return subprocess.check_output(["where", "mvn.cmd"], universal_newlines=True).split("\n")[0]
		except:
			try:
				return subprocess.check_output(["where", "mvn.bat"], universal_newlines=True).split("\n")[0]
			except:
				print("Unable to locate mvn with where. Is the maven executable in your PATH?")
	else:
		try:
			return subprocess.check_output(["which", "mvn"], universal_newlines=True).split("\n")[0]
		except:
			print("Unable to locate maven executable with which. Is the maven executable in your PATH?")
	return None

## DO THIS IF RUN AS A SCRIPT (not import) ##
if __name__ == "__main__":
	mavenCmd = getMavenCommand()
	if mavenCmd is None:
		sys.exit(1)

	# Command line arguments for this script
	parser = argparse.ArgumentParser(description="Automated staging validation")
	parser.add_argument("version", type=str, help="Vaadin version to use")
	parser.add_argument("framework", type=int, help="Framework repo id (comvaadin-XXXX)")
	parser.add_argument("archetype", type=int, help="Archetype repo id (comvaadin-XXXX)")
	parser.add_argument("plugin", type=int, help="Maven Plugin repo id (comvaadin-XXXX)")
	
	# If no args, give help
	if len(sys.argv) == 1:
		args = parser.parse_args(["-h"])
	else:
		args = parser.parse_args()
	
	# Argument parsing error.
	if hasattr(args, "echo"):
		print(args.echo)
		exit(1)
	
	# Create the result folder
	if not isdir(resultPath):
		mkdir(resultPath)
	
	# TODO: Clean up old builds ?
	
	for a in archetypes:
		buildArchetype(a)
