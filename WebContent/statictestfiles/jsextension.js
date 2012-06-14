window.com_vaadin_tests_extensions_SimpleJavaScriptExtensionTest_SimpleJavascriptExtension = function() {
	var state = this.getState();
	var greetBack = this.getRpcProxyFunction('com.vaadin.tests.extensions.SimpleJavaScriptExtensionTest$SimpleJavaScriptExtensionServerRpc', 'greet');

	this.registerRpc("com.vaadin.tests.extensions.SimpleJavaScriptExtensionTest.SimpleJavaScriptExtensionClientRpc", {
		'greet': function(greeting) {
			var response = window.prompt(state.prefix + greeting);
			if (response !== null) {
				greetBack(response);
			}
		}
	});
}