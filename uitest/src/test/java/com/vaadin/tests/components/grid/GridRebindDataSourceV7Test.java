package com.vaadin.tests.components.grid;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;

public class GridRebindDataSourceV7Test extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
    }

    @Test
    public void testNoNPE() {
        findElement(By.id("changeContainer")).click();
        assertNoErrorNotifications();
    }
}
