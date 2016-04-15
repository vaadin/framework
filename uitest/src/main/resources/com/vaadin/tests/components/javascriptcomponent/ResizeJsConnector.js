function com_vaadin_tests_components_javascriptcomponent_JavaScriptResizeListener_ResizeJsComponent() {
	var self = this;
	var e = this.getElement();
	
	var setText = function(text) {
		e.innerHTML = text;
	}
	setText('Initial state');
	
	var resizeListener = function(event) {
		setText('Current size is ' + event.element.offsetWidth + " x " + event.element.offsetHeight);
	};
	
	this.setListenerEnabled = function(enabled) {
		if (enabled) {
			setText("Listener enabled");
			self.addResizeListener(e, resizeListener);			
		} else {
			setText("Listener disabled");
			self.removeResizeListener(e, resizeListener);			
		}
	}
}