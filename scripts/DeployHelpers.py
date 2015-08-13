#coding=UTF-8

### Helper class for wildfly deployments. ###
# Related files $HOME/.deploy-url $HOME/.deploy-credentials

import sys, json
try:
	import requests
except Exception as e:
	print("DeployHelpers depends on requests library. Install it with `pip install requests`")
	sys.exit(1)
from requests.auth import HTTPDigestAuth
from os.path import join, expanduser, basename
from BuildHelpers import parser, getArgs
from time import sleep

parser.add_argument("--deployUrl", help="Wildfly management URL")
parser.add_argument("--deployUser", help="Deployment user", default=None)
parser.add_argument("--deployPass", help="Deployment password", default=None)

serverUp = None

def testServer():
	global serverUp

	if serverUp is not None:
		return serverUp

	print("Checking server status")
	i = 0
	request = {"operation" : "read-attribute", "name" : "server-state"}
	serverUp = False
	while not serverUp and i < 2:
		try:
			print("Trying on url %s" % (getUrl()))
			result = doPostJson(url=getUrl(), auth=getAuth(), data=json.dumps(request))
			response = result.json()
			if "outcome" not in response or response["outcome"] != "success":
				# Failure
				raise Exception(response)
			elif "result" not in response or response["result"] != "running":
				# Another failure
				raise Exception(response)
			# All OK
			serverUp = True
			print("Got server connection.")
		except Exception as e:
			print("Exception while checking server state: ", e)
			print("Server connection failed, retrying in 5 seconds.")
			i = i + 1
			sleep(5)
	return serverUp

# Helper for handling the full deployment
# name should end with .war
def deployWar(warFile, name=None):
	if not testServer():
		raise Exception("Server not up. Skipping deployment.")
		return
	if name is None:
		name = basename(warFile).replace('.war', "-%s.war" % (getArgs().version.split('-')[0]))

	print("Deploying to context %s" % (name[:-4]))
	# Undeploy/Remove old version if needed
	if deploymentExists(name):
		removeDeployment(name)
	# Do upload war file
	hash = doUploadWarFile(warFile)
	# Do deployment under name
	doDeploy(hash, name)

def deploymentExists(name):
	# Deployment existence check data
	data = {"operation" : "read-attribute", "name": "runtime-name", "address": [{"deployment" : name}]}
	result = doPostJson(url=getUrl(), auth=getAuth(), data=json.dumps(data))
	return result.json()["outcome"] == "success"

def doDeploy(hash, name):
	# Deployment data
	data = {}
	data["content"] = [{"hash" : hash}]
	data["address"] = [{"deployment" : name}]
	data["operation"] = "add"
	data["enabled"] = True
	return doPostJson(data=json.dumps(data), auth=getAuth(), url=getUrl())

# Helper for adding Content-Type to headers
def doPostJson(**kwargs):
	r = requests.post(headers={"Content-Type" : "application/json"}, **kwargs)
	# Wildfly gives code 500 when asking for a non-existent deployment
	if r.status_code == requests.codes.ok or r.status_code == 500:
		return r
	r.raise_for_status()

def doUploadWarFile(warFile):
	# Upload request, just see the outcome
	result = requests.post("%s/add-content" % (getUrl()), files={"file" : open(warFile, 'rb')}, auth=getAuth()).json()
	if "outcome" not in result or result["outcome"] != "success":
		raise Exception("File upload failed.", result)
	return result["result"]

# Method for removing an existing deployment
def removeDeployment(name):
	data = {}
	data["address"] = [{"deployment" : name}]
	for i in ["undeploy", "remove"]:
		print("%s old deployment of %s" % (i, name))
		data["operation"] = i
		doPostJson(data=json.dumps(data), auth=getAuth(), url=getUrl())

# Read credentials file and return a HTTPDigestAuth object
def getAuth():
	args = getArgs()
	return HTTPDigestAuth(args.deployUser, args.deployPass)

# Read the deploy url file and return the url
def getUrl():
	return getArgs().deployUrl
	
