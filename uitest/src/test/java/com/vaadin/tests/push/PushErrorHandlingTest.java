package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class PushErrorHandlingTest extends MultiBrowserTest {

    @Test
    public void testErrorHandling() {
        setPush(true);
        openTestURL();
        vaadinElementById("npeButton").click();
        assertEquals(
                "An error! Unable to invoke method click in com.vaadin.shared.ui.button.ButtonServerRpc",
                $(LabelElement.class).get(1).getText());

        WebElement table = vaadinElementById("testtable");
        WebElement row = table
                .findElement(By.xpath("//div[text()='Click for NPE']"));
        row.click();

        assertEquals("Internal error",
                vaadinElement("Root/VNotification[0]/HTML[0]/domChild[0]")
                        .getText());
    }
}
