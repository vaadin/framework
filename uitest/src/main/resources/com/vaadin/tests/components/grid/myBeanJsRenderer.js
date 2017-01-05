window.com_vaadin_tests_components_grid_MyBeanJSRenderer = function() {
	this.init = function(cell) {
		cell.element.setAttribute("column", cell.columnIndex);
	}
	
	this.render = function(cell, data) {
		if (!cell.renderedText) {
			cell.element.innerHTML = 'Bean(' + data.integer + ', ' + data.string + ')';
		}
	}
	
	this.getConsumedEvents = function() { return ["click"] };
	
	this.onBrowserEvent = function(cell, event) {
		cell.renderedText ="Clicked " + cell.rowIndex + " with key " + this.getRowKey(cell.rowIndex) +" at " + event.clientX;
		cell.element.innerHTML=cell.renderedText;
		return true;
	}
}