(function() {
	window.logMessage = function(msg) {
		if (!window.document.getElementById("clientlog")) {
			var d = document.createElement("div");
			d.id = "clientlog";
			document.body.insertBefore(d, document.body.firstChild);
		}
		var d = document.createElement("div");
		d.innerText = msg;
		d.className = "message";
		window.document.getElementById("clientlog").appendChild(d);

	}
})();
