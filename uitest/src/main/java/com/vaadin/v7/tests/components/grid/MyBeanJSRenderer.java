package com.vaadin.v7.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.v7.tests.components.grid.JavaScriptRenderers.MyBean;
import com.vaadin.v7.ui.renderers.AbstractJavaScriptRenderer;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
@JavaScript("myBeanJsRenderer.js")
public class MyBeanJSRenderer extends AbstractJavaScriptRenderer<MyBean> {

    public MyBeanJSRenderer() {
        super(MyBean.class, "");
    }

}
