/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.push;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.WebsocketTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class PushConfigurationTest extends AbstractTestUI {

    public static class PushConfigurationWebsocket extends WebsocketTest {

        @Test
        public void testWebsocketAndStreaming() {
            setDebug(true);
            openTestURL();
            // Websocket
            Assert.assertEquals(1, getServerCounter());
            new Select(getTransportSelect()).selectByVisibleText("WEBSOCKET");
            new Select(getPushModeSelect()).selectByVisibleText("AUTOMATIC");
            Assert.assertTrue(vaadinElement(
                    "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[5]/VLabel[0]/domChild[0]")
                    .getText()
                    .matches(
                            "^[\\s\\S]*fallbackTransport: streaming[\\s\\S]*transport: websocket[\\s\\S]*$"));
            int counter = getServerCounter();

            for (int second = 0;; second++) {
                if (second >= 5) {
                    Assert.fail("timeout");
                }
                if (getServerCounter() >= (counter + 2)) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            // Use debug console to verify we used the correct transport type
            Assert.assertTrue(driver.getPageSource().contains(
                    "Push connection established using websocket"));
            Assert.assertFalse(driver.getPageSource().contains(
                    "Push connection established using streaming"));

            new Select(getPushModeSelect()).selectByVisibleText("DISABLED");

            // Streaming
            driver.get(getTestUrl());
            Assert.assertEquals(1, getServerCounter());

            new Select(getTransportSelect()).selectByVisibleText("STREAMING");
            new Select(getPushModeSelect()).selectByVisibleText("AUTOMATIC");
            Assert.assertTrue(vaadinElement(
                    "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[5]/VLabel[0]/domChild[0]")
                    .getText()
                    .matches(
                            "^[\\s\\S]*fallbackTransport: streaming[\\s\\S]*transport: streaming[\\s\\S]*$"));

            counter = getServerCounter();
            for (int second = 0;; second++) {
                if (second >= 5) {
                    Assert.fail("timeout");
                }
                if (getServerCounter() >= (counter + 2)) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            // Use debug console to verify we used the correct transport type
            Assert.assertFalse(driver.getPageSource().contains(
                    "Push connection established using websocket"));
            Assert.assertTrue(driver.getPageSource().contains(
                    "Push connection established using streaming"));

        }

        private WebElement getPushModeSelect() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VNativeSelect[0]/domChild[0]");
        }

        private WebElement getTransportSelect() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[0]/VVerticalLayout[0]/Slot[1]/VNativeSelect[0]/domChild[0]");
        }

        private int getServerCounter() {
            return Integer.parseInt(getServerCounterElement().getText());
        }

        private WebElement getServerCounterElement() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VLabel[0]");
        }
    }

    private ObjectProperty<Integer> counter = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> counter2 = new ObjectProperty<Integer>(0);

    private final Timer timer = new Timer(true);

    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            access(new Runnable() {
                @Override
                public void run() {
                    counter2.setValue(counter2.getValue() + 1);
                }
            });
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new PushConfigurator(this));
        spacer();

        /*
         * Client initiated push.
         */
        Label lbl = new Label(counter);
        lbl.setCaption("Client counter (click 'increment' to update):");
        addComponent(lbl);

        addComponent(new Button("Increment", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter.setValue(counter.getValue() + 1);
            }
        }));

        spacer();

        /*
         * Server initiated push.
         */
        lbl = new Label(counter2);
        lbl.setCaption("Server counter (updates each 1s by server thread) :");
        addComponent(lbl);

        addComponent(new Button("Reset", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter2.setValue(0);
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "This test tests the very basic operations of push. "
                + "It tests that client initiated changes are "
                + "recieved back to the client as well as server "
                + "initiated changes are correctly updated to the client.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11494;
    }

    private void spacer() {
        addComponent(new Label("<hr/>", ContentMode.HTML));
    }

    @Override
    public void attach() {
        super.attach();
        timer.scheduleAtFixedRate(task, new Date(), 1000);
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
