package com.vaadin.tests.server.component.abstractorderedlayout;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;

/**
 * Tests for abstract layout settings which should be preserved on replace
 * component
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class LayoutSettingsOnReplaceTest {

    @Test
    public void testExpandRatio() {
        AbstractOrderedLayout layout = new AbstractOrderedLayout() {
        };

        AbstractComponent first = new AbstractComponent() {
        };
        AbstractComponent second = new AbstractComponent() {
        };

        layout.addComponent(first);
        layout.addComponent(second);

        int ratio = 2;
        layout.setExpandRatio(first, ratio);
        layout.setExpandRatio(second, 1);

        AbstractComponent replace = new AbstractComponent() {
        };
        layout.replaceComponent(first, replace);

        Assert.assertEquals(
                "Expand ratio for replaced component is not "
                        + "the same as for previous one",
                ratio, layout.getExpandRatio(replace), 0.0001);
    }

    @Test
    public void testAlignment() {
        AbstractOrderedLayout layout = new AbstractOrderedLayout() {
        };

        AbstractComponent first = new AbstractComponent() {
        };
        AbstractComponent second = new AbstractComponent() {
        };

        layout.addComponent(first);
        layout.addComponent(second);

        Alignment alignment = Alignment.BOTTOM_RIGHT;
        layout.setComponentAlignment(first, alignment);
        layout.setComponentAlignment(second, Alignment.MIDDLE_CENTER);

        AbstractComponent replace = new AbstractComponent() {
        };
        layout.replaceComponent(first, replace);

        Assert.assertEquals(
                "Alignment for replaced component is not "
                        + "the same as for previous one",
                alignment, layout.getComponentAlignment(replace));
    }
}
