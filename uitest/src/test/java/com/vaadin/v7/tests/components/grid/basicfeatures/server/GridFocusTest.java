package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

/**
 * Test for server-side Grid focus features.
 *
 * @author Vaadin Ltd
 */
public class GridFocusTest extends GridBasicFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testFocusListener() {
        selectMenuPath("Component", "Listeners", "Focus listener");

        getGridElement().click();

        assertTrue("Focus listener should be invoked",
                getLogRow(0).contains("FocusEvent"));
    }

    @Test
    public void testBlurListener() {
        selectMenuPath("Component", "Listeners", "Blur listener");

        getGridElement().click();
        $(MenuBarElement.class).first().click();

        assertTrue("Blur listener should be invoked",
                getLogRow(0).contains("BlurEvent"));
    }

    @Test
    public void testProgrammaticFocus() {
        selectMenuPath("Component", "State", "Set focus");

        assertTrue("Grid cell (0, 0) should be focused",
                getGridElement().getCell(0, 0).isFocused());
    }

    @Test
    public void testTabIndex() {
        assertEquals(getGridElement().getAttribute("tabindex"), "0");

        selectMenuPath("Component", "State", "Tab index", "-1");
        assertEquals(getGridElement().getAttribute("tabindex"), "-1");

        selectMenuPath("Component", "State", "Tab index", "10");
        assertEquals(getGridElement().getAttribute("tabindex"), "10");
    }
}
