package com.vaadin.tests.widgetset.client.grid;

import com.vaadin.client.connectors.grid.AbstractGridRendererConnector;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.client.SimpleTestBean;

@Connect(com.vaadin.tests.components.grid.BeanRenderer.class)
public class PojoRendererConnector
        extends AbstractGridRendererConnector<SimpleTestBean> {

    public static class BeanRenderer implements Renderer<SimpleTestBean> {
        @Override
        public void render(RendererCellReference cell, SimpleTestBean bean) {
            cell.getElement().setInnerText(bean.toString());
        }
    }

    @Override
    public BeanRenderer getRenderer() {
        return (BeanRenderer) super.getRenderer();
    }
}
