package com.vaadin.tests.components.table;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(table.getCell(LASTSELECTEDROW, 1).getText(), is("updated"));
        assertThat(table.getCell(LASTSELECTEDROW-1, 1).getText(), is("updated"));
    }

    private void selectRows() {
        TableElement table = getTable();
        table.getCell(FIRSTSELECTEDROW, 0).click();

        scrollToBottom();

        new Actions(getDriver()).keyDown(Keys.SHIFT).click(getTable().getCell(LASTSELECTEDROW, 0)).keyUp(Keys.SHIFT)
                .build().perform();
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void scrollToBottom() {
        testBenchElement(getTable().findElement(By.className("v-scrollable"))).scroll(ROWCOUNT * 30);

        waitUntilRowIsVisible(LASTSELECTEDROW);
    }

    private void waitUntilRowIsVisible(final int row) {
        waitUntil(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver input) {
                try {
                   return getTable().getCell(row, 0) != null;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }

}