package com.vaadin.tests.server.componentcontainer;

import java.util.List;

import junit.framework.TestCase;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.colorpicker.ColorPickerPreview;

public class AddRemoveComponentTest extends TestCase {

    public void testRemoveComponentFromWrongContainer()
            throws InstantiationException, IllegalAccessException {
        List<Class<? extends ComponentContainer>> containerClasses = VaadinClasses
                .getComponentContainersSupportingAddRemoveComponent();

        // No default constructor, special case
        containerClasses.remove(CustomLayout.class);
        containerClasses.remove(ColorPickerPreview.class);
        testRemoveComponentFromWrongContainer(new CustomLayout("dummy"));

        for (Class<? extends ComponentContainer> c : containerClasses) {
            testRemoveComponentFromWrongContainer(c.newInstance());
        }
    }

    private void testRemoveComponentFromWrongContainer(
            ComponentContainer componentContainer) {
        HorizontalLayout hl = new HorizontalLayout();
        Label label = new Label();
        hl.addComponent(label);

        componentContainer.removeComponent(label);
        assertEquals(
                "Parent no longer correct for " + componentContainer.getClass(),
                hl, label.getParent());
    }
}
