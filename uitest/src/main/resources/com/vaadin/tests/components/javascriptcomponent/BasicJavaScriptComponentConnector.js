window.com_vaadin_tests_components_javascriptcomponent_BasicJavaScriptComponent_ExampleWidget = function() {
	var self = this;
	var parentIds = [];
	var connectorId = this.getConnectorId();
	while(connectorId) {
		parentIds.push(connectorId);
		connectorId = this.getParentId(connectorId);
	}
	this.sendDifferentTypeOfData(new Date(123), "a string", 556, true ,{"aString": "value1","anInt":556,"aBoolean":false,"aDate":new Date(111)});
	this.reportParentIds(parentIds);
	this.onStateChange = function() {
		var e = this.getElement();
		
		e.innerHTML = '';

		var row = 1;
		var log = function(text) {
			e.innerHTML = "<div>" + row++ + ". " + text + "</div>" + e.innerHTML;
		}
		
		log("Parent element className: " + this.getElement(this.getParentId()).className);
		
		var messages = this.getState().messages;
		for(var i = 0; i < messages.length; i++) {
			log("State message: " + messages[i]);
		}
		
		var url = this.getState().url;
		log("Url: " + this.translateVaadinUri(url.uRL)); //Strange format, see #9210
	}
	
	this.registerRpc({
		sendRpc: function(message) {
			self.getRpcProxy().sendRpc(message + " processed");
		}
	});
	
	this.onUnregister = function() {
		document.getElementById('RemoveButton').appendChild(document.createTextNode("Don't mess with me"));
	};
	
	this.messageToClient = function(message) {
		this.messageToServer(message + " processed");
	}
}