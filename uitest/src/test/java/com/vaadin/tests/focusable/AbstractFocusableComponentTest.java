package com.vaadin.tests.focusable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class AbstractFocusableComponentTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testProgrammaticFocus() {
        selectMenuPath("Component", "State", "Set focus");
        assertTrue("Component should be focused", isFocused());
    }

    @Test
    public void testTabIndex() {
        assertEquals("0", getTabIndex());

        selectMenuPath("Component", "State", "Tab index", "-1");
        assertEquals("-1", getTabIndex());

        selectMenuPath("Component", "State", "Tab index", "10");
        assertEquals("10", getTabIndex());
    }

    protected String getTabIndex() {
        return getFocusElement().getAttribute("tabindex");
    }

    protected boolean isFocused() {
        return getFocusElement().equals(getDriver().switchTo().activeElement());
    }

    protected abstract WebElement getFocusElement();
}
