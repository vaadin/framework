package com.vaadin.tests.themes.valo;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDisabledTest extends MultiBrowserTest {

    @Test
    public void disabledGrid() throws IOException {
        openTestURL();

        $(ButtonElement.class).caption("Disable").first().click();

        compareScreen("disabled");
    }
}
