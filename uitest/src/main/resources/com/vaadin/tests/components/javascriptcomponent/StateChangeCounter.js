window.com_vaadin_tests_components_javascriptcomponent_StateChangeCounter_StateChangeCounterComponent = function() {
	var self = this;
	
	var logRow = function(text) {
		var child = document.createElement("div");
		child.className="logRow";
		child.textContent = text;
		self.getElement().appendChild(child);
	}
	
	this.onStateChange = function() {
		logRow("State change, counter = " + this.getState().stateCounter);
	}
	
	this.sendRpc = function() {
		logRow("RPC")
	}
}