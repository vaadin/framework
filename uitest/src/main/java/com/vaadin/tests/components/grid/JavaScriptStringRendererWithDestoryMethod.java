package com.vaadin.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.tests.components.grid.JavaScriptRenderers.ItemBean;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;

@JavaScript("JavaScriptStringRendererWithDestoryMethod.js")
public class JavaScriptStringRendererWithDestoryMethod
        extends AbstractJavaScriptRenderer<ItemBean, String> {

    protected JavaScriptStringRendererWithDestoryMethod() {
        super(String.class);
    }

}