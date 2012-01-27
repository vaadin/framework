package com.vaadin.tests.server.componentcontainer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;

public class CssLayoutTest extends AbstractIndexedLayoutTest {

    @Override
    protected Layout createLayout() {
        return new CssLayout();
    }

    @Override
    public CssLayout getLayout() {
        return (CssLayout) super.getLayout();
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
