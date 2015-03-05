package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowWithInvalidCloseListenerTest extends MultiBrowserTest {
    @Test
    public void testWindowClosesCorrectly() throws Exception {
        openTestURL();
        $(WindowElement.class).first()
                .findElement(By.className("v-window-closebox")).click();
        Assert.assertFalse("Window found when there should be none.",
                $(WindowElement.class).exists());
    }
}
