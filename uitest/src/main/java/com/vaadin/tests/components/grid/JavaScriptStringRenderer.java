package com.vaadin.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.tests.components.grid.JavaScriptRenderers.ItemBean;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;

@JavaScript("JavaScriptStringRenderer.js")
public class JavaScriptStringRenderer
        extends AbstractJavaScriptRenderer<ItemBean, String> {

    protected JavaScriptStringRenderer() {
        super(String.class);
    }

}
