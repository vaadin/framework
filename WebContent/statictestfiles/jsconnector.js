window.com_vaadin_tests_components_javascriptcomponent_BasicJavaScriptComponent_ExampleWidget = function() {
	var connector = this;
	
	var rootElement = connector.getWidgetElement();
	rootElement.innerHTML = 'Hello world!';
	rootElement.onclick = function() {
		connector.getRpcProxy().onClick("message");
		connector.onclick("another message");
	}
	connector.onStateChange = function() {
		console.log('state change:', this.getState());
	}
}