package com.vaadin.tests.server.componentcontainer;

import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutTest extends AbstractIndexedLayoutTest {

    @Override
    protected Layout createLayout() {
        return new VerticalLayout();
    }

    @Override
    public VerticalLayout getLayout() {
        return (VerticalLayout) super.getLayout();
    }

    @Override
    protected Component getComponent(int index) {
        return getLayout().getComponent(index);
    }

    @Override
    protected int getComponentIndex(Component c) {
        return getLayout().getComponentIndex(c);
    }

    @Override
    protected int getComponentCount() {
        return getLayout().getComponentCount();
    }

}
