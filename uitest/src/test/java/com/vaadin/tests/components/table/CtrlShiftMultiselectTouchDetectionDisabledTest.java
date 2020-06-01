package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CtrlShiftMultiselectTouchDetectionDisabledTest
        extends SingleBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSelectedCount() {
        openTestURL();
        clickRow(3);
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        clickRow(8);
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        new Actions(driver).release().perform();
        LabelElement labelElement = $(LabelElement.class).id("count");
        assertEquals("Unexpected amount of selected rows", "6",
                labelElement.getText());

    }

    private void clickRow(int index) {
        TableElement tableElement = $(TableElement.class).first();
        tableElement.getRow(index).click();
    }

}
