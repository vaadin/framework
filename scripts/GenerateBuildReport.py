#coding=UTF-8

from BuildDemos import demos
import argparse, subprocess

parser = argparse.ArgumentParser(description="Build report generator")
parser.add_argument("version", type=str, help="Vaadin version that was just built")
parser.add_argument("deployUrl", type=str, help="Base url of the deployment server")
parser.add_argument("buildResultUrl", type=str, help="URL for the build result page")

args = parser.parse_args()

content = """<html>
<head></head>
<body>
<table>
<tr><td><a href="https://dev.vaadin.com/milestone?action=new">Create milestone for next release</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=closed&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Closed tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&component=Core+Framework&resolution=fixed&milestone=Vaadin {version}&col=id&col=summary&col=component&col=milestone&col=status&col=type">Pending-release tickets with milestone {version}</a></td></tr>
<tr><td><a href="https://dev.vaadin.com/query?status=pending-release&milestone=">Pending-release tickets without milestone</a></td></tr>
<tr><td><a href="apidiff/changes.html">API Diff</a></td></tr>
<tr><td><a href="release-notes/release-notes.html">Release Notes</a></td></tr>
""".format(version=args.version)

try:
	p1 = subprocess.Popen(['find', '.', '-name', '*.java'], stdout=subprocess.PIPE)
	p2 = subprocess.Popen(['xargs', 'egrep', '@since ?$'], stdin=p1.stdout, stdout=subprocess.PIPE)
	missing = subprocess.check_output(['grep', '-v', 'tests'], stdin=p2.stdout)
	content += "<tr><td>Empty @since:<br>\n<pre>%s</pre></td></tr>\n" % (missing)
except subprocess.CalledProcessError as e:
	if e.returncode == 1:
		content += "<tr><td>No empty @since</td></tr>\n"
	else:
		raise e

content += """<tr><td><a href="{url}">Build result page (See test results, pin and tag build and dependencies)</a></td></tr>
</table>
</body>
</html>""".format(url=args.buildResultUrl)

f = open("result/report.html", 'w')
f.write(content)
