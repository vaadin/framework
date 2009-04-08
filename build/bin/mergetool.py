#!/usr/bin/python

import sys,re,os,string,urllib,httplib

################################################################################
# Configuration
################################################################################

# Determine repository root
pin = os.popen("svn info|grep 'Repository Root'|sed -e 's/^.\+: //'", "r")
REPOSITORY = pin.read().rstrip()+"/"
pin.close()

print "Repository: %s" % (REPOSITORY)

################################################################################
# Parse command-line arguments
################################################################################
def help(exitvalue = 0):
	print "Usage: batchmerge [options] <command>\n"
	print "Options:"
	print "\t-m\tOnly merge. (For 'single' command.)"
	print "\t-c\tOnly commit. (For 'single' command.)"
	print "\t-html\tHTML output. (For 'log' command.)"
	print "\t-author\tHTML output. (Include author in HTML log.)"
	print "\t-milestone <ms>\tList tickets in milestone (For 'log' command.)"
	print "\nCommands:"
	print "massmerge <cfg> <src> [<from>] "
	print "                          - Merges changesets listed in the configuration"
	print "                            file. The file is in svn log (text) format."
	print "                            You can comment out unwanted changesets."
	print "                            Merge is stopped on conflict."
	print "                            If you give the optional <from> parameter,"
	print "                            merge is started from the changeset number. "
	print "single <chg#> <target>    - Merges a single changeset. If -m is given,"
	print "                            only merge is done. If -c is given, only commit is done."
	print "revert                    - Reverts all changes made to repository except"
	print "                            changes in this program and the merge files."
	print "log <cfg> <src> <trg>     - Prints a ChangeLog as it will appear in the"
	print "                            commit log. If -html option is given,"
	print "                            the log is printed in a HTML format"
	print "                            suitable for the Release Notes."
	print "commit <cfg> <src> <trg>  - Commits all changes, except changes to this"
	print "                            program and merge files. The commit log"
	print "                            comment includes list of all changesets"
	print "                            listed in the configuration. The <target>"
	print "                            is the branch name, e.g., \"5.2\".\n"
	print "Common parameters:"
	print "    <cfg>                 - Configuration file (svn text log format)."
	print "    <src>                 - Source branch relative to repository URI."
	print "    <trg>                 - Target branch relative to repository URI."
	print "You must run the command in the root directory of the branch."
	print "The program file contains some basic configuration parameters."
	sys.exit(exitvalue)

################################################################################
# Globals
################################################################################
tickets = {}

################################################################################
# Utility Functions
################################################################################
def command(cmd, dryrun=0):
	print cmd
	if not dryrun:
		if os.system(cmd):
			print "Command failed, exiting."
			sys.exit(1)
	else:
		print "Dry run - not executing."

def listChangedFiles():
	# Get Svn status
	pin = os.popen("svn st", "r")
	lines = pin.readlines()
	pin.close()

	changed  = []
	for line in lines:
		# Remove trailing newline
		line = line.rstrip()
		print line
		
		# Extract the file state and name
		filestate = line[0:2].strip()
		filename  = line[7:].strip()

		# Ignore files in build directory
		if (filename.startswith("build/merge/") \
			or filename.startswith("build/bin/mergetool.py") \
			or filename.startswith("build/testing")) \
			and filestate == "M":
			continue

		# File is changed if it is not local
		if filestate != "?":
			changed.append(filename)

	return changed

# Retrieves ticket summary string with HTTP
# Returns: (summary, milestone)
def fetchSummary(ticketno):
	params = urllib.urlencode({'format': 'tab'})
	conn = httplib.HTTPConnection("dev.itmill.com")
	conn.request("GET", "/ticket/%d?%s" % (ticketno, params) )
	response = conn.getresponse()
	data = response.read()
	conn.close()

	lines = data.split("\n")
	data = reduce(lambda x,y: x+"\n"+y, lines[1:])
	#cols = lines[1].split("\t")

	cols = data.split("\t")

	return (cols[1],cols[8])

# Adds summary to ticket number, unless the context already has it
# Returns: (summary, milestone)
def addSummary(m):
	ticketnum = int(m.group(1))
	context = m.group(2)
	if re.match(" *\(", context):
		# The context already has ticket summary
		return "#%d%s" % (ticketnum, context)

	(summary,milestone) = fetchSummary(ticketnum)

	# Remove possible " quotation from the summary
	if summary.startswith('"'):
		summary = summary.strip('"')
		summary = summary.replace('""', '"')

	# Add summaries to further ticket numbers recursively
	context = re.sub(r'#([0-9]+)(.*)', addSummary, context)

	return "#%s (<i>%s</i>) %s" % (ticketnum, summary, context)

