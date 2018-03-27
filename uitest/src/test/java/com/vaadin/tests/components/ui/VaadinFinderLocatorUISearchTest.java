package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.components.button.ButtonClick;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class VaadinFinderLocatorUISearchTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ButtonClick.class;
    }

    @Test
    public void getUIElementTest() {
        openTestURL();
        UIElement ui = $(UIElement.class).first();
        Assert.assertNotNull("Couldn't find the UI Element on the page", ui);
    }
}
