package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NativeSelectVisibleItemCountTest extends SingleBrowserTest {

    @Test
    public void changeItemCount() {
        openTestURL();
        WebElement select = $(NativeSelectElement.class).first()
                .findElement(By.xpath("select"));
        assertEquals("1", select.getAttribute("size"));
        selectMenuPath("Component", "Size", "Visible item count", "5");
        assertEquals("5", select.getAttribute("size"));
    }

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }
}
