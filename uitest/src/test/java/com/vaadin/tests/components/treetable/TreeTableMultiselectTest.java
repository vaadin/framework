package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeTableMultiselectTest extends SingleBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSelectedCount() {
        openTestURL();
        clickRow(0);
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        clickRow(2);
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        new Actions(driver).release().perform();
        LabelElement labelElement = $(LabelElement.class).id("count");
        assertEquals("Unexpected amount of selected rows", "3",
                labelElement.getText());
    }

    private void clickRow(int index) {
        TreeTableElement treeTable = $(TreeTableElement.class).first();
        treeTable.getRow(index).click();
    }

}
