window.com_vaadin_tests_extensions_SimpleJavascriptExtensionTest_SimpleJavascriptExtension = function() {
	var state = this.getState();
	var greetBack = this.getRpcProxyFunction('com.vaadin.tests.extensions.SimpleJavascriptExtensionTest$SimpleJavascriptExtensionServerRpc', 'greet');

	this.registerRpc("com.vaadin.tests.extensions.SimpleJavascriptExtensionTest.SimpleJavascriptExtensionClientRpc", {
		'greet': function(greeting) {
			var response = window.prompt(state.prefix + greeting);
			if (response !== null) {
				greetBack(response);
			}
		}
	});
}