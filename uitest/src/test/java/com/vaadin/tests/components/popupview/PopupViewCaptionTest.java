package com.vaadin.tests.components.popupview;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PopupViewCaptionTest extends MultiBrowserTest {

    @Test
    public void testCaption() {
        openTestURL();

        WebElement caption = driver.findElement(By.className("v-caption"));
        assertNotNull(caption);

        List<WebElement> elements = caption.findElements(By.xpath("*"));

        boolean foundCaptionText = false;
        for (WebElement element : elements) {
            if ("Popup Caption:".equals(element.getText())) {
                foundCaptionText = true;
                break;
            }
        }
        assertTrue("Unable to find caption text", foundCaptionText);
    }

}
