#!/usr/bin/python

import sys,os,re
from sets import Set

################################################################################
# Configuration
################################################################################
downloadsite = "http://vaadin.com/download"
latestfile   = "/LATEST"

################################################################################
# Utility Functions
################################################################################
def command(cmd, dryrun=0):
	if not dryrun:
		if os.system(cmd):
			print "Command '%s' failed, exiting." % (cmd)
			sys.exit(1)
	else:
		print "Dry run - not executing."

################################################################################
# List files in an archive.
################################################################################
def listfiles(archive):
	pin = os.popen("tar ztf %s | sort" % (archive), "r")
	files = map(lambda x: x.strip(), pin.readlines())
	pin.close()

	cleanedfiles = []
	for file in files:
		# Remove archive file name from the file names
		slashpos = file.find("/")
		if slashpos != -1:
			cleanedname = file[slashpos+1:]
		else:
			cleanedname = file

		# Purge GWT compilation files.
		if cleanedname.find(".cache.html") != -1:
			continue
		
		cleanedfiles.append(cleanedname)

	return cleanedfiles

# For Zip archives in Vaadin 6.3.0
def listZipFiles(archive):
    pin = os.popen("unzip -l -qq %s | cut -c 29- | sort" % (archive), "r")
    files = map(lambda x: x.strip(), pin.readlines())
    pin.close()

    cleanedfiles = []
    for file in files:
        # Remove archive file name from the file names
        slashpos = file.find("/")
        if slashpos != -1:
            cleanedname = file[slashpos+1:]
        else:
            cleanedname = file

        # Purge GWT compilation files.
        if cleanedname.find(".cache.html") != -1:
            continue
        
        cleanedfiles.append(cleanedname)

    return cleanedfiles

################################################################################
# Difference of two lists of files
################################################################################
def diffFiles(a, b):
	diff = Set(a).difference(Set(b))
	difffiles = []
	for item in diff:
		difffiles.append(item)
	difffiles.sort()
	return difffiles

################################################################################
# Lists files inside a Zip file (a JAR)
################################################################################
def listJarFiles(jarfile):
	# Read the jar content listing
	pin = os.popen("unzip -ql %s" % jarfile, "r")
	lines = map(lambda x: x[:-1], pin.readlines())
	pin.close()

	# Determine the position of file names
	namepos = lines[0].find("Name")
	files = []
	for i in xrange(2, len(lines)-2):
		filename = lines[i][namepos:]
		files.append(filename)

	return files

################################################################################
# Lists files inside a Vaadin Jar inside a Tar
################################################################################
# For Vaadin 6.2 Tar
def listTarVaadinJarFiles(tarfile, vaadinversion):
	jarfile = "vaadin-linux-%s/WebContent/vaadin-%s.jar" % (vaadinversion, vaadinversion)
	extractedjar = "/tmp/vaadinjar-tmp-%d.jar" % (os.getpid())
	tarcmd = "tar zOxf %s %s > %s " % (tarfile, jarfile, extractedjar)
	command (tarcmd)
	files = listJarFiles(extractedjar)
	command ("rm %s" % (extractedjar))
	return files

# For Vaadin 6.3 Zip
def listZipVaadinJarFiles(zipfile, vaadinversion):
    jarfile = "vaadin-%s/WebContent/vaadin-%s.jar" % (vaadinversion, vaadinversion)
    extractedjar = "/tmp/vaadinjar-tmp-%d.jar" % (os.getpid())
    tarcmd = "unzip -p %s %s > %s " % (zipfile, jarfile, extractedjar)
    command (tarcmd)
    files = listJarFiles(extractedjar)
    command ("rm %s" % (extractedjar))
    return files

################################################################################
#
################################################################################

# Download the installation package of the latest version
wgetcmd = "wget -q -O - %s" % (downloadsite+latestfile)
pin = os.popen(wgetcmd, "r")
latestdata = pin.readlines()
pin.close()

latestversion  = latestdata[0].strip()
latestpath     = latestdata[1].strip()
latestURL      = downloadsite + "/" + latestpath + "/"

filename  = "vaadin-%s.tar.gz" % (latestversion)
package   = latestURL + filename
localpackage = "/tmp/%s" % (filename)

print "Latest version:      %s" % (latestversion)
print "Latest version path: %s" % (latestpath)
print "Latest version URL:  %s" % (latestURL)

# Check if it already exists
try:
	os.stat(localpackage)
	print "Latest package already exists in %s" % (localpackage)
	# File exists
except OSError:
	# File does not exist, get it.
	print "Downloading package %s to %s" % (package, localpackage)
	wgetcmd = "wget -q -O %s %s" % (localpackage, package)
	command (wgetcmd)

# List files in latest version.
latestfiles  = listfiles(localpackage)

# List files in built version.
builtversion = sys.argv[1]
builtpackage = "build/result/vaadin-%s.zip" % (builtversion)
builtfiles = listZipFiles(builtpackage)

# Report differences

print "\n--------------------------------------------------------------------------------\nVaadin TAR differences"

# New files
newfiles = diffFiles(builtfiles, latestfiles)
print "\n%d new files:" % (len(newfiles))
for item in newfiles:
	print item

# Removed files
removed = diffFiles(latestfiles, builtfiles)
print "\n%d removed files:" % (len(removed))
for item in removed:
	print item

print "\n--------------------------------------------------------------------------------\nVaadin JAR differences"

latestJarFiles = listTarVaadinJarFiles(localpackage, latestversion)
builtJarFiles  = listZipVaadinJarFiles(builtpackage,      builtversion)

# New files
newfiles = diffFiles(builtJarFiles, latestJarFiles)
print "\n%d new files:" % (len(newfiles))
for item in newfiles:
	print item

# Removed files
removed = diffFiles(latestJarFiles, builtJarFiles)
print "\n%d removed files:" % (len(removed))
for item in removed:
	print item

# Purge downloaded package
command("rm %s" % (localpackage))
