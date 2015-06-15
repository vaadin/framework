#coding=UTF-8

# See BuildArchetypes for details on environment
# BuildDemos needs git in PATH and depends on gitpython library
# gitpython can be installed with python installer script "pip":
# pip install gitpython	

from git import Repo
from BuildHelpers import updateRepositories, mavenValidate, copyWarFiles, VersionObject, getLogFile, parseArgs

## Example of a non-staging test.
#version = VersionObject()
#version.version = "7.4.8"

# Uncomment lines before this, and comment following line to make a non-staging test
version = None

demos = {
	"dashboard" : "https://github.com/vaadin/dashboard-demo.git",
	"parking" : "https://github.com/vaadin/parking-demo.git",
	"addressbook" : "https://github.com/vaadin/addressbook.git",
	"confirmdialog" : "https://github.com/samie/Vaadin-ConfirmDialog.git"
}

def checkout(folder, url):
	Repo.clone_from(url, folder)

if __name__ == "__main__":
	if version is None:
		version = parseArgs()
	for demo in demos:
		print("Validating demo %s" % (demo))
		try:
			checkout(demo, demos[demo])
			updateRepositories(demo, repoIds = version)
			mavenValidate(demo, repoIds = version, logFile = getLogFile(demo))
			copyWarFiles(demo)
			print("%s demo validation succeeded!" % (demo))
		except:
			print("%s demo validation failed" % (demo))
		print("")
