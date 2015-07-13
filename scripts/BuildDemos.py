#coding=UTF-8

# See BuildArchetypes for details on environment
# BuildDemos needs git in PATH and depends on gitpython library
# gitpython can be installed with python installer script "pip":
# pip install gitpython	
#
# Deployment dependency: requests
# pip install requests
# Deploy depends on .deployUrl and .deployCredentials files in home folder

import sys, os
from os.path import join, isfile
from fnmatch import fnmatch
from xml.etree.ElementTree import ElementTree

# Validated demos. name -> git url
demos = {
	"dashboard" : "https://github.com/vaadin/dashboard-demo.git",
	"parking" : "https://github.com/vaadin/parking-demo.git",
	"addressbook" : "https://github.com/vaadin/addressbook.git",
	"grid-gwt" : "https://github.com/vaadin/grid-gwt.git"
}

def checkout(folder, url):
	Repo.clone_from(url, join(resultPath, folder))

if __name__ == "__main__":
	# Do imports.	
	try:
		from git import Repo
	except:
		print("BuildDemos depends on gitpython. Install it with `pip install gitpython`")
		sys.exit(1)
	from BuildHelpers import updateRepositories, mavenValidate, copyWarFiles, getLogFile, removeDir, getArgs, mavenInstall, resultPath, readPomFile, parser
	from DeployHelpers import deployWar

	# Add command line arguments for staging repos
	parser.add_argument("--repo", type=str, help="Staging repository URL", default=None)

	args = getArgs()
	if hasattr(args, "artifactPath") and args.artifactPath is not None:
		version = False
		basePath = args.artifactPath
		poms = []
		for root, dirs, files in os.walk(basePath):
			for name in files:
				if fnmatch(name, "*.pom"):
					poms.append(join(root, name))
		for pom in poms:
			jarFile = pom.replace(".pom", ".jar")
			if isfile(jarFile):
				mavenInstall(pom, jarFile)
			else:
				mavenInstall(pom)
			if "vaadin-server" in pom:
				pomXml, nameSpace = readPomFile(pom)
				for version in pomXml.getroot().findall("./{%s}version" % (nameSpace)):
					args.version = version.text
	demosFailed = False
	
	for demo in demos:
		print("Validating demo %s" % (demo))
		try:
			checkout(demo, demos[demo])
			if hasattr(args, "repo") and args.repo is not None:
				updateRepositories(join(resultPath, demo), args.repo)
			mavenValidate(demo, logFile=getLogFile(demo))
			resultWars = copyWarFiles(demo)
			for war in resultWars:
				try:
					deployWar(war)
				except Exception as e:
					print("War %s failed to deploy: %s" % (war, e))
					demosFailed = True
			print("%s demo validation succeeded!" % (demo))
		except Exception as e:
			print("%s demo validation failed: %s" % (demo, e))
			demosFailed = True
		removeDir(demo)
		print("")
	if demosFailed:
		sys.exit(1)
