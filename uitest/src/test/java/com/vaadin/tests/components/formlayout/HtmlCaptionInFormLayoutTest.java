package com.vaadin.tests.components.formlayout;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class HtmlCaptionInFormLayoutTest extends SingleBrowserTest {
    @Test
    public void testHtmlCaptions() {
        openTestURL();

        List<WebElement> captions = getDriver()
                .findElements(By.cssSelector(".v-formlayout-captioncell span"));

        assertEquals("Should be two formlayout captions", 2, captions.size());

        assertEquals("Contains HTML", captions.get(0).getText());
        assertEquals("Contains <b>HTML</b>", captions.get(1).getText());
    }

    @Test
    public void testHtmlCaptionToggle() {
        openTestURL();

        $(ButtonElement.class).caption("Toggle").first().click();

        List<WebElement> captions = getDriver()
                .findElements(By.cssSelector(".v-formlayout-captioncell span"));

        assertEquals("Should be two formlayout captions", 2, captions.size());

        assertEquals("Contains <b>HTML</b>", captions.get(0).getText());
        assertEquals("Contains HTML", captions.get(1).getText());
    }

}
