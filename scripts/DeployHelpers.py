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

# .deploy-url in home folder
deployUrlFile = join(expanduser("~"), ".deploy-url")

# .deploy-credentials in home folder
deployCredFile = join(expanduser("~"), ".deploy-credentials")

# Helper for handling the full deployment
# name should end with .war
def deployWar(warFile, name=None):
	if name is None:
		name = basename(warFile)

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
	return requests.post(headers={"Content-Type" : "application/json"}, **kwargs)

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
	(deployUser, deployPass) = open(deployCredFile).read().strip().split(",")
	return HTTPDigestAuth(deployUser, deployPass)

# Read the deploy url file and return the url
def getUrl():
	return open(deployUrlFile).read().strip()

