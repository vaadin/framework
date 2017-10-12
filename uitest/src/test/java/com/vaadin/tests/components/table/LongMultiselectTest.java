package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class LongMultiselectTest extends MultiBrowserTest {

    private int ROWCOUNT = 100;
    private int FIRSTSELECTEDROW = 4;
    private int LASTSELECTEDROW = 97;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingShiftClick();
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void selectedRowsAreUpdated() throws InterruptedException {
        openTestURL();

        selectRows();
        $(ButtonElement.class).first().click();

        TableElement table = getTable();
        assertEquals("updated", table.getCell(LASTSELECTEDROW, 1).getText());
        assertEquals("updated",
                table.getCell(LASTSELECTEDROW - 1, 1).getText());
    }

    private void selectRows() {
        TableElement table = getTable();
        table.getCell(FIRSTSELECTEDROW, 0).click();

        scrollToBottom();

        new Actions(getDriver()).keyDown(Keys.SHIFT)
                .click(getTable().getCell(LASTSELECTEDROW, 0)).keyUp(Keys.SHIFT)
                .build().perform();
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void scrollToBottom() {
        scrollTable(getTable(), ROWCOUNT, LASTSELECTEDROW);
    }

}
