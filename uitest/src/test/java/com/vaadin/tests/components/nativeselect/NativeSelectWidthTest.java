package com.vaadin.tests.components.nativeselect;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class NativeSelectWidthTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void testWidthIs200Px() {
        WebElement nativeSelect = $(NativeSelectElement.class).first().findElement(By.tagName("select"));
        assertEquals(200, nativeSelect.getSize().getWidth());
        if (!BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            //PhantomJS does not support explicit <select> height
            assertEquals(120, nativeSelect.getSize().getHeight());
        }
    }
}
