/*******************************************************************************
 * HubTest-BasicConformance.js:
 *		JavaScript for test case HubTest-BasicConformance.html.
 *
 *		This JavaScript MUST NOT BE CHANGED.
 *
 * Copyright 2007 OpenAjax Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless 
 * required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 *
 ******************************************************************************/

var checkPrefix = HubTest_BasicConformance_MyPrefix;
var publishSubscribeWorking = false;
var markupScannerWorking = false;
var loadHandlerWorking = false;

function TestWasSuccessful(idstring) {
	var elem = document.getElementById(idstring);
	elem.innerHTML = '<span style="color:green">TEST SUCCEEDED!!!</span>';
}

function subscribeTestCB(prefix, name, subscriberData, publisherData) {
	publishSubscribeWorking = true;
}

/* This function updates the HTML DOM based on whether the various test succeeded.
	It is invoked when the document 'load' event is raised. */
function ConformanceChecks() {
	var elem = document.getElementById("LibraryName");
	elem.innerHTML = '<span style="color:green">For library: '+checkPrefix+'</span>';
	if (OpenAjax.libraries[checkPrefix]) {
		TestWasSuccessful("registerLibraryResult");
	}
	if (OpenAjax.globals[checkPrefix]) {
		TestWasSuccessful("registerGlobalsResult");
	}
	if (loadHandlerWorking) {
		TestWasSuccessful("addOnLoadResult");
	}
	OpenAjax.subscribe("foo","bar",subscribeTestCB);
	OpenAjax.publish("foo","bar");
	if (publishSubscribeWorking) {
		TestWasSuccessful("PublishSubscribeResult");
	}
	if (markupScannerWorking) {
		TestWasSuccessful("MarkupScannerResult");
	}
}

/* This logic verifies that the markup scanner is working */
function markupScannerCB(element) {
	markupScannerWorking = true;
}
OpenAjax.registerAttrScanCB("foo", "class", "match", "HubTestResult", markupScannerCB);

