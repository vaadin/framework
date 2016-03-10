package com.vaadin.tests.server.componentcontainer;

import junit.framework.TestCase;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public abstract class AbstractIndexedLayoutTestBase extends TestCase {

    private Layout layout;

    protected abstract Layout createLayout();

    @Override
    protected void setUp() throws Exception {
        layout = createLayout();
    }

    public Layout getLayout() {
        return layout;
    }

    public void testAddRemoveComponent() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);

        assertEquals(c1, getComponent(0));
        assertEquals(1, getComponentCount());
        layout.addComponent(c2);
        assertEquals(c1, getComponent(0));
        assertEquals(c2, getComponent(1));
        assertEquals(2, getComponentCount());
        layout.removeComponent(c1);
        assertEquals(c2, getComponent(0));
        assertEquals(1, getComponentCount());
        layout.removeComponent(c2);
        assertEquals(0, getComponentCount());
    }

    protected abstract int getComponentCount();

    protected abstract Component getComponent(int index);

    protected abstract int getComponentIndex(Component c);

    public void testGetComponentIndex() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);
        assertEquals(0, getComponentIndex(c1));
        layout.addComponent(c2);
        assertEquals(0, getComponentIndex(c1));
        assertEquals(1, getComponentIndex(c2));
        layout.removeComponent(c1);
        assertEquals(0, getComponentIndex(c2));
        layout.removeComponent(c2);
        assertEquals(-1, getComponentIndex(c2));
        assertEquals(-1, getComponentIndex(c1));
    }

    public void testGetComponent() {
        Label c1 = new Label();
        Label c2 = new Label();

        layout.addComponent(c1);
        assertEquals(c1, getComponent(0));
        layout.addComponent(c2);
        assertEquals(c1, getComponent(0));
        assertEquals(c2, getComponent(1));
        layout.removeComponent(c1);
        assertEquals(c2, getComponent(0));
        layout.removeComponent(c2);
        try {
            getComponent(0);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }
}
