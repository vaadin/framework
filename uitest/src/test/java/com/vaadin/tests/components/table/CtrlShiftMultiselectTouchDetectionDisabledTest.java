package com.vaadin.tests.components.table;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CtrlShiftMultiselectTouchDetectionDisabledTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11);
    }

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
        assertEquals("6", labelElement.getText());

    }

    private void clickRow(int index) {
        TableElement tableElement = $(TableElement.class).first();
        tableElement.getRow(index).click();
    }

}
