#!/usr/bin/python

import sys,string

filename = sys.argv[1]

fin = open(filename, "r")
lines = fin.readlines()
fin.close()

for line in lines:
	fields = string.split(line, "\t")

	if fields[0] != "id":
		ticketid = "<a href=\"http://dev.vaadin.com/ticket/%s\">#%s</a>" % (fields[0],fields[0])
		print "  <li>%s: %s</li>" % (ticketid, fields[1])
