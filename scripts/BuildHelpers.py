#coding=UTF-8

## Collection of helpers for Build scripts ##

import sys, argparse, subprocess, platform
from xml.etree import ElementTree
from os.path import join, isdir, isfile, basename, exists
from os import listdir, makedirs
from shutil import copy, rmtree
from glob import glob

# Directory where the resulting war files are stored
# TODO: deploy results
resultPath = join("result", "demos")

if not exists(resultPath):
	makedirs(resultPath)
elif not isdir(resultPath):
	print("Result path is not a directory.")
	sys.exit(1)

args = None

# Default argument parser
parser = argparse.ArgumentParser(description="Automated staging validation")
group = parser.add_mutually_exclusive_group(required=True)
group.add_argument("--version", help="Vaadin version to use")
group.add_argument("--artifactPath", help="Path to local folder with Vaadin artifacts")

parser.add_argument("--maven", help="Additional maven command line parameters", default=None)

# Parse command line arguments <version>
def parseArgs():
	# If no args, give help
	if len(sys.argv) == 1:
		args = parser.parse_args(["-h"])
	else:
		args = parser.parse_args()
	return args

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

mavenCmd = getMavenCommand()

# Get command line arguments. Parses arguments if needed.
def getArgs():
	global args
	if args is None:
		args = parseArgs()
	return args

# Maven Package and Validation
def mavenValidate(artifactId, mvnCmd = mavenCmd, logFile = sys.stdout, version = None, mavenParams = None):
	if version is None:
		version = getArgs().version
	if mavenParams is None:
		mavenParams = getArgs().maven

	print("Do maven clean package validate")
	cmd = [mvnCmd]
	cmd.append("-Dvaadin.version=%s" % (version))
	if mavenParams is not None:
		cmd.extend(mavenParams.strip('"').split(" "))
	cmd.extend(["clean", "package", "validate"])
	print("executing: %s" % (" ".join(cmd)))
	subprocess.check_call(cmd, cwd=join(resultPath, artifactId), stdout=logFile)

# Collect .war files to given folder with given naming
def copyWarFiles(artifactId, resultDir = resultPath, name = None):
	if name is None:
		name = artifactId
	copiedWars = []
	warFiles = glob(join(resultDir, artifactId, "target", "*.war"))
	warFiles.extend(glob(join(resultDir, artifactId, "*", "target", "*.war")))
	for warFile in warFiles:
		if len(warFiles) == 1:
			deployName = "%s.war" % (name)
		else:
			deployName = "%s-%d.war" % (name, warFiles.index(warFile))
		print("Copying .war file %s as %s to result folder" % (basename(warFile), deployName))
		copy(warFile, join(resultDir, deployName))
		copiedWars.append(join(resultDir, deployName))
	return copiedWars

def readPomFile(pomFile):
	# pom.xml namespace workaround
	root = ElementTree.parse(pomFile).getroot()
	nameSpace = root.tag[1:root.tag.index('}')]
	ElementTree.register_namespace('', nameSpace)

	# Read the pom.xml correctly
	return ElementTree.parse(pomFile), nameSpace 

# Recursive pom.xml update script
def updateRepositories(path, repoUrl = None, version = None):
	# If versions are not supplied, parse arguments
	if version is None:
		version = getArgs().version

	# Read pom.xml
	pomXml = join(path, "pom.xml")
	if isfile(pomXml):
		# Read the pom.xml correctly
		tree, nameSpace = readPomFile(pomXml)
		
		# NameSpace needed for finding the repositories node
		repoNode = tree.getroot().find("{%s}repositories" % (nameSpace))
	else:
		return
	
	if repoNode is not None:
		print("Add staging repositories to " + pomXml)
		
		# Add framework staging repository
		addRepo(repoNode, "repository", "vaadin-%s-staging" % (version), repoUrl)
		
		# Find the correct pluginRepositories node
		pluginRepo = tree.getroot().find("{%s}pluginRepositories" % (nameSpace))
		if pluginRepo is None:
			# Add pluginRepositories node if needed
			pluginRepo = ElementTree.SubElement(tree.getroot(), "pluginRepositories")
		
		# Add plugin staging repository
		addRepo(pluginRepo, "pluginRepository", "vaadin-%s-plugin-staging" % (version), repoUrl)
		
		# Overwrite the modified pom.xml
		tree.write(pomXml, encoding='UTF-8')
	
	# Recursive pom.xml search.
	for i in listdir(path):
		file = join(path, i)
		if isdir(file):
			updateRepositories(join(path, i), repoUrl, version)

# Add a repository of repoType to given repoNode with id and URL
def addRepo(repoNode, repoType, id, url):
	newRepo = ElementTree.SubElement(repoNode, repoType)
	idElem = ElementTree.SubElement(newRepo, "id")
	idElem.text = id
	urlElem = ElementTree.SubElement(newRepo, "url")
	urlElem.text = url

# Get a logfile for given artifact
def getLogFile(artifact, resultDir = resultPath):
	return open(join(resultDir, "%s.log" % (artifact)), 'w')

def removeDir(subdir):
	if '..' in subdir or '/' in subdir:
		# Dangerous relative paths.
		return
	rmtree(join(resultPath, subdir))

def mavenInstall(pomFile, jarFile = None, mvnCmd = mavenCmd, logFile = sys.stdout):
	cmd = [mvnCmd, "install:install-file"]
	cmd.append("-Dfile=%s" % (jarFile if jarFile is not None else pomFile))
	cmd.append("-DpomFile=%s" % (pomFile))
	print("executing: %s" % (" ".join(cmd)))
	subprocess.check_call(cmd, stdout=logFile)	
