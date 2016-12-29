package com.vaadin.tests.components.popupview;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledPopupViewTest extends MultiBrowserTest {

    @Test
    public void disabledPopupDoesNotOpen() {
        openTestURL();

        $(PopupViewElement.class).first().click();

        assertFalse($(ButtonElement.class).exists());
    }
}
