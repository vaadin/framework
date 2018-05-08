package com.vaadin.tests.components.window;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.Browser;

/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowRefocusTest extends ModalWindowFocusTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Chrome doesn't support clicking on the modality curtain
        return getBrowserCapabilities(Browser.IE11, Browser.EDGE,
                Browser.FIREFOX);
    }

    @Override
    protected Class<?> getUIClass() {
        return ModalWindowFocus.class;
    }

    /**
     * Open modal window -> click modality curtain to remove focus from Window
     * -> press tab thrice so that focus goes into Window again and focuses the
     * text field so that the focus event is fired.
     */
    @Test
    public void testFocusOutsideModal() {
        waitForElementPresent(By.id("modalWindowButton"));
        WebElement button = findElement(By.id("modalWindowButton"));
        button.click();
        waitForElementPresent(By.id("focusfield"));
        WebElement curtain = findElement(
                org.openqa.selenium.By.className("v-window-modalitycurtain"));
        curtain.click();

        pressKeyAndWait(Keys.TAB);
        pressKeyAndWait(Keys.TAB);
        pressKeyAndWait(Keys.TAB);

        TextFieldElement tfe = $(TextFieldElement.class).id("focusfield");
        assertTrue("First TextField should have received focus",
                "this has been focused".equals(tfe.getValue()));

    }

}
