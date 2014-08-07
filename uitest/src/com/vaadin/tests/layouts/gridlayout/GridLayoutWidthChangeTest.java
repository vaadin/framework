package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GridLayoutWidthChangeTest extends MultiBrowserTest {

    @Test
    public void layoutIsReduced() throws IOException {
        openTestURL();

        compareScreen("initial");

        $(ButtonElement.class).caption("Reduce GridLayout parent width")
                .first().click();

        compareScreen("buttonMoved");
    }
}