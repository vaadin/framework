package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingWithArrowsTest extends MultiBrowserTest {

    private final int PAGESIZE = 10;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
        openPopup();
    }

    private WebElement getDropDown() {
        // Selenium is used instead of TestBench4, because there is no method to
        // access the popup of the combobox
        // The method ComboBoxElement.openPopup() opens the popup, but doesn't
        // provide any way to access the popup and send keys to it.
        // Ticket #13756

        return driver.findElement(By.className("v-filterselect-input"));
    }

    private void openPopup() {
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.openPopup();
    }

    @Test
    public void scrollDownArrowKeyTest() throws InterruptedException {
        WebElement dropDownComboBox = getDropDown();

        for (int i = 0; i < PAGESIZE; i++) {
            dropDownComboBox.sendKeys(Keys.DOWN);
        }

        assertThat(getSelectedItemText(), is("item " + PAGESIZE)); // item 10
    }

    private String getSelectedItemText() {
        List<WebElement> items = driver
                .findElements(By.className("gwt-MenuItem-selected"));
        return items.get(0).getText();
    }

    @Test
    public void scrollUpArrowKeyTest() throws InterruptedException {
        WebElement dropDownComboBox = getDropDown();

        for (int i = 0; i < PAGESIZE; i++) {
            dropDownComboBox.sendKeys(Keys.DOWN);
        }

        // move to one item up
        waitUntilNextPageIsVisible();
        dropDownComboBox.sendKeys(Keys.UP);

        // item 9
        assertThat(getSelectedItemText(), is("item " + (PAGESIZE - 1)));
    }

    private void waitUntilNextPageIsVisible() {
        waitUntil(input -> getSelectedItemText().equals("item " + PAGESIZE), 5);
    }
}
