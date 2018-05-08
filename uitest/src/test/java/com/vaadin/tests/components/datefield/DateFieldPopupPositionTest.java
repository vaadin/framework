package com.vaadin.tests.components.datefield;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for date field popup calendar position.
 *
 * @author Vaadin Ltd
 */
public abstract class DateFieldPopupPositionTest extends MultiBrowserTest {

    @Test
    public void testPopupPosition() {
        openTestURL();

        int height = getFieldBottom() + 150;
        adjustBrowserWindow(height);

        openPopup();

        checkPopupPosition();
    }

    protected abstract void checkPopupPosition();

    protected WebElement getPopup() {
        return findElement(By.className("v-datefield-popup"));
    }

    private void adjustBrowserWindow(int height) {
        Dimension size = getDriver().manage().window().getSize();
        getDriver().manage().window()
                .setSize(new Dimension(size.getWidth(), height));
    }

    private int getFieldBottom() {
        DateFieldElement dateField = $(DateFieldElement.class).first();
        return dateField.getLocation().getY() + dateField.getSize().getHeight();
    }

    private void openPopup() {
        findElement(By.className("v-datefield-button")).click();
        if (!isElementPresent(By.className("v-datefield-popup"))) {
            findElement(By.className("v-datefield-button")).click();
        }
    }
}
