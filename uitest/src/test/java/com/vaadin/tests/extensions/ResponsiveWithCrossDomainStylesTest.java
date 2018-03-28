package com.vaadin.tests.extensions;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveWithCrossDomainStylesTest extends MultiBrowserTest {
    @Test
    public void testResponsive() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();
    }

}
