#!/usr/bin/python

import os,os.path,sys

################################################################################
ZIP_DIR = "download/book-of-vaadin/vaadin-7"
ZIP_FILE = ZIP_DIR + "/book-of-vaadin.zip"
TMP_DIR = ZIP_DIR + "/tmp"
################################################################################

def command(cmd):
    result = os.system(cmd)
    if result:
        print "Command failed with result %d: %s" % (result, cmd)
        sys.exit(1)

print "Creating temporary directory..."
command("rm -rf %s/tmp" % (ZIP_DIR))
command("mkdir -p %s" % (TMP_DIR))

print "Unpackaging docs package..."
command("unzip -q -d %s %s" % (TMP_DIR, ZIP_FILE))

print "Replacing documentation..."
for folder in ["html", "tutorial", "pdf"]:
    if os.path.exists(TMP_DIR + "/" + folder):
        print "Replacing '%s' directory..." % (folder)
        command("rm -rf %s/%s" % (ZIP_DIR, folder))
        command("mv %s/%s %s" % (TMP_DIR, folder, ZIP_DIR))

# Clean-up
command("rm -rf %s/tmp" % (ZIP_DIR))

print "Done - documentation published."
