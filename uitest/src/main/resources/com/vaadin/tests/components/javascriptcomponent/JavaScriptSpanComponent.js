com_vaadin_tests_components_javascriptcomponent_JavaScriptSpan_Span = function() {
  this.onStateChange = function() {
    this.getElement().innerText = this.getState().text;
  } 
}
com_vaadin_tests_components_javascriptcomponent_JavaScriptSpan_Span.tag = "span";
