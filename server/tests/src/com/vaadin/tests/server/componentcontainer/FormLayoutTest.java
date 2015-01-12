package com.vaadin.tests.server.componentcontainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class FormLayoutTest extends AbstractIndexedLayoutTestBase {

    @Override
    protected Layout createLayout() {
        return new FormLayout();
    }

    @Override
    public FormLayout getLayout() {
        return (FormLayout) super.getLayout();
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

    Component[] children = new Component[] { new Label("A"), new Label("B"),
            new Label("C"), new Label("D") };

    @Test
    public void testConstructorWithComponents() {
        FormLayout l = new FormLayout(children);
        assertOrder(l, new int[] { 0, 1, 2, 3 });
    }

    @Test
    public void testAddComponents() {
        FormLayout l = new FormLayout();
        l.addComponents(children);
        assertOrder(l, new int[] { 0, 1, 2, 3 });
    }

    private void assertOrder(Layout layout, int[] indices) {
        Iterator<?> i = layout.iterator();
        try {
            for (int index : indices) {
                if (index != -1) {
                    assertSame(children[index], i.next());
                } else {
                    i.next();
                }
            }
            assertFalse("Too many components in layout", i.hasNext());
        } catch (NoSuchElementException e) {
            fail("Too few components in layout");
        }
    }

}
