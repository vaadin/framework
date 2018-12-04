package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.TooltipTest;

/**
 * Test for check visibility of browser-dependent tootlip for Upload component.
 *
 * @author Vaadin Ltd
 */
public class UploadTitleWithTooltipTest extends TooltipTest {

    @Test
    public void testDropdownTable() throws Exception {
        openTestURL();

        List<WebElement> elements = findElements(By.tagName("input"));
        WebElement input = null;
        for (WebElement element : elements) {
            if ("file".equals(element.getAttribute("type"))) {
                input = element;
            }
        }

        assertNotNull("Input element with type 'file' is not found", input);

        checkTooltip(input, "tooltip");

        compareScreen("init");
    }

}
