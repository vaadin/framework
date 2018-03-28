package com.vaadin.tests.smoke;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class TableSqlContainerTest extends MultiBrowserTest {

    @Test
    public void sqlContainerSmokeTest() {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        char ch = 'A';
        for (int i = 0; i < 4; i++) {
            assertEquals(String.valueOf(i + 1), table.getCell(i, 0).getText());
            assertEquals(String.valueOf(ch) + i % 2,
                    table.getCell(i, 2).getText());
            if (i == 1) {
                ch++;
            }
        }

        table.getCell(1, 0).click();

        assertEquals("Selected: 2", findElement(By.id("selection")).getText());
    }

}
