#coding=UTF-8

# See BuildArchetypes for details on environment
# BuildDemos needs git in PATH and depends on gitpython library
# gitpython can be installed with python installer script "pip":
# pip install gitpython	
#
# Deployment dependency: requests
# pip install requests
# Deploy depends on .deployUrl and .deployCredentials files in home folder

import sys
try:
	from git import Repo
except:
	print("BuildDemos depends on gitpython. Install it with `pip install gitpython`")
	sys.exit(1)
from BuildHelpers import updateRepositories, mavenValidate, copyWarFiles, getLogFile, removeDir, getArgs
from DeployHelpers import deployWar

# Validated demos. name -> git url
demos = {
	"dashboard" : "https://github.com/vaadin/dashboard-demo.git",
	"parking" : "https://github.com/vaadin/parking-demo.git",
	"addressbook" : "https://github.com/vaadin/addressbook.git",
	"grid-gwt" : "https://github.com/vaadin/grid-gwt.git"
}

def checkout(folder, url):
	Repo.clone_from(url, folder)

if __name__ == "__main__":
	if getArgs().teamcity:
		print("Add dependency jars from TeamCity here")
	
	demosFailed = False
	
	for demo in demos:
		print("Validating demo %s" % (demo))
		try:
			checkout(demo, demos[demo])
			updateRepositories(demo)
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
