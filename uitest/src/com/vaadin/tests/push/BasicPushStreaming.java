package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.annotations.Push;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.tb3.MultiBrowserTest;

@Push(transport = Transport.STREAMING)
public class BasicPushStreaming extends BasicPush {

    public static class BasicPushStreamingTest extends MultiBrowserTest {

        @Test
        public void testPush() {
            // Test client initiated push
            Assert.assertEquals(0, getClientCounter());
            getIncrementButton().click();
            Assert.assertEquals(
                    "Client counter not incremented by button click", 1,
                    getClientCounter());
            getIncrementButton().click();
            getIncrementButton().click();
            getIncrementButton().click();
            Assert.assertEquals(
                    "Four clicks should have incremented counter to 4", 4,
                    getClientCounter());

            // Test server initiated push
            getServerCounterResetButton().click();
            Assert.assertEquals(0, getServerCounter());
            sleep(3000);
            int serverCounter = getServerCounter();
            if (serverCounter < 1) {
                // No push has happened
                Assert.fail("No push has occured within 3s");
            }
            sleep(3000);
            if (getServerCounter() <= serverCounter) {
                // No push has happened
                Assert.fail("Only one push took place within 6s");

            }
        }

        private int getServerCounter() {
            return Integer.parseInt(getServerCounterElement().getText());
        }

        private int getClientCounter() {
            return Integer.parseInt(getClientCounterElement().getText());
        }

        private WebElement getServerCounterElement() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[4]/VLabel[0]");
        }

        private WebElement getServerCounterResetButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getIncrementButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getClientCounterElement() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]");
        }
    }

}
