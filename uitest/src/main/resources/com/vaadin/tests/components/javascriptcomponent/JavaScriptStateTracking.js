window.com_vaadin_tests_components_javascriptcomponent_JavaScriptStateTracking_StateTrackingComponent = function() {
	var self = this;

	this.showState = function(state) {
		this.getElement().innerHTML = 'counter: <span id="counter">' + state.counter + '</span><br>'
				+ 'field1: <span id="field1">' + state.field1 + '</span><br>'
				+ 'field2: <span id="field2">' + state.field2 + '</span>';
	}

	this.onStateChange = function() {
		this.showState(this.getState());
	}
}