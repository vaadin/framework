package com.vaadin.tests.components.window;

import static com.vaadin.tests.components.window.ComboboxScrollableWindow.COMBOBOX_ID;
import static com.vaadin.tests.components.window.ComboboxScrollableWindow.WINDOW_ID;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a ComboBox at the bottom of a Window remains visible when clicked.
 *
 * @author Vaadin Ltd
 */
public class ComboboxScrollableWindowTest extends MultiBrowserTest {

    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();

        WindowElement window = $(WindowElement.class).id(WINDOW_ID);
        WebElement scrollableElement = window
                .findElement(By.className("v-scrollable"));
        TestBenchElementCommands scrollable = testBenchElement(
                scrollableElement);
        scrollable.scroll(1000);

        int beforeClick = getScrollTop(scrollableElement);
        ComboBoxElement comboBox = $(ComboBoxElement.class).id(COMBOBOX_ID);
        Point location = comboBox.getLocation();

        comboBox.openPopup();
        waitForElementPresent(By.className("v-filterselect-suggestpopup"));

        assertEquals("Clicking should not cause scrolling", beforeClick,
                getScrollTop(scrollableElement));
        assertEquals("ComboBox should not move along x-axis", location.getX(),
                comboBox.getLocation().getX());
        assertEquals("ComboBox should not move along y-axis", location.getY(),
                comboBox.getLocation().getY());
    }

}
