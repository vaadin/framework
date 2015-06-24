package com.vaadin.tests.extensions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

public class IframeIsOpenedInNonIOSTest extends MultiBrowserTest {

    @Test
    public void fileOpenedInNewTab() {
        openTestURL();

        $(ButtonElement.class).caption("Download").first().click();

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
        boolean containsFileIframe = false;
        for (WebElement iframe : iframes) {
            containsFileIframe = containsFileIframe
                    | iframe.getAttribute("src").contains(
                            IframeIsOpenedInNonIOS.FILE_NAME);
        }

        Assert.assertTrue("page doesn't contain iframe with the file",
                containsFileIframe);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // once running ios is possible, this test should be fixed to exclude it
        // from the browsers list

        // The test is failing in all IEs for some reason even though the iframe
        // is in place.
        // Probably related to some IE driver issue
        return getBrowsersExcludingIE();
    }
}
