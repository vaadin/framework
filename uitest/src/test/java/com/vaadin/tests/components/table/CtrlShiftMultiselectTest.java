package com.vaadin.tests.components.table;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CtrlShiftMultiselectTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingShiftClick();
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testSelectionRangeDragging() throws IOException {
        openTestURL();
        new Actions(driver).moveToElement(getAllRows().get(3)).click()
                .keyDown(Keys.SHIFT)
                .moveToElement(getAllRows().get(8)).click()
                .keyUp(Keys.SHIFT).build().perform();

        dragRows(5, 700, 0);
        compareScreen("draggedMultipleRows");
        new Actions(driver).release().perform();
    }

    private void dragRows(int dragIdx, int xOffset, int yOffset) {
        List<WebElement> rows = getAllRows();
        new Actions(driver).moveToElement(rows.get(dragIdx)).clickAndHold()
                .moveByOffset(5, 0).perform();
        new Actions(driver).moveByOffset(xOffset - 5, yOffset).perform();

    }

    private List<WebElement> getAllRows() {
        WebElement table = vaadinElement(
                "/VVerticalLayout[0]/VVerticalLayout[0]/VScrollTable[0]");
        return table.findElements(By.cssSelector(".v-table-table tr"));

    }
}
