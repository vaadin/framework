package com.vaadin.tests.components.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SelectAllConstantViewportTest extends MultiBrowserTest {

    @Test
    public void testViewportUnchanged() throws IOException {
        openTestURL();

        CheckBoxElement checkbox = $(CheckBoxElement.class).first();

        WebElement row = $(TableElement.class).first().getCell(190, 0);
        final WebElement scrollPositionDisplay = getDriver()
                .findElement(By.className("v-table-scrollposition"));
        waitUntilNot(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return scrollPositionDisplay.isDisplayed();
            }
        }, 10);

        int rowLocation = row.getLocation().getY();

        // use click x,y with non-zero offset to actually toggle the checkbox.
        // (#13763)
        checkbox.click(5, 5);
        int newRowLocation = row.getLocation().getY();

        assertThat(newRowLocation, is(rowLocation));

    }
}
