package com.vaadin.v7.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.v7.ui.renderers.AbstractJavaScriptRenderer;

@JavaScript("JavaScriptStringRenderer.js")
public class JavaScriptStringRenderer
        extends AbstractJavaScriptRenderer<String> {

    protected JavaScriptStringRenderer() {
        super(String.class);
    }

}
