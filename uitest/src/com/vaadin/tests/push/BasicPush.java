package com.vaadin.tests.push;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class BasicPush extends AbstractTestUI {

    public static class BasicPushTest extends MultiBrowserTest {

        @Test
        public void testPush() {
            // Test client initiated push
            Assert.assertEquals("0", getClientCounter().getText());
            getIncrementButton().click();
            Assert.assertEquals("1", getClientCounter().getText());
            getIncrementButton().click();
            getIncrementButton().click();
            getIncrementButton().click();
            Assert.assertEquals("4", getClientCounter().getText());

            // Test server initiated push
            getServerCounterResetButton().click();
            Assert.assertEquals("0", getServerCounter().getText());
            sleep(3000);
            Assert.assertEquals("1", getServerCounter().getText());
            sleep(3000);
            Assert.assertEquals("2", getServerCounter().getText());
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

        @Override
        protected boolean isPushEnabled() {
            return true;
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
        timer.scheduleAtFixedRate(task, new Date(), 3000);
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
