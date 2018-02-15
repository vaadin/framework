package com.vaadin.tests.components;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarDownloadBrowserOpenerUITest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        //alerts do not work properly on PhantomJS
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Test
    public void testTriggerExtension() {
        openTestURL();
        MenuBarElement first = $(MenuBarElement.class).first();
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();

        first.clickItem("TestExtension", "AddTrigger");
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();
        checkAndCloseAlert();

        first.clickItem("TestExtension", "RemoveTrigger");
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();
    }

    private void checkAndCloseAlert() {
        Alert alert = getDriver().switchTo().alert();
        Assert.assertEquals("Trigger",alert.getText());
        alert.dismiss();
    }

}
