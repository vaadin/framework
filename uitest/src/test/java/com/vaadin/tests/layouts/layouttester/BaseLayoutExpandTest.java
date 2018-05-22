package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class BaseLayoutExpandTest extends MultiBrowserTest {

    @Test
    public void LayoutExpand() throws IOException, InterruptedException {
        openTestURL();
        waitForElementPresent(By.className("v-table"));
        compareScreen("initial");
        String[] states = { "expand_100_0", "expand_50_50", "expand_25_75" };
        List<ButtonElement> buttons = $(ButtonElement.class).all();
        int index = 0;
        // go through all buttons click them and see result
        for (ButtonElement btn : buttons) {
            btn.click();
            waitForElementPresent(By.className("v-table"));
            compareScreen(states[index]);
            index++;
        }
    }
}