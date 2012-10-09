window.com_vaadin_tests_components_javascriptcomponent_BasicJavaScriptComponent_ExampleWidget = function() {
	var self = this;
	var parentIds = [];
	var connectorId = this.getConnectorId();
	while(connectorId) {
		parentIds.push(connectorId);
		connectorId = this.getParentId(connectorId);
	}
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
		//Strip hostname to make more easily testable
		log("Url: " + this.translateVaadinUri(url.uRL).replace(/http:\/\/[^/]*/, '')); //Strange format, see #9210
	}
	
	this.registerRpc({
		sendRpc: function(message) {
			self.getRpcProxy().sendRpc(message + " processed");
		}
	});
	
	this.messageToClient = function(message) {
		this.messageToServer(message + " processed");
	}
}