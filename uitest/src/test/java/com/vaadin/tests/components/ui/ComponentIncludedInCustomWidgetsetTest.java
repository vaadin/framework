package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests if a component is included in a custom widgetset
 * (com.vaadin.tests.widgetset.TestingWidgetSet)
 *
 * @author Vaadin Ltd
 */
public class ComponentIncludedInCustomWidgetsetTest extends MultiBrowserTest {

    @Test
    public void testComponentInTestingWidgetsetNotInDefaultWidgetset() {
        openTestURL();
        WebElement component = vaadinElementById("missing-component");
        assertEquals(
                "This component is available in TestingWidgetset, but not in DefaultWidgetset",
                component.getText());
    }
}
