com_vaadin_tests_components_javascriptcomponent_JSComponentLoadingIndicator_JSComponent = function()
{
	var connector = this;
	var e = this.getElement();

	e.innerText="click me to ping server";
	e.id="js";
	e.addEventListener("click", function() {
		connector.test();
	});
}