################################################################################
# Change
################################################################################
class Ticket:
	def __init__(self, id, summary=None, milestone=None):
		self.id        = id
		self.summary   = summary
		self.milestone = milestone

	def fetchData(self):
		(summary, milestone) = fetchSummary(self.id)
		self.summary   = summary
		self.milestone = milestone

################################################################################
# Change
################################################################################
class Change:
	def __init__(self, id, undo=0, author=""):
		self.id = id
		self.author = author
		self.comment = ""
		self.undo = undo
		self.tickets = []

	def addCommentLine(self, line):
		self.comment += line

	def merge(self, trunkURI, dryrun=0):
		drycmd = ""
		if dryrun:
			drycmd = "--dry-run"

		# Handle negative merge
		mergesign = ""
		if self.undo:
			mergesign = "-"

		# Build the merge command
		cmd = "svn merge --non-interactive %s -c %s%d %s" % (drycmd, mergesign, self.id, trunkURI)
		print cmd
		
		# Run the merge command
		pin = os.popen(cmd, "r")
		lines = pin.readlines()
		pin.close()

		# Parse the lines for conflicts
		conflicts = 0
		for line in lines:
			print line[:-1]

			# Check for conflict
			if line.startswith("C"):
				conflicts += 1

			# Check for skipped file
			elif line.startswith("Skipped"):
				conflicts += 1

			filename = line[8:-1]

		# Simply exit if there was any problem
		if conflicts > 0:
			print "Problems detected. Exiting."
			sys.exit(1)

	def fetchComment(self, trunkURI):
		cmd = "svn log -r %d %s" % (self.id, trunkURI)
		
		# Run the log command
		pin = os.popen(cmd, "r")
		lines = pin.readlines()
		pin.close()

		STATE_START = 0
		STATE_COMMENT = 1
		comment = None
		state = STATE_START
		for line in lines:
			if state == STATE_START:
				if line == "\n":
					state = STATE_COMMENT
			elif state == STATE_COMMENT:
				if line.startswith("-----------------"):
					self.comment = comment
					return comment
				elif comment:
					comment += "\n" + line.rstrip("\n")
				else:
					comment = line.rstrip("\n")

		self.comment = comment
		return comment

	def commit(self):
		# Write the log comment to a temporary file
		logtmpname = "/tmp/merge-single-%d.log" % (os.getpid())
		fout = open(logtmpname, "w")
		fout.write(self.comment)
		fout.close()

		# Get listo
		files = listChangedFiles()
		if len(files) == 0:
			print "Error: Will not do empty commit."
			sys.exit(1)

		# Write the list of files to be committed to a temporary file
		changedfiles = ("\n".join(files)) + "\n"
		targettmpname = "/tmp/merge-targets-%d.txt" % (os.getpid())
		fout = open(targettmpname, "w")
		fout.write(changedfiles)
		fout.close()
		print changedfiles,

		command("svn commit --file %s --targets %s" % (logtmpname, targettmpname))

		command("rm %s %s" % (logtmpname, targettmpname))


	def getNumber(self):
		return self.id

	def getComment(self):
		return self.comment

	def getUndo(self):
		return self.undo

	def getAuthor(self):
		return self.author

	def isForMilestone(self, milestone):
		return self.author

	def addSummary(self, m, target_milestone=None):
		ticketnum = int(m.group(1))
		context = m.group(2)
		if re.match(" *\(", context):
			# The context already has ticket summary
			return "#%d%s" % (ticketnum, context)

		# Check for cached ticket
		if tickets.has_key(ticketnum):
			summary = tickets[ticketnum].summary
			ticket_milestone = tickets[ticketnum].milestone
		else:
			# Fetch ticket from server and add to cache
			(summary,ticket_milestone) = fetchSummary(ticketnum)
			tickets[ticketnum] = Ticket(ticketnum,summary,ticket_milestone)

		self.tickets.append(ticketnum);

		# Remove possible " quotation from the summary
		if summary.startswith('"'):
			summary = summary.strip('"')
			summary = summary.replace('""', '"')

		ticketnum = "#%s" % (ticketnum)
			
		# Emphasize tickets matching the target milestone
		if target_milestone:
			if ticket_milestone.find(target_milestone) != -1:
				ticketnum = "<b>%s</b>" % (ticketnum)

		# Add summaries to further ticket numbers recursively
		context = re.sub(r'#([0-9]+)(.*)', lambda m: self.addSummary(m, target_milestone=target_milestone), context)

		return "%s (<i>%s</i>) %s" % (ticketnum, summary, context)

	def registerTicket(self, m, ticketNumbers):
		ticketNumbers[int(m.group(1))] = 1
		return ""

	# Returns a list of ticket numbers referenced by this change
	def listTickets(self):
		ticketNumbers = {}
		re.sub(r'#([0-9]+)', lambda m: self.registerTicket(m,ticketNumbers=ticketNumbers), self.comment)
		return ticketNumbers.keys()

