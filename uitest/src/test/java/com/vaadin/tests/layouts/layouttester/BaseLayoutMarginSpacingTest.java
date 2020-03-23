package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class BaseLayoutMarginSpacingTest extends MultiBrowserTest {

    @Test
    public void LayoutMarginSpacing() throws IOException, InterruptedException {
        openTestURL();
        sleep(500);
        compareScreen("initial");
        String[] states = { "marginOnSpaceOff", "marginOnSpaceOn",
                "marginOffSpaceOn" };
        ButtonElement marginBtn = $(ButtonElement.class).get(0);
        ButtonElement spaceBtn = $(ButtonElement.class).get(1);
        marginBtn.click();
        sleep(1000);
        compareScreen(states[0]);
        spaceBtn.click();
        sleep(1000);
        compareScreen(states[1]);
        marginBtn.click();
        sleep(1000);
        compareScreen(states[2]);
    }
}
