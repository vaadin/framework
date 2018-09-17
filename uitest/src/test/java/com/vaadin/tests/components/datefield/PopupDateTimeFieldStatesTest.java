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

        waitUntilLoadingIndicatorNotVisible();

        compareScreen("dateFieldStates");
    }

}
