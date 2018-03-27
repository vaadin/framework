package com.vaadin.tests.widgetset.server;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ClientRpcClassTest extends MultiBrowserTest {

    @Test
    public void pauseDisplayed() {
        openTestURL();

        WebElement element = getDriver()
                .findElement(By.id(ClientRpcClass.TEST_COMPONENT_ID));
        Assert.assertEquals("pause", element.getText());
    }
}
