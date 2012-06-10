window.com_vaadin_tests_components_javascriptcomponent_BasicJavascriptComponent_ExampleWidget = function() {
	var connector = this;
	
	var rootElement = connector.getWidgetElement();
	rootElement.innerHTML = 'Hello world!';
	rootElement.onclick = function() {
		connector.getRpcProxyFunction("com.vaadin.tests.components.javascriptcomponent.BasicJavascriptComponent$ExampleClickRpc", "onClick")("message");
	}
	connector.onStateChange = function() {
		console.log('state change:', this.getState());
	}
}