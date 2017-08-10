com_vaadin_tests_components_grid_JavaScriptStringRenderer = function() {
	this.render = function(cell, data) {
		cell.element.textContent = data;
		// This one is for IE8
		cell.element.innerText = data;
	}

	this.destroy = function(cell) {
		document.getElementById("clientLog").innerHTML += "destroy: "+cell.rowIndex+"/"+cell.columnIndex+"<br>";
	}

}