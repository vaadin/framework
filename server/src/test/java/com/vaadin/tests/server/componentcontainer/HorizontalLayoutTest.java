package com.vaadin.tests.server.componentcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

public class HorizontalLayoutTest extends AbstractIndexedLayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new HorizontalLayout();
    }

    @Override
    public HorizontalLayout getLayout() {
        return (HorizontalLayout) super.getLayout();
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

    @Test
    public void addAndExpand_basicCase() {
        Button b1 = new Button();
        Button b2 = new Button();
        Button b3 = new Button();

        HorizontalLayout layout = getLayout();
        layout.addComponents(b3, b2);

        layout.addComponentsAndExpand(b1, b2);

        assertEquals(3, layout.getComponentCount());

        assertSame(b3, layout.getComponent(0));
        assertSame(b1, layout.getComponent(1));
        assertSame(b2, layout.getComponent(2));

        assertEquals(0, layout.getExpandRatio(b3), 0);
        assertEquals(1, layout.getExpandRatio(b1), 0);
        assertEquals(1, layout.getExpandRatio(b2), 0);

        assertEquals(-1, b3.getWidth(), 0);
        assertEquals(100, b1.getWidth(), 0);
        assertEquals(100, b2.getWidth(), 0);
    }

    @Test
    public void addAndExpand_undefinedHeightUpdated() {
        HorizontalLayout layout = getLayout();

        assertEquals(-1, layout.getWidth(), 0);

        layout.addComponentsAndExpand();

        assertEquals(100, layout.getWidth(), 0);
    }

    @Test
    public void addAndExpand_definedHeightPreserved() {
        HorizontalLayout layout = getLayout();

        layout.setWidth("150px");

        layout.addComponentsAndExpand();

        assertEquals(150, layout.getWidth(), 0);
    }

}
