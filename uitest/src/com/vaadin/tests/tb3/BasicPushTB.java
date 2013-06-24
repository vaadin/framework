package com.vaadin.tests.tb3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.push.BasicPush;

public class BasicPushTB extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPush.class;
    }

    @Test
    public void runTest() throws Exception {
        // Test client initiated push
        assertEquals("0", getClientCounter().getText());
        getIncrementButton().click();
        assertEquals("1", getClientCounter().getText());
        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        assertEquals("4", getClientCounter().getText());

        // Test server initiated push
        getServerCounterResetButton().click();
        assertEquals("0", getServerCounter().getText());
        sleep(3000);
        assertEquals("1", getServerCounter().getText());
        sleep(3000);
        assertEquals("2", getServerCounter().getText());
    }

    private WebElement getServerCounter() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[4]/VLabel[0]");
    }

    private WebElement getServerCounterResetButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getIncrementButton() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
    }

    private WebElement getClientCounter() {
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]");
    }

}
