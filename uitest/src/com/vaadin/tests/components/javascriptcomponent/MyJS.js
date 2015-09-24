window.com_vaadin_tests_components_javascriptcomponent_JavaScriptNoLayoutHandlingUI_MyJsComponent = function() {
	var e = this.getElement();

	this.onStateChange = function() {
		e.innerHTML = "state: "+this.getState().aaa;
	}
}