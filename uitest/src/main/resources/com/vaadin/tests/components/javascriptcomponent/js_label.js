window.com_vaadin_tests_components_javascriptcomponent_JavaScriptPreloading_JsLabel = function() {
	var e = this.getElement();
	
	(function() {
		e.innerHTML = "Widget executed javascript";
	})();
};

(function() {
	window.alert("First");
})();
