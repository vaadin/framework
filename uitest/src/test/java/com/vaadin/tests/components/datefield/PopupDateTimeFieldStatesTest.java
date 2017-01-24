package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

@RunLocally(Browser.PHANTOMJS)
public class PopupDateTimeFieldStatesTest extends MultiBrowserTest {

    private boolean indicatorHasFullWidth;

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen()
            throws IOException, InterruptedException {
        openTestURL();

        // wait until loading indicator becomes invisible
        WebElement loadingIndicator = findElement(
                By.className("v-loading-indicator"));
        waitUntil(driver -> {
            if (indicatorHasFullWidth) {
                return true;
            }
            if (driver.manage().window().getSize()
                    .getWidth() == loadingIndicator.getSize().getWidth()) {
                indicatorHasFullWidth = true;
            }
            return false;
        });

        compareScreen("dateFieldStates");
    }

}