################################################################################
# Read configuration file
################################################################################
class Configuration:
	def __init__(self, cfgfilename, startfrom=0):
		self.changes = []
		self.readConfig(cfgfilename, startfrom)

	def readConfig(self, cfgfilename, startfrom=0):
		fin = open(cfgfilename, "r")
		content = fin.readlines()
		fin.close()

		# Parse configuration
		currentChange = None
		skipChange    = 0
		for line in content:
			m_changestart = re.match(r'(-?)r([0-9]+)', line)
			m_endofchange = re.match(r'------+', line)
			m_emptyline = re.match(r'^$', line)
			if m_changestart:
				# Parse negative merge
				undo = 0
				if m_changestart.group(1) == "-":
					undo = 1

				# Get changeset number
				id = int(m_changestart.group(2))
				
				# Skip the target if its number is too small
				if startfrom != 0 and id < startfrom:
					skipChange = 1

				# Get the author
				author = re.sub(r'\@.+', '', line.split("|")[1].strip())

				currentChange = Change(id, undo=undo, author=author)
				
			elif m_endofchange:
				# Register changeset, unless it is marked
				# for skipping.
				if currentChange:
					if skipChange:
						skipChange = 0
					else:
						self.changes.append(currentChange)
					currentChange = None
			elif m_emptyline:
				pass
			else:
				if currentChange:
					currentChange.addCommentLine(line)

	def massMerge(self, trunkURI, dryrun=0, allatonce=0):
		if not allatonce:
			# Merge one changeset at a time
			for change in self.changes:
				change.merge(trunkURI, dryrun=dryrun)
		else:
			# What is the first changeset in the merge?
			smallest = 99999999
			for change in self.changes:
				if change.getNumber() < smallest:
					smallest = change.getNumber()

			drycmd = ""
			if dryrun:
				drycmd = "--dry-run"
				
			# Merge from the first changeset to HEAD
			cmd = "svn merge --non-interactive %s -r %d:HEAD %s" % (drycmd, smallest, trunkURI)
			print cmd
			command(cmd)

	def createLogComment(self):
		# Create a log comment that lists all merged changesets with
		# comments
		logcomment = "Merge from %s to %s:\n" % (sourcebranch, targetbranch)
		for change in self.changes:
			changeno      = change.getNumber()
			changecomment = change.getComment().rstrip("\n")
			if change.getUndo():
				logcomment += "Reverted [%d] merge: %s\n" % (changeno, changecomment)
			else:
				logcomment += "Merged [%d]: %s\n" % (changeno, changecomment)
		return logcomment

	def logHtml(self, author=0, milestone=None):
		# In author inclusion mode, include some styles to make a printout look better
		if author:
			print "<head>\n<style type=\"text/css\">\n"+ \
				  "tr {\n	  vertical-align: top;\n}\ntd {\n	  font-size: 8pt;\n}\n</style>\n</head>\n"
			
		# Print header
		print "<table id=\"changelog-table\">"
		authorcolumnheader = ""
		if author:
			authorcolumnheader = "<td>Author</td>"
		print "  <tr><td>#</td><td>Changeset Comment</td>%s</tr>" % (authorcolumnheader)

		# Print body: the changes
		for change in self.changes:
			changeno      = change.getNumber()
			changecomment = change.getComment().rstrip("\n")
			
			changeref     = "[%d]" % (changeno)

			# Handle merge undo markup
			if change.getUndo():
				changeref     = "<font class=\"changeset-merge-undone\">%s</font>" % (changeref)
				changecomment = "<font class=\"changeset-merge-undone\">%s</font>" % (changecomment)
				changecomment = "Reverted a change: "+changecomment

			authorcolumn = ""
			if author:
				authorcolumn = "<td>%s</td>" % (change.getAuthor())

			# Add ticket summary after ticket number, if missing
			# TODO: this handles only one
			changecomment = re.sub(r'#([0-9]+)(.*)', lambda m: change.addSummary(m, target_milestone=milestone), changecomment, 100)
			# item = re.sub(r'#([0-9]+)(.*)', '#\\1 (SUMMARY) \\2', item)
	
			# Change ticket numbers to references to tickets
			changecomment = re.sub(r'#([0-9]+)', '#<a href="http://dev.itmill.com/ticket/\\1">\\1</a>', changecomment)
	
			# Change changeset numbers to references to changesets
			changecomment = re.sub(r'\[([0-9]+)\]', '[<a href="http://dev.itmill.com/changeset/\\1">\\1</a>]', changecomment)
			changeref     = re.sub(r'\[([0-9]+)\]', '[<a href="http://dev.itmill.com/changeset/\\1">\\1</a>]', changeref)

			# See if any of the tickets have milestone under work.
			if milestone:
				for ticketnum in change.tickets:
					ticket = tickets[ticketnum]
					if ticket.milestone.find(milestone) != -1:
						changeref = "<b>%s</b>" % (changeref)

			# Make basic HTML formatting
			item = "  <tr><td>%s:</td><td>%s</td>%s</tr>" % (changeref, changecomment, authorcolumn)

			print item
			sys.stdout.flush()
		print "</table>"

	# Prints a commit log to standard output
	def log(self, sourcebranch, targetbranch, html=0, author=0, milestone=None):
		if html:
			self.logHtml(author=author,milestone=milestone)
			return
		sys.stdout.write(self.createLogComment())

	def commit(self, sourcebranch, targetbranch, dryrun=0):
		logcomment = self.createLogComment()

		# Write the log comment to a temporary file
		logtmpname = "/tmp/massmerge-pid-%d.log" % (os.getpid())
		fout = open(logtmpname, "w")
		fout.write(logcomment)
		fout.close()

		# Write the list of files to be committed to a temporary file
		changedfiles = "\n".join(listChangedFiles())
		targettmpname = "/tmp/massmerge-targets-%d.txt" % (os.getpid())
		fout = open(targettmpname, "w")
		fout.write(changedfiles)
		fout.close()

		if dryrun:
			print "Log:"
			os.system("cat %s" % (logtmpname))
			print "\nChanged Files:"
			os.system("cat %s" % (targettmpname))
			print "\n"

		command("svn commit --file %s --targets %s" % (logtmpname, targettmpname), dryrun=dryrun)

		command("rm %s %s" % (logtmpname, targettmpname))

	def listTickets(self):
		fixed = {}
		
		for change in self.changes:
			changeno      = change.getNumber()
			changeTickets = change.listTickets()
			if len(changeTickets)>0 and change.comment.lower().find("fix") != -1:
				for ticket in changeTickets:
					fixed[ticket] = 1

		fixedlist = fixed.keys()
		fixedlist.sort()
		# print "Fixed:", fixedlist

		print "<ul>"
		
		for ticketNum in fixedlist:
			if not tickets.has_key(ticketNum):
				ticket = Ticket(ticketNum)
				ticket.fetchData()
				tickets[ticketNum] = ticket
				
			ticket = tickets[ticketNum]
			# print "%d: %s" % (ticket.id, ticket.summary)
			print "  <li><a href=\"http://dev.itmill.com/ticket/%d\">#%d</a>: %s</li>" % (ticket.id, ticket.id, ticket.summary)
			sys.stdout.flush()

		print "</ul>"

