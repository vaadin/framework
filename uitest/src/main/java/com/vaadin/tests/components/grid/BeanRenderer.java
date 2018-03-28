package com.vaadin.tests.components.grid;

import com.vaadin.tests.components.grid.CustomRendererUI.Data;
import com.vaadin.tests.widgetset.client.SimpleTestBean;
import com.vaadin.ui.renderers.AbstractRenderer;

public class BeanRenderer extends AbstractRenderer<Data, SimpleTestBean> {
    public BeanRenderer() {
        super(SimpleTestBean.class, "");
    }
}
