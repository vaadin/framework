package com.vaadin.tests.components.datefield;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupDateTimeFieldStatesTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen()
            throws IOException, InterruptedException {
        openTestURL();

        // wait until loading indicator becomes invisible
        WebElement loadingIndicator = findElement(
                By.className("v-loading-indicator"));
        Pattern pattern = Pattern.compile("display: *none;");
        waitUntil(driver -> {
            return pattern.matcher(loadingIndicator.getAttribute("style"))
                    .find();
        });

        compareScreen("dateFieldStates");
    }

}