################################################################################
# Commands
################################################################################

# Command: revert
def commandRevert():
	# Get Svn status
	pin = os.popen("svn st", "r")
	lines = pin.readlines()
	pin.close()

	reverted = []
	removed  = []
	for line in lines:
		# Remove trailing newline
		line = line[:-1]

		# Extract the file state and name
		filestate = line[0:2].strip()
		filename  = line[7:].strip()

		# Ignore files in build directory
		if (filename.startswith("build/merge/") \
			or filename.startswith("build/bin/")) \
			and filestate == "M":
			continue

		# Added files are simply deleted
		if filestate != "?":
			reverted.append(filename)

			# Added files have to be removed in addition to reverting
			if filestate == "A":
				removed.append(filename)

		# Remove conflict choises
		elif filestate == "?" and \
			 (filename.find(".merge-left.r") != -1 or \
			  filename.find(".merge-right.r") != -1):
			removed.append(filename)

	# Revert files marked for reverting
	donework = 0
	if len(reverted) > 0:
		files = " ".join(reverted)
		command("svn revert -R %s" % (files))
		donework = 1

	# Remove files marked for deletion
	if len(removed) > 0:
		files = " ".join(removed)
		command("rm %s" % (files))
		donework = 1

	if not donework:
		print "Nothing to do."

