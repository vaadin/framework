package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
abstract class PushConfigurationTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return PushConfiguration.class;
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        setDebug(true);

        openTestURL("restartApplication");
        disablePush();
    }

    protected String getStatusText() {
        WebElement statusLabel = vaadinElementById("status");

        return statusLabel.getText();
    }

    protected void disablePush() throws InterruptedException {
        getPushModeSelect().selectByText("Disabled");

        int counter = getServerCounter();
        sleep(2000);
        assertEquals("Server count changed without push enabled", counter,
                getServerCounter());
    }

    protected NativeSelectElement getPushModeSelect() {
        return $(NativeSelectElement.class).caption("Push mode").first();
    }

    protected NativeSelectElement getTransportSelect() {
        return $(NativeSelectElement.class).caption("Transport").first();
    }

    protected int getServerCounter() {
        return Integer.parseInt(getServerCounterElement().getText());
    }

    protected WebElement getServerCounterElement() {
        return vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[5]/VLabel[0]");
    }

    protected void waitForServerCounterToUpdate() {
        int counter = getServerCounter();
        final int waitCounter = counter + 2;
        waitUntil(input -> getServerCounter() >= waitCounter);
    }
}
