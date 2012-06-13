window.com_vaadin_tests_features_SimpleJavascriptExtensionTest_SimpleJavascriptExtension = function() {
	var state = this.getState();
	var greetBack = this.getRpcProxyFunction('com.vaadin.tests.features.SimpleJavascriptExtensionTest$SimpleJavascriptExtensionServerRpc', 'greet');

	this.registerRpc("com.vaadin.tests.features.SimpleJavascriptExtensionTest.SimpleJavascriptExtensionClientRpc", {
		'greet': function(greeting) {
			var response = window.prompt(state.prefix + greeting);
			if (response !== null) {
				greetBack(response);
			}
		}
	});
}