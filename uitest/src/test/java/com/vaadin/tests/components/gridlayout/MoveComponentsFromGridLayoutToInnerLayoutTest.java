package com.vaadin.tests.components.gridlayout;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MoveComponentsFromGridLayoutToInnerLayoutTest
        extends MultiBrowserTest {

    @Test
    public void buttonIsMovedInsideInnerLayout() throws IOException {
        openTestURL();

        $(ButtonElement.class).first().click();

        compareScreen("buttonClicked");
    }
}
