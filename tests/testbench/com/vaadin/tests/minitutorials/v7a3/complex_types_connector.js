window.com_vaadin_tests_minitutorials_v7a3_ComplexTypesComponent = function() {
	this.registerRpc({
		sendComplexTypes: function(list, stringMap, otherMap, connectorMap, bits, matrix, bean) {
			var connectorId = this.getConnectorId();
			
			var message = 'list[2]  = "' + list[2] + '"<br />';
			message += 'stringMap.two = ' + stringMap.two + '<br />'; 
			message += 'otherMap[1][1] = "' + otherMap[1][1] + '"<br />'; 
			message += 'connectorMap[connectorId] = "' + connectorMap[connectorId] +'"<br />'; 
			message += 'bits[3] = ' + bits[3] + '<br />'; 
			message += 'matrix[0][1] = ' + matrix[0][1] + '<br />'; 
			message += 'bean.bean.integer = ' + bean.bean.integer + '<br />';
			
			this.getElement().innerHTML = message;
		}
	});
}