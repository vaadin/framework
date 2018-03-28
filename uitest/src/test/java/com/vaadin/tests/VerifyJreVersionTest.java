package com.vaadin.tests;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class VerifyJreVersionTest extends SingleBrowserTest {

    @Test
    public void verifyJreVersion() {
        openTestURL();

        WebElement jreVersionLabel = vaadinElementById("jreVersionLabel");

        assertThat(jreVersionLabel.getText(),
                CoreMatchers.startsWith("Using Java 1.8.0_"));
    }

}
