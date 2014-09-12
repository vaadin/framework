package com.vaadin.tests.components.formlayout;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;

public class TableInFormLayoutCausesScrollingTest extends MultiBrowserTest {

    @Test
    public void pageIsNotScrolled() throws IOException {
        openTestURL();

        new Actions(driver).sendKeys(Keys.PAGE_DOWN).perform();

        $(TableElement.class).first().getCell(2, 0).click();

        compareScreen("scrolledDown");
    }
}