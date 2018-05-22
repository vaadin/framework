package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollingWithSQLContainerTest extends MultiBrowserTest {

    @Test
    public void verifySQLContainerIndexOfIDNotCalled() {
        openTestURL();

        vaadinElement("/VVerticalLayout[0]/VButton[0]").click();

        Assert.assertTrue("SQLContainer indexOfId was called", driver
                .findElements(By.className("v-errorindicator")).isEmpty());
    }
}
