window.com_vaadin_tests_components_grid_MyBeanJSRenderer = function() {
	this.init = function(cell) {
		cell.element.setAttribute("column", cell.columnIndex);
	}
	
	this.render = function(cell, data) {
		cell.element.innerHTML = 'Bean(' + data.integer + ', ' + data.string + ')'
	}
	
	this.getConsumedEvents = function() { return ["click"] };
	
	this.onBrowserEvent = function(cell, event) {
		cell.element.innerHTML =  "Clicked " + cell.rowIndex + " with key " + this.getRowKey(cell.rowIndex) +" at " + event.clientX;
		return true;
	}
}