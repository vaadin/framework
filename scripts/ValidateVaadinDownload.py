#coding=UTF-8

import argparse, sys
from urllib.request import urlopen

parse = argparse.ArgumentParser(description="Check vaadin.com version lists")
parse.add_argument("version", help="Released Vaadin version number")

args = parse.parse_args()
if hasattr(args, "echo"):
	print(args.echo)
	sys.exit(1)

prerelease = None
(major, minor, maintenance) = args.version.split(".", 2)

if "." in maintenance:
	(maintenance, prerelease) = maintenance.split(".", 1)

# Version without prerelease tag
version = "%s.%s.%s" % (major, minor, maintenance)
isPrerelease = prerelease is not None

failed = False

vaadin7Latest = "http://vaadin.com/download/LATEST7"
vaadin7Versions = "http://vaadin.com/download/VERSIONS_7"
vaadin6Latest = "http://vaadin.com/download/LATEST"
vaadinPrerelease = "http://vaadin.com/download/PRERELEASES"

try:
	latest = urlopen(vaadin7Latest).read().decode().split("\n")
	releaseRow = "release/%s.%s/%s" % (major, minor, version)

	assert (version in latest[0]) ^ isPrerelease, "Latest version mismatch. %s: %s, was: %s" % ("should not be" if isPrerelease else "should be", args.version, latest[0])
	assert (releaseRow in latest[1]) ^ isPrerelease, "Release row mismatch; %s: %s, was %s" % ("should not be" if isPrerelease else "should be", releaseRow, latest[1])
except Exception as e:
	failed = True
	print("Latest version was not correctly updated: %s" % (e))

try:
	assert "%s," % (args.version) in urlopen(vaadin7Versions).read().decode().split("\n"), "Released version not in version list"
except Exception as e:
	if isPrerelease:
		print("Prerelease version needs to be added manually to versions!")
	else:
		failed = True
		print(e)

try:
	latest = urlopen(vaadin6Latest).read().decode().split("\n")
	releaseRow = "release/6.8/6.8."

	assert ("6.8." in latest[0]), "Latest version mismatch; should be: %sX, was: %s" % ("6.8.", latest[0])
	assert (releaseRow in latest[1]), "Release row mismatch; should be: %sX, was %s" % (releaseRow, latest[1])
except Exception as e:
	failed = True
	print("Latest Vaadin 6 version was updated by release. %s" % (e))

try:
	latest = urlopen(vaadinPrerelease).read().decode().split("\n")
	assert (args.version in latest[0]) or not isPrerelease, "%s: %s, was: %s" % ("should be", args.version, latest[0])
except Exception as e:
	print("Prerelease file was not correctly updated: %s" % (e))

sys.exit(1 if failed else 0)
