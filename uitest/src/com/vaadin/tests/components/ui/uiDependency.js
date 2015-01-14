(function() {
	var loadedBeforeVaadin = (window.vaadin === undefined);
	
	window.reportUiDependencyStatus = function() {
		var styleIndex = 1000;
		var themeIndex = -1;
		
		var stylesheets = document.querySelectorAll("link[rel=stylesheet]");
		for(var i = 0; i < stylesheets.length; i++) {
			var stylesheet = stylesheets[i];
			var href = stylesheet.getAttribute("href"); 
			if (href.indexOf("uiDependency.css") > -1) {
				styleIndex = i;
			} else if (href.indexOf("styles.css" > -1)) {
				themeIndex = i;
			}
		}
		
		var status = "Script loaded before vaadinBootstrap.js: " + loadedBeforeVaadin;
		status += "<br />Style tag before vaadin theme: " + (styleIndex < themeIndex);
		
		document.getElementById("statusBox").innerHTML = status;
	}
})();