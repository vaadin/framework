package com.vaadin.tests.components.ui;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test class for issue #13477, where selecting a combobox item that is too long
 * would render the ending of an item instead of the beginning, which was
 * considered less than informative.
 *
 * @author Vaadin Ltd
 */

public class ComboboxSelectedItemTextTest extends MultiBrowserTest {

    public final String SCREENSHOT_NAME_EDITABLE = "LongComboboxItemSelectedEditable";
    public final String SCREENSHOT_NAME_NON_EDITABLE = "LongComboboxItemSelectedNonEditable";
    public final int INDEX_EDITABLE_COMBOBOX = 1;
    public final int INDEX_NON_EDITABLE_COMBOBOX = 2;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Regression. See #16636.
        return getBrowsersExcludingChrome();
    }

    @Test
    public void testCombobox() throws IOException {
        testCombobox(INDEX_EDITABLE_COMBOBOX, INDEX_NON_EDITABLE_COMBOBOX,
                SCREENSHOT_NAME_EDITABLE);
    }

    @Test
    public void testComboboxNonEditable() throws IOException {
        testCombobox(INDEX_NON_EDITABLE_COMBOBOX, INDEX_EDITABLE_COMBOBOX,
                SCREENSHOT_NAME_NON_EDITABLE);
    }

    private void testCombobox(int indexToTest, int indexToFocus,
            String screenshotIdentifier) throws IOException {
        openTestURL();

        WebElement comboBox = vaadinElement(
                "/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot["
                        + indexToTest + "]/VComboBox[0]");
        WebElement comboBoxFocus = vaadinElement(
                "/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot["
                        + indexToFocus + "]/VComboBox[0]");

        // Select an element from the first (to test) combobox.

        clickElement(
                comboBox.findElement(By.className("v-filterselect-button")));
        waitForPopup(comboBox);
        WebElement comboBoxPopup = vaadinElement(
                "/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot["
                        + indexToTest + "]/VComboBox[0]#popup");
        clickElement(comboBoxPopup.findElements(By.tagName("td")).get(2));

        // Select an element from the second (to focus) combobox to remove
        // focus from the first combobox

        clickElement(comboBoxFocus
                .findElement(By.className("v-filterselect-button")));
        waitForPopup(comboBoxFocus);
        comboBoxPopup = vaadinElement(
                "/VVerticalLayout[0]/Slot[2]/VVerticalLayout[0]/Slot["
                        + indexToFocus + "]/VComboBox[0]#popup");
        clickElement(comboBoxPopup.findElements(By.tagName("td")).get(2));

        // click the button of the first combobox. This would reveal the
        // unwanted behavior.

        clickElement(
                comboBox.findElement(By.className("v-filterselect-button")));

        // sadly, screenshot comparison is the only reasonable way to test a
        // rendering issue.

        compareScreen(screenshotIdentifier);

    }

    private void waitForPopup(final WebElement comboBox) {
        waitUntilNot(
                input -> comboBox.findElements(By.vaadin("#popup")).isEmpty(),
                10);
    }

}
