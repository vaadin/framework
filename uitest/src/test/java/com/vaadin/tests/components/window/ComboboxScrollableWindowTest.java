package com.vaadin.tests.components.window;

import static com.vaadin.tests.components.window.ComboboxScrollableWindow.COMBOBOX_ID;
import static com.vaadin.tests.components.window.ComboboxScrollableWindow.WINDOW_ID;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;
import com.vaadin.tests.tb3.newelements.WindowElement;

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
        ComboBoxElement comboBox = $(ComboBoxElement.class).id(COMBOBOX_ID);
        comboBox.openPopup();
        waitForElementPresent(By.className("v-filterselect-suggestpopup"));

        compareScreen("combobox-open");
    }

}
