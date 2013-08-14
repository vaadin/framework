package com.vaadin.tests.push;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.annotations.Push;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Push
public class BasicPush extends AbstractTestUI {

    public static class BasicPushTest extends MultiBrowserTest {

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
            getServerCounterStartButton().click();
            try {
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
            } finally {
                // Avoid triggering push assertions
                getServerCounterStopButton().click();
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

        private WebElement getServerCounterStartButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getServerCounterStopButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[6]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getIncrementButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getClientCounterElement() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]");
        }
    }

    private ObjectProperty<Integer> counter = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> counter2 = new ObjectProperty<Integer>(0);

    private final Timer timer = new Timer(true);

    private TimerTask task;

    @Override
    protected void setup(VaadinRequest request) {

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
        lbl.setCaption("Server counter (updates each 3s by server thread) :");
        addComponent(lbl);

        addComponent(new Button("Start timer", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter2.setValue(0);
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {

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
                timer.scheduleAtFixedRate(task, 3000, 3000);
            }
        }));

        addComponent(new Button("Stop timer", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (task != null) {
                    task.cancel();
                    task = null;
                }
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
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
