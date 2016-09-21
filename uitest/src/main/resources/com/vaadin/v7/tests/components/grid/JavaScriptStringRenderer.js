com_vaadin_v7_tests_components_grid_JavaScriptStringRenderer = function() {
	this.render = function(cell, data) {
		cell.element.textContent = data;
	}
}