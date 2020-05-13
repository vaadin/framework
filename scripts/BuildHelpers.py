#coding=UTF-8

## Collection of helpers for Build scripts ##

import sys, argparse, subprocess, platform
from xml.etree import ElementTree
from os.path import join, isdir, isfile, basename, exists
from os import listdir, makedirs
from shutil import copy, rmtree
from glob import glob

# Directory where the resulting war files are stored
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

parser.add_argument("--maven", help="Additional maven command line parameters", default=None)
parser.add_argument("--fwRepo", help="Framework staging repository URL", default=None)
parser.add_argument("--pluginRepo", help="Maven plugin repository URL", default=None)

# Parse command line arguments <version>
def parseArgs():
	# If no args, give help
	if len(sys.argv) == 1:
		args = parser.parse_args(["-h"])
	else:
		args = parser.parse_args()
	return args

# Function for determining the path for an executable
def getCommand(command):
	# This method uses .split("\n")[0] which basically chooses the first result where/which returns.
	# Fixes the case with multiple maven installations available on PATH
	if platform.system() == "Windows":
		try:
			return subprocess.check_output(["where", "%s.cmd" % (command)], universal_newlines=True).split("\n")[0]
		except:
			try:
				return subprocess.check_output(["where", "%s.bat" % (command)], universal_newlines=True).split("\n")[0]
			except:
				print("Unable to locate command %s with where. Is it in your PATH?" % (command))
	else:
		try:
			return subprocess.check_output(["which", command], universal_newlines=True).split("\n")[0]
		except:
			print("Unable to locate command %s with which. Is it in your PATH?" % (command))
	return None

mavenCmd = getCommand("mvn")
dockerCmd = getCommand("docker")

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
	# Enforcer does not always seem to take vaadin.version into account, skip until this can be resolved
	cmd.append("-Denforcer.skip=true")
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

# Generates and modifies a maven pom file
def generateArchetype(archetype, artifactId, repo, logFile, group="testpkg", archetypeGroup="com.vaadin"):
	# Generate the required command line for archetype generation
	args = getArgs()
	print("Parameters for archetype %s:" % (archetype))
	print("using version %s" % (args.version))
	cmd = [mavenCmd, "archetype:generate"]
	cmd.append("-DarchetypeGroupId=%s" % (archetypeGroup))
	cmd.append("-DarchetypeArtifactId=%s" % (archetype))
	cmd.append("-DarchetypeVersion=%s" % (args.version))
	if repo is not None:
		cmd.append("-DarchetypeRepository=%s" % repo)
		print("using repository %s" % (repo))
	else:
	    print("using no repository")
	cmd.append("-DgroupId=%s" % (group))
	cmd.append("-DartifactId=%s" % (artifactId))
	cmd.append("-Dversion=1.0-SNAPSHOT")
	cmd.append("-DinteractiveMode=false")
	if hasattr(args, "maven") and args.maven is not None:
		cmd.extend(args.maven.strip('"').split(" "))

	# Generate pom.xml
	print("Generating archetype %s" % (archetype))
	subprocess.check_call(cmd, cwd=resultPath, stdout=logFile)

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

def dockerWrap(imageVersion, imageName = "demo-validation"):
	dockerFileContent = """FROM jtomass/alpine-jre-bash:latest
LABEL maintainer="FrameworkTeam"

COPY ./*.war /var/lib/jetty/webapps/
USER root
RUN mkdir /opt
RUN chown -R jetty:jetty /opt
COPY ./index-generate.sh /opt/
RUN chmod +x /opt/index-generate.sh

USER jetty
RUN /opt/index-generate.sh

RUN mkdir -p /var/lib/jetty/webapps/root && \
    cp /opt/index.html /var/lib/jetty/webapps/root && \
    chmod 644 /var/lib/jetty/webapps/root/index.html

EXPOSE 8080
"""
	indexGenerateScript = """#!/bin/ash

wars="/var/lib/jetty/webapps"
OUTPUT="/opt/index.html"

echo "<UL>" > $OUTPUT
cd $wars
for war in `ls -1 *.war`; do
  nowar=`echo "$war" | sed -e 's/\(^.*\)\(.war$\)/\\1/'`
  echo "<LI><a href=\"/$nowar/\">$nowar</a></LI>" >> $OUTPUT
done
echo "</UL>" >> $OUTPUT
"""
	with open(join(resultPath, "Dockerfile"), "w") as dockerFile:
		dockerFile.write(dockerFileContent)
	with open(join(resultPath, "index-generate.sh"), "w") as indexScript:
		indexScript.write(indexGenerateScript)
	# build image
	cmd = [dockerCmd, "build", "-t", "%s:%s" % (imageName, imageVersion), resultPath]
	subprocess.check_call(cmd)
	# save to tgz
	cmd = [dockerCmd, "save", imageName]
	dockerSave = subprocess.Popen(cmd, stdout=subprocess.PIPE)
	subprocess.check_call(["gzip"], stdin=dockerSave.stdout, stdout=open(join(resultPath, "%s-%s.tgz" % (imageName, imageVersion)), "w"))
	dockerSave.wait()
	# delete from docker
	cmd = [dockerCmd, "rmi", "%s:%s" % (imageName, imageVersion)]
	subprocess.check_call(cmd)
