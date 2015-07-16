package com.vaadin.tests.components.textfield;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class NullRepresentationLegacyModeTest extends MultiBrowserTest {

    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();
        String without = getDriver().findElement(
                By.xpath("//div[@id='without']//input")).getAttribute("value");
        String with = getDriver().findElement(
                By.xpath("//div[@id='with']//input")).getAttribute("value");
        Assert.assertEquals("null", with);
        Assert.assertEquals("", without);
    }
}
