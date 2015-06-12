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

import subprocess
from BuildHelpers import mavenValidate, copyWarFiles, repo, getLogFile, mavenCmd, updateRepositories, getArgs, removeDir
from DeployHelpers import deployWar

## DEFAULT VARIABLES ##

# ArchetypeGroupId
archetypeGroup = "com.vaadin"

# List of built archetypes
archetypes = [
	"vaadin-archetype-widget", 
	"vaadin-archetype-application", 
	"vaadin-archetype-application-example",
	"vaadin-archetype-application-multimodule"
]

# Maven GroupID
group = "testpkg"

log = None
args = None

## BUILDING METHODS ##

# Generates and modifies a maven pom file
def generateArchetype(archetype):
	artifactId = "test-%s-%s" % (archetype, args.version.replace(".", "-"))

	# Generate the required command line for archetype generation
	cmd = [mavenCmd, "archetype:generate"]
	cmd.append("-DarchetypeGroupId=%s" % (archetypeGroup))
	cmd.append("-DarchetypeArtifactId=%s" % (archetype))
	cmd.append("-DarchetypeVersion=%s" % (args.version))
	if hasattr(args, "archetype") and args.archetype != None:
		cmd.append("-DarchetypeRepository=%s" % (repo % (args.archetype)))
	cmd.append("-DgroupId=%s" % (group))
	cmd.append("-DartifactId=%s" % (artifactId))
	cmd.append("-Dversion=1.0-SNAPSHOT")
	cmd.append("-DinteractiveMode=false")
	
	# Generate pom.xml
	print("Generating pom.xml for archetype %s" % (archetype))
	subprocess.check_call(cmd, stdout=log)
	
	# Return the artifactId so we know the name in the future
	return artifactId

## DO THIS IF RUN AS A SCRIPT (not import) ##
if __name__ == "__main__":
	args = getArgs()
	for archetype in archetypes:
		try:
			log = getLogFile(archetype)
			artifactId = generateArchetype(archetype)
			updateRepositories(artifactId)
			mavenValidate(artifactId, logFile=log)	
			warFiles = copyWarFiles(artifactId, name=archetype)
			for war in warFiles:
				try:
					deployWar(war, "%s.war" % (archetype.split("-", 2)[2]))
				except Exception as e:
					print("War %s failed to deploy: %s" % (war, e))
		except:
			print("Archetype %s build failed" % (archetype))
		removeDir(artifactId)
		print("")
