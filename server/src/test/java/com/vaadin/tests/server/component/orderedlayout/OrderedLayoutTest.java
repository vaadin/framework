package com.vaadin.tests.server.component.orderedlayout;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OrderedLayoutTest {

    @Test
    public void testVLIteration() {
        testIndexing(new VerticalLayout(), 10);
    }

    @Test
    public void testHLIteration() {
        testIndexing(new HorizontalLayout(), 12);
    }

    public void testIndexing(AbstractOrderedLayout aol, int nrComponents) {
        Component[] components = generateComponents(nrComponents);
        for (Component c : components) {
            aol.addComponent(c);
        }
        for (int i = 0; i < nrComponents; i++) {
            assert (aol.getComponent(i) == components[i]);
            assert (aol.getComponentIndex(components[i]) == i);
        }

        // Iteration should be in indexed order
        int idx = 0;
        for (Iterator<Component> i = aol.getComponentIterator(); i.hasNext();) {
            Component c = i.next();
            assert (aol.getComponentIndex(c) == idx++);
        }
    }

    private Component[] generateComponents(int nr) {
        Component[] components = new Component[nr];
        for (int i = 0; i < nr; i++) {
            components[i] = new Label("" + i);
        }

        return components;
    }

    @Test
    public void testExpandMethod() {

        VerticalLayout vl = new VerticalLayout();
        Label component = new Label();
        Label another = new Label();
        vl.addComponent(component);
        vl.expand(component, another);

        Assert.assertEquals(1, vl.getExpandRatio(component), 0.0001);
        Assert.assertEquals(1, vl.getExpandRatio(another), 0.0001);
        Assert.assertEquals(100, vl.getHeight(), 0.0001);
        Assert.assertSame(vl, another.getParent());

        HorizontalLayout hl = new HorizontalLayout();
        component = new Label();
        another = new Label();
        hl.addComponent(component);
        hl.expand(component, another);

        Assert.assertEquals(1, hl.getExpandRatio(component), 0.0001);
        Assert.assertEquals(1, hl.getExpandRatio(another), 0.0001);
        Assert.assertEquals(100, hl.getWidth(), 0.0001);
        Assert.assertSame(hl, another.getParent());

    }

}