# Command: massmerge
def commandMassmerge(cfgfilename, sourceuri, startfrom, dryrun=0, allatonce=0):
	cfg = Configuration(cfgfilename, startfrom=startfrom)
	cfg.massMerge(sourceuri, dryrun=dryrun, allatonce=allatonce)

# Command: single
def commandSingle(trunkuri, changeset, sourcebranch, targetbranch, onlymerge=0, onlycommit=0):
	change = Change(changeset)
	print "Found changeset with log comment:\n  "+change.fetchComment(trunkuri) + "\n"

	change.merge(trunkuri, dryrun=onlycommit)
	if onlycommit:
		print "Got file list."
	else:
		print "Merge successful."

	# Change the comment
	change.comment = "Merged [%d] from %s to %s branch: %s" % (change.id, sourcebranch, branchname, change.comment)
	print "\nLog comment: \"%s\"" % (change.comment)
	
	if not onlymerge:
		print "Committing."
		change.commit()

# Command: commit
def commandCommit(cfgfilename, sourcebranch, targetbranch, dryrun=0):
	cfg = Configuration(cfgfilename)
	cfg.commit(sourcebranch, targetbranch, dryrun=dryrun)

# Command: log
def commandLog(cfgfilename, sourcebranch, targetbranch, html=0, author=0, milestone=None):
	cfg = Configuration(cfgfilename)
	cfg.log(sourcebranch, targetbranch, html=html, author=author, milestone=milestone)

# Command: tickets
def commandTickets(cfgfilename):
	cfg = Configuration(cfgfilename)
	cfg.listTickets()

################################################################################
# Main Program
################################################################################

# Handle switches
dryrun = 0
html   = 0
html_author = 0
html_milestone = None
onlymerge = 0
onlycommit = 0
all        = 0
while len(sys.argv)>1 and sys.argv[1][0] == '-':
	if sys.argv[1] == "-d":
		dryrun = 1
		del sys.argv[1:2]

	elif sys.argv[1] == "-html":
		html = 1
		del sys.argv[1:2]

	elif sys.argv[1] == "-author":
		html_author = 1
		del sys.argv[1:2]

	elif sys.argv[1] == "-milestone":
		html_milestone = sys.argv[2]
		del sys.argv[1:3]

	elif sys.argv[1] == "-m":
		onlymerge = 1
		del sys.argv[1:2]

	elif sys.argv[1] == "-c":
		onlycommit = 1
		del sys.argv[1:2]

	elif sys.argv[1] == "-all":
		all = 1
		del sys.argv[1:2]

	else:
		print "Invalid option '%s'." % (sys.argv[1])
		sys.exit(1)

if len(sys.argv) < 2:
	help(1)

# Handle commands
if sys.argv[1] == "revert":
	commandRevert()

elif (len(sys.argv) == 4 or len(sys.argv)==5) and sys.argv[1] == "massmerge":
	cfgfilename = sys.argv[2]
	sourcebranch = sys.argv[3]
	startfrom = None
	if len(sys.argv)>4:
		startfrom = int(sys.argv[4])
	commandMassmerge(cfgfilename, sourceuri=REPOSITORY+sourcebranch, startfrom=startfrom, dryrun=dryrun, allatonce=all)

elif len(sys.argv) == 5 and sys.argv[1] == "single":
	changeset = int(sys.argv[2])
	sourcebranch = sys.argv[3]
	targetbranch = sys.argv[4]
	commandSingle(REPOSITORY+sourcebranch, changeset, targetbranch, onlymerge=onlymerge, onlycommit=onlycommit)

elif len(sys.argv) == 5 and sys.argv[1] == "commit":
	cfgfilename = sys.argv[2]
	sourcebranch = sys.argv[3]
	targetbranch = sys.argv[4]
	commandCommit(cfgfilename, sourcebranch, targetbranch, dryrun=dryrun)

elif len(sys.argv) == 5 and sys.argv[1] == "log":
	cfgfilename = sys.argv[2]
	sourcebranch = sys.argv[3]
	targetbranch = sys.argv[4]
	commandLog(cfgfilename, sourcebranch, targetbranch, html=html, author=html_author, milestone=html_milestone)

elif len(sys.argv) == 3 and sys.argv[1] == "tickets":
	cfgfilename = sys.argv[2]
	commandTickets(cfgfilename)

else:
	help(1)
