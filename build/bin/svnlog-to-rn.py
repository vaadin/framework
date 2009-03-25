#!/usr/bin/python
################################################################################
# SVN Log to ChangeLog generator for Release Notes
#
# Generates list of changes in HTML for ChangeLog
# from SVN Log in XML format. You typically generate the log with
# a command such as:
#     svn log -v -r 1234:HEAD > svnlog-1234:HEAD.log.xml
# The command must be executed in the root directory of Toolkit project,
# either in the trunk or in the proper branch. The converter is then
# used as follows:
#     ./build/bin/svnlog-to-rn.py svnlog-1234:HEAD.log.xml
#
# The ChangeLog generator will strip away any merges that begin with
# "Merged [...] from trunk to x.x branch."
#
# The generator will handle the following markup:
#   - Changeset tags such as [1234] to links to dev.itmill.com/changeset/1234
#   - Ticket references such as #1234 to links to dev.itmill.com/ticket/1234
#   - If ticket reference does not have explanation in parentheses,
#     the script will fetch the summary of the ticket from Trac and
#     add it in parentheses after the reference, such as:
#     "fixes #1234 (A nasty bug I found)".
#
# Requirements:
#   - Xalan
################################################################################

import sys,re,os,httplib,urllib

################################################################################
# Convert XML to XHTML
#  - The transformation includes various relevent information
#    and does basic formatting
################################################################################

# Determine path to XSLT file
pathToScript = sys.argv[0]
sloc = pathToScript.rfind("/")
pathToScript = pathToScript[:sloc]

if len(sys.argv) != 2:
    print "Usage: svnlog-to-rn.py <logfile.xml>"
    print "Read the svnlog-to-rn.py header for more info."
    sys.exit(1)

# Open Xalan 
filename = sys.argv[1]
fin = open(filename, "r")
(pout,pin) = os.popen2("xalan -xsl %s/svnlog-to-rn.xsl" % (pathToScript))

# Preprocessing before feeding to XSLT Processor
lines = fin.readlines()
out = ""
for line in lines:
	if line.find("action") != -1:
		line = line.replace(r'>[^<]+/', '')
	#print line,
	pout.write(line)
pout.close()

################################################################################
# Helper functions for postprocessing
################################################################################

# Retrieves summary string with HTTP
def fetchSummary(ticketno):
	params = urllib.urlencode({'format': 'tab'})
	conn = httplib.HTTPConnection("dev.itmill.com")
	conn.request("GET", "/ticket/%d?%s" % (ticketno, params) )
	response = conn.getresponse()
	data = response.read()
	conn.close()

	lines = data.split("\n")
	cols = lines[1].split("\t")
	return cols[1]

# Adds summary to ticket number, unless the context already has it
def addSummary(m):
	ticketnum = int(m.group(1))
	context = m.group(2)
	if re.match(" *\(", context):
		# The context already has ticket summary
		return "#%d%s" % (ticketnum, context)

	summary = fetchSummary(ticketnum)

	return "#%s (<i>%s</i>) %s" % (ticketnum, summary, context)

################################################################################
# Postprocessing for XSLT output
################################################################################

lines = pin.readlines()
for line in lines:
	# Add ticket summary after ticket number, if missing
	line = re.sub(r'#([0-9]+)(.*)', addSummary, line)
	
	# Change ticket numbers to references to tickets
	line = re.sub(r'#([0-9]+)', '#<a href="http://dev.itmill.com/ticket/\\1">\\1</a>', line)
	
	# Change changeset numbers to references to changesets
	#line = re.sub(r'\[([0-9]+)\]', '[<a href="http://dev.itmill.com/changeset/\\1">\\1</a>]', line)

	# Remove prefix about merging
	line = re.sub(r'Merged.+from trunk to [0-9]+.[0-9]+ branch: ', '', line)
	
	print line,
