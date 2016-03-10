package com.vaadin.tests.server.components;

import java.util.Iterator;

import junit.framework.TestCase;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.Area;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ComponentAttachDetachListenerTest extends TestCase {

    private AbstractOrderedLayout olayout;
    private GridLayout gridlayout;
    private AbsoluteLayout absolutelayout;
    private CssLayout csslayout;
    private CustomLayout customlayout;

    // General variables
    private int attachCounter = 0;
    private Component attachedComponent = null;
    private HasComponents attachTarget = null;
    private boolean foundInContainer = false;

    private int detachCounter = 0;
    private Component detachedComponent = null;
    private HasComponents detachedTarget = null;

    // Ordered layout specific variables
    private int indexOfComponent = -1;

    // Grid layout specific variables
    private Area componentArea = null;

    // Absolute layout specific variables
    private ComponentPosition componentPosition = null;

    private class MyAttachListener implements ComponentAttachListener {
        @Override
        public void componentAttachedToContainer(ComponentAttachEvent event) {
            attachCounter++;
            attachedComponent = event.getAttachedComponent();
            attachTarget = event.getContainer();

            // Search for component in container (should be found)
            Iterator<Component> iter = attachTarget.iterator();
            while (iter.hasNext()) {
                if (iter.next() == attachedComponent) {
                    foundInContainer = true;
                    break;
                }
            }

            // Get layout specific variables
            if (attachTarget instanceof AbstractOrderedLayout) {
                indexOfComponent = ((AbstractOrderedLayout) attachTarget)
                        .getComponentIndex(attachedComponent);
            } else if (attachTarget instanceof GridLayout) {
                componentArea = ((GridLayout) attachTarget)
                        .getComponentArea(attachedComponent);
            } else if (attachTarget instanceof AbsoluteLayout) {
                componentPosition = ((AbsoluteLayout) attachTarget)
                        .getPosition(attachedComponent);
            }
        }
    }

    private class MyDetachListener implements ComponentDetachListener {
        @Override
        public void componentDetachedFromContainer(ComponentDetachEvent event) {
            detachCounter++;
            detachedComponent = event.getDetachedComponent();
            detachedTarget = event.getContainer();

            // Search for component in container (should NOT be found)
            Iterator<Component> iter = detachedTarget.iterator();
            while (iter.hasNext()) {
                if (iter.next() == detachedComponent) {
                    foundInContainer = true;
                    break;
                }
            }

            // Get layout specific variables
            if (detachedTarget instanceof AbstractOrderedLayout) {
                indexOfComponent = ((AbstractOrderedLayout) detachedTarget)
                        .getComponentIndex(detachedComponent);
            } else if (detachedTarget instanceof GridLayout) {
                componentArea = ((GridLayout) detachedTarget)
                        .getComponentArea(detachedComponent);
            } else if (detachedTarget instanceof AbsoluteLayout) {
                componentPosition = ((AbsoluteLayout) detachedTarget)
                        .getPosition(detachedComponent);
            }

        }
    }

    private void resetVariables() {
        // Attach
        attachCounter = 0;
        attachedComponent = null;
        attachTarget = null;
        foundInContainer = false;

        // Detach
        detachCounter = 0;
        detachedComponent = null;
        detachedTarget = null;

        // Common
        indexOfComponent = -1;
        componentArea = null;
        componentPosition = null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        olayout = new HorizontalLayout();
        olayout.addComponentAttachListener(new MyAttachListener());
        olayout.addComponentDetachListener(new MyDetachListener());

        gridlayout = new GridLayout();
        gridlayout.addComponentAttachListener(new MyAttachListener());
        gridlayout.addComponentDetachListener(new MyDetachListener());

        absolutelayout = new AbsoluteLayout();
        absolutelayout.addComponentAttachListener(new MyAttachListener());
        absolutelayout.addComponentDetachListener(new MyDetachListener());

        csslayout = new CssLayout();
        csslayout.addComponentAttachListener(new MyAttachListener());
        csslayout.addComponentDetachListener(new MyDetachListener());

        customlayout = new CustomLayout("<div location='loc'/>");
        customlayout.addComponentAttachListener(new MyAttachListener());
        customlayout.addComponentDetachListener(new MyDetachListener());
    }

    public void testOrderedLayoutAttachListener() {
        // Reset state variables
        resetVariables();

        // Add component -> Should trigger attach listener
        Component comp = new Label();
        olayout.addComponent(comp);

        // Attach counter should get incremented
        assertEquals(1, attachCounter);

        // The attached component should be the label
        assertSame(comp, attachedComponent);

        // The attached target should be the layout
        assertSame(olayout, attachTarget);

        // The attached component should be found in the container
        assertTrue(foundInContainer);

        // The index of the component should not be -1
        assertFalse(indexOfComponent == -1);
    }

    public void testOrderedLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        olayout.addComponent(comp);

        // Reset state variables (since they are set by the attach listener)
        resetVariables();

        // Detach the component -> triggers the detach listener
        olayout.removeComponent(comp);

        // Detach counter should get incremented
        assertEquals(1, detachCounter);

        // The detached component should be the label
        assertSame(comp, detachedComponent);

        // The detached target should be the layout
        assertSame(olayout, detachedTarget);

        // The detached component should not be found in the container
        assertFalse(foundInContainer);

        // The index of the component should be -1
        assertEquals(-1, indexOfComponent);
    }

    public void testGridLayoutAttachListener() {
        // Reset state variables
        resetVariables();

        // Add component -> Should trigger attach listener
        Component comp = new Label();
        gridlayout.addComponent(comp);

        // Attach counter should get incremented
        assertEquals(1, attachCounter);

        // The attached component should be the label
        assertSame(comp, attachedComponent);

        // The attached target should be the layout
        assertSame(gridlayout, attachTarget);

        // The attached component should be found in the container
        assertTrue(foundInContainer);

        // The grid area should not be null
        assertNotNull(componentArea);
    }

    public void testGridLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        gridlayout.addComponent(comp);

        // Reset state variables (since they are set by the attach listener)
        resetVariables();

        // Detach the component -> triggers the detach listener
        gridlayout.removeComponent(comp);

        // Detach counter should get incremented
        assertEquals(1, detachCounter);

        // The detached component should be the label
        assertSame(comp, detachedComponent);

        // The detached target should be the layout
        assertSame(gridlayout, detachedTarget);

        // The detached component should not be found in the container
        assertFalse(foundInContainer);

        // The grid area should be null
        assertNull(componentArea);
    }

    public void testAbsoluteLayoutAttachListener() {
        // Reset state variables
        resetVariables();

        // Add component -> Should trigger attach listener
        Component comp = new Label();
        absolutelayout.addComponent(comp);

        // Attach counter should get incremented
        assertEquals(1, attachCounter);

        // The attached component should be the label
        assertSame(comp, attachedComponent);

        // The attached target should be the layout
        assertSame(absolutelayout, attachTarget);

        // The attached component should be found in the container
        assertTrue(foundInContainer);

        // The component position should not be null
        assertNotNull(componentPosition);
    }

    public void testAbsoluteLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        absolutelayout.addComponent(comp);

        // Reset state variables (since they are set by the attach listener)
        resetVariables();

        // Detach the component -> triggers the detach listener
        absolutelayout.removeComponent(comp);

        // Detach counter should get incremented
        assertEquals(1, detachCounter);

        // The detached component should be the label
        assertSame(comp, detachedComponent);

        // The detached target should be the layout
        assertSame(absolutelayout, detachedTarget);

        // The detached component should not be found in the container
        assertFalse(foundInContainer);

        // The component position should be null
        assertNull(componentPosition);
    }

    public void testCSSLayoutAttachListener() {
        // Reset state variables
        resetVariables();

        // Add component -> Should trigger attach listener
        Component comp = new Label();
        csslayout.addComponent(comp);

        // Attach counter should get incremented
        assertEquals(1, attachCounter);

        // The attached component should be the label
        assertSame(comp, attachedComponent);

        // The attached target should be the layout
        assertSame(csslayout, attachTarget);

        // The attached component should be found in the container
        assertTrue(foundInContainer);
    }

    public void testCSSLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        csslayout.addComponent(comp);

        // Reset state variables (since they are set by the attach listener)
        resetVariables();

        // Detach the component -> triggers the detach listener
        csslayout.removeComponent(comp);

        // Detach counter should get incremented
        assertEquals(1, detachCounter);

        // The detached component should be the label
        assertSame(comp, detachedComponent);

        // The detached target should be the layout
        assertSame(csslayout, detachedTarget);

        // The detached component should not be found in the container
        assertFalse(foundInContainer);
    }

    public void testCustomLayoutAttachListener() {
        // Reset state variables
        resetVariables();

        // Add component -> Should trigger attach listener
        Component comp = new Label();
        customlayout.addComponent(comp, "loc");

        assertEquals("Attach counter should get incremented", 1, attachCounter);

        assertSame("The attached component should be the label", comp,
                attachedComponent);

        assertSame("The attached target should be the layout", customlayout,
                attachTarget);

        assertTrue("The attached component should be found in the container",
                foundInContainer);
    }

    public void testCustomLayoutDetachListener() {
        // Add a component to detach
        Component comp = new Label();
        customlayout.addComponent(comp);

        // Reset state variables (since they are set by the attach listener)
        resetVariables();

        // Detach the component -> triggers the detach listener
        customlayout.removeComponent(comp);

        assertEquals("Detach counter should get incremented", 1, detachCounter);

        assertSame("The detached component should be the label", comp,
                detachedComponent);

        assertSame("The detached target should be the layout", customlayout,
                detachedTarget);

        assertFalse(
                "The detached component should not be found in the container",
                foundInContainer);
    }
}
