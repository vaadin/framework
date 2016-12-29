package com.vaadin.tests.layouts.gridlayout;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutMoveComponentTest extends MultiBrowserTest {

    @Test
    public void componentsShouldMoveRight() throws IOException {
        openTestURL();

        compareScreen("all-left");

        clickButtonWithCaption("Shift label right");
        compareScreen("label-right");

        clickButtonWithCaption("Shift button right");
        compareScreen("label-button-right");

        clickButtonWithCaption("Shift text field right");
        compareScreen("label-button-textfield-right");
    }

    private void clickButtonWithCaption(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

}
