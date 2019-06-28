package com.vaadin.tests.components.treetable;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TreeTableMultiselectTest extends MultiBrowserTest {


    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME);
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSelectedCount() {
        openTestURL();
        clickRow(0);
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        clickRow(1);
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        new Actions(driver).release().perform();
        LabelElement labelElement = $(LabelElement.class).id("count");
        assertEquals("2", labelElement.getText());
    }

    private void clickRow(int index) {
        TreeTableElement treeTable = $(TreeTableElement.class).first();
        treeTable.getRow(index).click();
    }

}
