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
	"dashboard" : "https://github.com/vaadin/dashboard-demo.git",
	"addressbook" : "https://github.com/vaadin/addressbook.git",
	"framework8-demo" : "https://github.com/vaadin/framework8-demo",
	"sampler" : "demos/sampler"
#	"my-demo" : ("my_demo_url_or_path", "my-demo-dev-branch")
}

# List of built archetypes
archetypes = [
	"vaadin-archetype-widget",
	"vaadin-archetype-application",
	"vaadin-archetype-application-example",
	"vaadin-archetype-application-multimodule"
]

status_dump = {"messages": []}

def dump_status(error_occurred):
	status_dump["error"] = error_occurred
	pickle.dump(status_dump, open("result/demo_validation_status.pickle", "wb"))

def log_status(log_string):
	status_dump["messages"].append(log_string)
	print(log_string)
	sys.stdout.flush()

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
	from BuildHelpers import mavenValidate, copyWarFiles, getLogFile, removeDir, getArgs, resultPath, parser, dockerWrap, generateArchetype
	from DeployHelpers import deployWar
	# Add command line agrument for ignoring failing demos
	parser.add_argument("--ignore", type=str, help="Ignored demos", default="")

	# Control to skip demos and archetypes
	parser.add_argument("--skipDemos", action="store_true", help="Skip building demos")
	parser.add_argument("--skipArchetypes", action="store_true", help="Skip building archetypes")

	args = getArgs()
	demosFailed = False
	ignoredDemos = args.ignore.split(",")
	wars = []

	if not args.skipDemos:
		for demo in demos:
			print("Validating demo %s" % (demo))
			try:
				repo = demos[demo]
				if (isinstance(repo, tuple)):
					checkout(demo, repo[0], repo[1])
				else:
					checkout(demo, repo)
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
			log_status("")

	if not args.skipArchetypes:
		for archetype in archetypes:
			artifactId = "test-%s-%s" % (archetype, args.version.replace(".", "-"))
			try:
				log = getLogFile(archetype)
				generateArchetype(archetype, artifactId, args.pluginRepo, log)
				mavenValidate(artifactId, logFile=log)
				wars.extend(copyWarFiles(artifactId, name=archetype))
				log_status("%s validation succeeded!" % (archetype))
			except Exception as e:
				print("Archetype %s build failed:" % (archetype), e)
				if archetype not in ignoredDemos:
					demosFailed = True
	
			try:
				removeDir(artifactId)
			except:
				pass
			log_status("")

	if args.deploy_mode:
		for war in wars:
			try:
				deployWar(war)
			except Exception as e:
				log_status("War %s failed to deploy: %s" % (war, e))
				demosFailed = True
	else:
		dockerWrap(args.version)


	if demosFailed:
		dump_status(True)
		sys.exit(1)

	dump_status(False)
