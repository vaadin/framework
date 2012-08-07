window.com_vaadin_tests_extensions_SimpleJavaScriptExtensionTest_SimpleJavascriptExtension = function() {
	var self = this;
	var state = this.getState();
	
	//var rpc = this.getRpcProxy("com.vaadin.tests.extensions.SimpleJavaScriptExtensionTest.SimpleJavaScriptExtensionServerRpc");
	var rpc = this.getRpcProxy();

//	this.registerRpc("com.vaadin.tests.extensions.SimpleJavaScriptExtensionTest.SimpleJavaScriptExtensionClientRpc", {
	this.registerRpc({
		'greet': function(greeting) {
			var response = window.prompt(state.prefix + greeting);
			if (response !== null) {
				rpc.greet(response);
			}
		}
	});
	
	this.greetToClient = function(greeting) {
		var response = window.prompt(state.prefix + greeting);
		if (response !== null) {
			self.greetToServer(response);
		}
	} 
}