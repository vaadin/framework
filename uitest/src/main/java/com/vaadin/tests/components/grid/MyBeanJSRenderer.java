package com.vaadin.tests.components.grid;

import com.vaadin.annotations.JavaScript;
import com.vaadin.tests.components.grid.JavaScriptRenderers.ItemBean;
import com.vaadin.tests.components.grid.JavaScriptRenderers.MyBean;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;

/**
 *
 * @author Vaadin Ltd
 */
@JavaScript("myBeanJsRenderer.js")
public class MyBeanJSRenderer
        extends AbstractJavaScriptRenderer<ItemBean, MyBean> {

    public MyBeanJSRenderer() {
        super(MyBean.class, "");
    }

}
