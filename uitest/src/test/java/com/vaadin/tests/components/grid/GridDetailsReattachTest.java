package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDetailsReattachTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void clickToAddCaption() {
        openTestURL();
        Assert.assertTrue("Grid details don't exist",  hasDetailsElement());
        $(ButtonElement.class).first().click();
        Assert.assertTrue("Grid details don't exist after deattach and reattach",hasDetailsElement() );
    }

    private final By locator = By.className("v-grid-spacer");

    private boolean hasDetailsElement() {
        return !findElements(locator).isEmpty();
    }

}
