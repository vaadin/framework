(function() {
	var loadedBeforeVaadin = (window.vaadin === undefined);

	window.reportUiDependencyStatus = function() {
		var style1Index = 1000;
		var style2Index = 1000;
		var style3Index = 1000;
		var themeIndex = -1;

		var stylesheets = document.querySelectorAll("link[rel=stylesheet]");
		for (var i = 0; i < stylesheets.length; i++) {
			var stylesheet = stylesheets[i];
			var href = stylesheet.getAttribute("href");
			if (href.indexOf("uiDependency1.css") > -1) {
				style1Index = i;
			} else if (href.indexOf("uiDependency2.css") > -1) {
				style2Index = i;
			} else if (href.indexOf("uiDependency3.css") > -1) {
				style3Index = i;
			} else if (href.indexOf("styles.css" > -1)) {
				themeIndex = i;
			}
		}

		var status = "Script loaded before vaadinBootstrap.js: "
				+ loadedBeforeVaadin;
		status += "<br />Style tag before vaadin theme: "
				+ (style1Index < themeIndex);
		status += "<br />Style tags in correct order: "
				+ (style1Index < style2Index && style2Index < style3Index);

		document.getElementById("statusBox").innerHTML = status;
	}
})();
