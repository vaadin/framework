#!/usr/bin/python

import os,sys,re

###############################################################################
# Read maps
###############################################################################
def readMap(fin, separator):
    lines = map(lambda x: x.strip(), fin.readlines())

    values = {}
    for line in lines:
        if line == "":
            continue

        m = re.match(separator, line)
        if not m:
            print "Line '%s' not parseable." % line
            sys.exit(1)
        values[m.group(1)] = m.group(2)

    return values

def getSvnInfo():
    if os.path.exists(".svn"):
        pin = os.popen("svn info", "r")
    elif os.path.exists(".git"):
        pin = os.popen("git svn info", "r")
    values = readMap(pin, r'^([^:]+):\s*(.+)$')
    pin.close()
    return values

def readProperties(filename):
    fin = open(filename, "r")
    values = readMap(fin, r'^([^=]+)\s*=\s*(.+)$')
    fin.close()
    return values

###############################################################################
# Help
###############################################################################
def helpAndExit():
    print "Usage: build/bin/tagrelease <command> [parameters...]"
    print "Commands:"
    print "\ttag <version> <changeset> <manual-repository>"
    sys.exit(1)

###############################################################################
# Checks if a tag exists
###############################################################################
def isTagged(trgUrl):
    return not os.system("svn list --depth empty %s 2> /dev/null" % trgUrl)

###############################################################################
# Tag
###############################################################################
def checkNotTagged(tagUrl):
    if isTagged(tagUrl):
        print "The tag '%s' already exists." % tagUrl
        sys.exit(1)

def tag(product, srcUrl, trgUrl, version, changeset, dryrun = 1):
    # Check that the tag doesn't already exist
    checkNotTagged(trgUrl)
    
    tagComment = "Tagged %s %s release." % (product, version)
    tagCmd = "svn copy -m \"%s\" %s %s" % (tagComment, srcUrl+"@"+changeset, trgUrl)
    print tagCmd

    # TODO enable
    # error = os.system(tagCmd)
    error = 0
    if error:
        print "Tagging failed."
        sys.exit(1)

###############################################################################
# Tag command
###############################################################################
def tagCommand(version, changeset, bookRepo):
    # Check parameters
    m = re.match(r'^[0-9]+\.[0-9]+\.[0-9]+(\.\w+)?$', version)
    if not m:
        print "Invalid version number '%s'" % version
        sys.exit(1)
    m = re.match(r'^[0-9]+$', changeset)
    if not m:
        print "Invalid changeset number '%s'" % changeset
        sys.exit(1)

    # Repository parameters
    svnInfo = getSvnInfo()
    url = svnInfo["URL"]
    repoRoot = svnInfo["Repository Root"]
    tagUrl = repoRoot+"/releases/"+version

    # Book tag parameters
    if not re.search(r'branches/[0-9\.]+$', bookRepo):
        print "Bad documentation branch '%s' for release." % (bookRepo)
        sys.exit(1)
    bookTagUrl = repoRoot+"/doc/tags/"+version

    # Check that neither tag exists
    checkNotTagged(tagUrl)
    checkNotTagged(bookTagUrl)

    # Do the tagging
    tag("Vaadin", url, tagUrl, version, changeset)
    tag("Book of Vaadin", bookRepo, bookTagUrl, version, changeset)

###############################################################################
# Verify command
###############################################################################
def verifyCommand(version, changeset):
    # Check parameters
    # TODO Put to a function
    m = re.match(r'^[0-9]+\.[0-9]+\.[0-9]+(\.\w+)?$', version)
    if not m:
        print "Invalid version number '%s'" % version
        sys.exit(1)
    m = re.match(r'^[0-9]+$', changeset)
    if not m:
        print "Invalid changeset number '%s'" % changeset
        sys.exit(1)

    print "Verification not yet implemented, but ok."

###############################################################################
# Main
###############################################################################

if len(sys.argv) < 2:
    helpAndExit()

if sys.argv[1] == "tag" and len(sys.argv) == 5:
    tagCommand(sys.argv[2], sys.argv[3], sys.argv[4])
elif sys.argv[1] == "verify" and len(sys.argv) == 4:
    verifyCommand(sys.argv[2], sys.argv[3])
else:
    print "Invalid command or number of parameters."
    helpAndExit()
