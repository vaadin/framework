package com.vaadin.tests.server.componentcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutTest extends AbstractIndexedLayoutTestBase {

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

    @Test
    public void addAndExpand_basicCase() {
        Button b1 = new Button();
        Button b2 = new Button();
        Button b3 = new Button();

        VerticalLayout layout = getLayout();
        layout.addComponents(b3, b2);

        layout.addComponentsAndExpand(b1, b2);

        assertEquals(3, layout.getComponentCount());

        assertSame(b3, layout.getComponent(0));
        assertSame(b1, layout.getComponent(1));
        assertSame(b2, layout.getComponent(2));

        assertEquals(0, layout.getExpandRatio(b3), 0);
        assertEquals(1, layout.getExpandRatio(b1), 0);
        assertEquals(1, layout.getExpandRatio(b2), 0);

        assertEquals(-1, b3.getHeight(), 0);
        assertEquals(100, b1.getHeight(), 0);
        assertEquals(100, b2.getHeight(), 0);
    }

    @Test
    public void addAndExpand_undefinedHeightUpdated() {
        VerticalLayout layout = getLayout();

        assertEquals(-1, layout.getHeight(), 0);

        layout.addComponentsAndExpand();

        assertEquals(100, layout.getHeight(), 0);
    }

    @Test
    public void addAndExpand_definedHeightPreserved() {
        VerticalLayout layout = getLayout();

        layout.setHeight("150px");

        layout.addComponentsAndExpand();

        assertEquals(150, layout.getHeight(), 0);
    }

}
