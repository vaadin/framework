package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.UIDetachedException;

public class EnableDisablePush extends AbstractTestUI {

    public static class EnableDisablePushTest extends MultiBrowserTest {
        @Test
        public void testEnablePushWhenUsingPolling() throws Exception {
            openTestURL();

            assertEquals("1. Push enabled", getLogRow(0));

            getDisablePushButton().click();
            assertEquals("3. Push disabled", getLogRow(0));

            getEnablePollButton().click();
            assertEquals("5. Poll enabled", getLogRow(0));

            getEnablePushButton().click();
            assertEquals("7. Push enabled", getLogRow(0));

            getDisablePollButton().click();
            assertEquals("9. Poll disabled", getLogRow(0));

            getDisablePushButtonAndReenableFromBackground().click();
            Thread.sleep(2500);
            assertEquals("16. Polling disabled, push enabled", getLogRow(0));

            getDisablePushButton().click();
            assertEquals("18. Push disabled", getLogRow(0));
        }

        private WebElement getDisablePushButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VButton[0]");
        }

        private WebElement getEnablePushButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VButton[0]");
        }

        private WebElement getDisablePollButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]");
        }

        private WebElement getEnablePollButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[3]/VButton[0]");
        }

        private WebElement getDisablePushButtonAndReenableFromBackground() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[4]/VButton[0]");
        }

    }

    private int c = 0;

    private Log log = new Log(15);

    private final Timer timer = new Timer(true);

    private final class CounterTask extends TimerTask {

        @Override
        public void run() {

            try {
                while (true) {
                    TimeUnit.MILLISECONDS.sleep(500);

                    access(new Runnable() {
                        @Override
                        public void run() {
                            log.log("Counter = " + c++);
                            if (c == 3) {
                                log.log("Disabling polling, enabling push");
                                getPushConfiguration().setPushMode(
                                        PushMode.AUTOMATIC);
                                setPollInterval(-1);
                                log.log("Polling disabled, push enabled");
                            }
                        }
                    });
                    if (c == 3) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
            } catch (UIDetachedException e) {
            }
        }
    };

    @Override
    protected void setup(VaadinRequest request) {

        getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        log.log("Push enabled");

        addComponent(new Button("Disable push", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Disabling push");
                getPushConfiguration().setPushMode(PushMode.DISABLED);
                log.log("Push disabled");
            }
        }));

        addComponent(new Button("Enable push", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Enabling push");
                getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                log.log("Push enabled");
            }
        }));

        addComponent(new Button("Disable polling", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Disabling poll");
                setPollInterval(-1);
                log.log("Poll disabled");
            }
        }));

        addComponent(new Button("Enable polling", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                log.log("Enabling poll");
                setPollInterval(1000);
                log.log("Poll enabled");
            }
        }));

        addComponent(new Button(
                "Disable push, re-enable from background thread",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        log.log("Disabling push, enabling polling");
                        getPushConfiguration().setPushMode(PushMode.DISABLED);
                        setPollInterval(1000);
                        timer.schedule(new CounterTask(), new Date());
                        log.log("Push disabled, polling enabled");
                    }
                }));

        addComponent(log);
    }

    @Override
    protected String getTestDescription() {
        return "Test dynamically enablind and disabling push";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12226;
    }
}
