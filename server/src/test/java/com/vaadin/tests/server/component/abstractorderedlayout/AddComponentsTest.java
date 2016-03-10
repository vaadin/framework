package com.vaadin.tests.server.component.abstractorderedlayout;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class AddComponentsTest {

    Component[] children = new Component[] { new Label("A"), new Label("B"),
            new Label("C"), new Label("D") };

    @Test
    public void moveComponentsBetweenLayouts() {
        AbstractOrderedLayout layout1 = new HorizontalLayout();
        AbstractOrderedLayout layout2 = new VerticalLayout();

        layout1.addComponent(children[0]);
        layout1.addComponent(children[1]);

        layout2.addComponent(children[2]);
        layout2.addComponent(children[3]);

        layout2.addComponent(children[1], 1);
        assertOrder(layout1, new int[] { 0 });
        assertOrder(layout2, new int[] { 2, 1, 3 });

        layout1.addComponent(children[3], 0);
        assertOrder(layout1, new int[] { 3, 0 });
        assertOrder(layout2, new int[] { 2, 1 });

        layout2.addComponent(children[0]);
        assertOrder(layout1, new int[] { 3 });
        assertOrder(layout2, new int[] { 2, 1, 0 });

        layout1.addComponentAsFirst(children[1]);
        assertOrder(layout1, new int[] { 1, 3 });
        assertOrder(layout2, new int[] { 2, 0 });
    }

    @Test
    public void shuffleChildComponents() {
        shuffleChildComponents(new HorizontalLayout());
        shuffleChildComponents(new VerticalLayout());
    }

    private void shuffleChildComponents(AbstractOrderedLayout layout) {

        for (int i = 0; i < children.length; ++i) {
            layout.addComponent(children[i], i);
        }

        assertOrder(layout, new int[] { 0, 1, 2, 3 });

        // Move C from #2 to #1
        // Exhibits defect #7668
        layout.addComponent(children[2], 1);
        assertOrder(layout, new int[] { 0, 2, 1, 3 });

        // Move C from #1 to #4 (which becomes #3 when #1 is erased)
        layout.addComponent(children[2], 4);
        assertOrder(layout, new int[] { 0, 1, 3, 2 });

        // Keep everything in place
        layout.addComponent(children[1], 1);
        assertOrder(layout, new int[] { 0, 1, 3, 2 });

        // Move D from #2 to #0
        layout.addComponent(children[3], 0);
        assertOrder(layout, new int[] { 3, 0, 1, 2 });

        // Move A from #1 to end (#4 which becomes #3)
        layout.addComponent(children[0]);
        assertOrder(layout, new int[] { 3, 1, 2, 0 });

        // Keep everything in place
        layout.addComponent(children[0]);
        assertOrder(layout, new int[] { 3, 1, 2, 0 });

        // Move C from #2 to #0
        layout.addComponentAsFirst(children[2]);
        assertOrder(layout, new int[] { 2, 3, 1, 0 });

        // Keep everything in place
        layout.addComponentAsFirst(children[2]);
        assertOrder(layout, new int[] { 2, 3, 1, 0 });
    }

    @Test
    public void testConstructorsWithComponents() {
        AbstractOrderedLayout layout1 = new HorizontalLayout(children);
        assertOrder(layout1, new int[] { 0, 1, 2, 3 });
        shuffleChildComponents(layout1);

        AbstractOrderedLayout layout2 = new VerticalLayout(children);
        assertOrder(layout2, new int[] { 0, 1, 2, 3 });
        shuffleChildComponents(layout2);
    }

    @Test
    public void testAddComponents() {
        HorizontalLayout layout1 = new HorizontalLayout();
        layout1.addComponents(children);
        assertOrder(layout1, new int[] { 0, 1, 2, 3 });

        Label extra = new Label("Extra");
        layout1.addComponents(extra);
        assertSame(extra, layout1.getComponent(4));

        layout1.removeAllComponents();
        layout1.addComponents(children[3], children[2], children[1],
                children[0]);
        assertOrder(layout1, new int[] { 3, 2, 1, 0 });

        VerticalLayout layout2 = new VerticalLayout(children);
        layout2.addComponents(children);
        assertOrder(layout2, new int[] { 0, 1, 2, 3 });

        layout2.addComponents(extra);
        assertSame(extra, layout2.getComponent(4));

        layout2.removeAllComponents();
        layout2.addComponents(children[3], children[2], children[1],
                children[0]);
        assertOrder(layout2, new int[] { 3, 2, 1, 0 });
    }

    /**
     * Asserts that layout has the components in children in the order specified
     * by indices.
     */
    private void assertOrder(Layout layout, int[] indices) {
        Iterator<?> i = layout.getComponentIterator();
        try {
            for (int index : indices) {
                assertSame(children[index], i.next());
            }
            assertFalse("Too many components in layout", i.hasNext());
        } catch (NoSuchElementException e) {
            fail("Too few components in layout");
        }
    }
}
