package com.vaadin.tests.components.panel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PanelChangeContentsTest extends MultiBrowserTest {

    @Test
    public void testReattachComponentUsingPush() {
        setPush(true);
        openTestURL();

        assertEquals("stats", vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());
        vaadinElement(
                "/VVerticalLayout[0]/Slot[0]/VHorizontalLayout[0]/Slot[1]/VButton[0]/domChild[0]/domChild[0]")
                        .click();
        assertEquals("companies", vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());
        vaadinElement(
                "/VVerticalLayout[0]/Slot[0]/VHorizontalLayout[0]/Slot[0]/VButton[0]/domChild[0]/domChild[0]")
                        .click();
        assertEquals("stats", vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VPanel[0]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                        .getText());

        assertElementNotPresent(By.className("caption-with-html"));
    }
}
