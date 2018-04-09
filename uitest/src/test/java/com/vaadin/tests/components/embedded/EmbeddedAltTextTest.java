package com.vaadin.tests.components.embedded;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmbeddedAltTextTest extends MultiBrowserTest {

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-embedded"));
    }

    @Test
    public void testEmbeddedAltText() {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();

        assertEquals("Alt text of the image", getAltText(embedded));
        assertHtmlSource("Alt text of the object");

        $(ButtonElement.class).first().click();

        assertEquals("New alt text of the image!", getAltText(embedded));
        assertHtmlSource("New alt text of the object!");
    }

    private void assertHtmlSource(String html) {
        String pageSource = driver.getPageSource();
        assertTrue("Page source does not contain '" + html + "'",
                pageSource.contains(html));
    }

    private String getAltText(EmbeddedElement embedded) {
        return embedded.findElement(By.vaadin("/domChild[0]"))
                .getAttribute("alt");
    }
}
