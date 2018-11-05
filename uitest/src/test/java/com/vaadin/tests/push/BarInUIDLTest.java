package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class BarInUIDLTest extends MultiBrowserTest {
    @Test
    public void sendBarInUIDL() {
        openTestURL();
        getButton().click();
        assertEquals("Thank you for clicking | bar", vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]")
                        .getText());
        getButton().click();
        assertEquals("Thank you for clicking | bar", vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VLabel[0]")
                        .getText());
    }

    private WebElement getButton() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VButton[0]");
    }
}
