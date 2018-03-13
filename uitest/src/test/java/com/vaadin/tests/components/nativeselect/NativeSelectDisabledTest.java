package com.vaadin.tests.components.nativeselect;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NativeSelectDisabledTest extends MultiBrowserTest {

    @Test
    public void testDisabled() {
        openTestURL();

        NativeSelectElement el = $(NativeSelectElement.class).first();
        assertEquals(false, el.isEnabled());
        ButtonElement but = $(ButtonElement.class).first();
        but.click();
        assertEquals(true, el.isEnabled());
        assertEquals(null, el.getSelectElement().getAttribute("disabled"));
        but.click();
        System.out.println(el.getSelectElement().getText());
        assertEquals("true", el.getSelectElement().getAttribute("disabled"));
    }
}