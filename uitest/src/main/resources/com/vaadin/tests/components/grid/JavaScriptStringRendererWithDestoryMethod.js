com_vaadin_tests_components_grid_JavaScriptStringRendererWithDestoryMethod = function() {
	this.render = function(cell, data) {
		cell.element.textContent = data;
		// This one is for IE8
		cell.element.innerText = data;
	}

	this.destory = function(cell) {
		document.getElementById("clientLog").innerHTML += "destory: "+cell.rowIndex+"/"+cell.columnIndex+"<br>";
	}

}