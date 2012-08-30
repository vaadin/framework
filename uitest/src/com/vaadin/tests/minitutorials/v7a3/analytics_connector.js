window._gaq = window._gaq || [];

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
  
window.com_vaadin_tests_minitutorials_v7a3_Analytics = function() {
	this.pushCommand = function(command) {
		_gaq.push(command);
	}
} 