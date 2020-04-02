package com.vaadin.tests.layouts.layouttester.VLayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.layouts.layouttester.BaseComponentSizingTest;

public class VComponentSizingTest extends BaseComponentSizingTest {

    @Override
    public void ComponentSizing() throws IOException, InterruptedException {
        openTestURL();
        sleep(500);

        // The layout is too high to fit into one screenshot, we need to scroll
        // down to see relevant content. And keep doing it since interacting
        // with the controls scrolls back up again.

        List<VerticalLayoutElement> layouts = $(VerticalLayoutElement.class)
                .all();
        assertEquals(5, layouts.size());
        VerticalLayoutElement lastLayout = layouts.get(4);

        compareScreen("initial");

        new Actions(driver).moveToElement(lastLayout).build().perform();
        compareScreen("scrolled");

        String[] states = { "setSize350px", "setSize_-1px", "setSize75Percent",
                "setSize100Percent" };
        List<ButtonElement> buttons = $(ButtonElement.class).all();
        int index = 0;
        // go through all buttons click them and see result
        for (ButtonElement btn : buttons) {
            btn.click();
            sleep(500);
            new Actions(driver).moveToElement(lastLayout).build().perform();
            compareScreen(states[index]);

            index++;
        }
    }
}
