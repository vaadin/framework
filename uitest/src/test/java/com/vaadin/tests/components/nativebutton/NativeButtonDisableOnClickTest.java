package com.vaadin.tests.components.nativebutton;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class NativeButtonDisableOnClickTest extends MultiBrowserTest {

    @Test
    public void testButtonIsDisabled() {
        openTestURL();
        WebElement button = findElement(By.id("buttonId"));
        assertEquals(true, button.isEnabled());

        button.click();
        assertEquals(NativeButtonDisableOnClick.UPDATED_CAPTION,
                button.getText());
        assertEquals(false, button.isEnabled());

        button.click();
        assertEquals(NativeButtonDisableOnClick.UPDATED_CAPTION,
                button.getText());
    }
}
