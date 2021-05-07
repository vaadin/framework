package com.vaadin.tests.components.window;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowRefocusTest extends MultiBrowserTest {

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
        openTestURL();
        waitForElementPresent(By.id("modalWindowButton"));
        WebElement button = findElement(By.id("modalWindowButton"));
        button.click();

        waitForElementPresent(By.id("focusfield"));
        TextFieldElement tfe = $(TextFieldElement.class).id("focusfield");
        assertFalse("First TextField should not have focus",
                "this has been focused".equals(tfe.getValue()));

        WebElement curtain = findElement(
                org.openqa.selenium.By.className("v-window-modalitycurtain"));
        testBenchElement(curtain).click(getXOffset(curtain, 20),
                getYOffset(curtain, 20));

        pressKeyAndWait(Keys.TAB);
        pressKeyAndWait(Keys.TAB);
        pressKeyAndWait(Keys.TAB);

        assertTrue("First TextField should have received focus",
                "this has been focused".equals(tfe.getValue()));

    }

    protected void pressKeyAndWait(Keys key) {
        new Actions(driver).sendKeys(key).build().perform();
        sleep(100);
    }

}
