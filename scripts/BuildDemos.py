#coding=UTF-8

# See BuildArchetypes for details on environment
# BuildDemos needs git in PATH and depends on gitpython library
# gitpython can be installed with python installer script "pip":
# pip install gitpython
#
# Deployment dependency: requests
# pip install requests
# Deploy depends on .deployUrl and .deployCredentials files in home folder

import sys, os, pickle
from os.path import join, isfile
from fnmatch import fnmatch
from xml.etree.ElementTree import ElementTree

# Validated demos. name -> git url
demos = {
	"dashboard" : ("https://github.com/vaadin/dashboard-demo.git","7.7"),
	"parking" : ("https://github.com/vaadin/parking-demo.git", "7.7"),
	"addressbook" : ("https://github.com/vaadin/addressbook.git", "7.7"),
	"grid-gwt" : ("https://github.com/vaadin/grid-gwt.git", "7.7"),
	"sampler" : ("demos/sampler", "7.7")
#	"my-demo" : ("my_demo_url_or_path", "my-demo-dev-branch")
}

status_dump = {"messages": []}

def dump_status(error_occurred):
	status_dump["error"] = error_occurred
	pickle.dump(status_dump, open("result/demo_validation_status.pickle", "wb"))

def log_status(log_string):
	status_dump["messages"].append(log_string)
	print(log_string)

def checkout(folder, url, repoBranch = "master"):
	Repo.clone_from(url, join(resultPath, folder), branch = repoBranch)

if __name__ == "__main__":
	# Do imports.
	try:
		from git import Repo
	except:
		log_status("BuildDemos depends on gitpython. Install it with `pip install gitpython`")
		dump_status(True)
		sys.exit(1)
	from BuildHelpers import updateRepositories, mavenValidate, copyWarFiles, getLogFile, removeDir, getArgs, mavenInstall, resultPath, readPomFile, parser
	from DeployHelpers import deployWar
	# Add command line agrument for ignoring failing demos
	parser.add_argument("--ignore", type=str, help="Ignored demos", default="")
	args = getArgs()
	demosFailed = False
	ignoredDemos = args.ignore.split(",")

	wars = []

	for demo in demos:
		print("Validating demo %s" % (demo))
		try:
			repo = demos[demo]
			if (isinstance(repo, tuple)):
				checkout(demo, repo[0], repo[1])
			else:
				checkout(demo, repo)
			if hasattr(args, "fwRepo") and args.fwRepo is not None:
				updateRepositories(join(resultPath, demo), args.fwRepo)
			if hasattr(args, "pluginRepo") and args.pluginRepo is not None:
				updateRepositories(join(resultPath, demo), args.pluginRepo, postfix="plugin")
			mavenValidate(demo, logFile=getLogFile(demo))
			wars.extend(copyWarFiles(demo))
			log_status("%s demo validation succeeded!" % (demo))
		except Exception as e:
			log_status("%s demo validation failed: %s" % (demo, e))
			if demo not in ignoredDemos:
				demosFailed = True
		except EnvironmentError as e:
			log_status("%s demo validation failed: %s" % (demo, e))
			if demo not in ignoredDemos:
				demosFailed = True
		try:
			removeDir(demo)
		except:
			pass
		print("")

	for war in wars:
		try:
			deployWar(war)
		except Exception as e:
			log_status("War %s failed to deploy: %s" % (war, e))
			demosFailed = True

	if demosFailed:
		dump_status(True)
		sys.exit(1)

	dump_status(False)
