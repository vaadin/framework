package com.vaadin.tests.push;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public abstract class BasicPushTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testPush() throws InterruptedException {
        openTestURL();

        getIncrementButton().click();
        testBench().disableWaitForVaadin();

        waitUntilClientCounterChanges(1);

        getIncrementButton().click();
        getIncrementButton().click();
        getIncrementButton().click();
        waitUntilClientCounterChanges(4);

        // Test server initiated push
        getServerCounterStartButton().click();
        waitUntilServerCounterChanges();
    }

    public static int getClientCounter(AbstractTB3Test t) {
        WebElement clientCounterElem = t
                .findElement(By.id(BasicPush.CLIENT_COUNTER_ID));
        return Integer.parseInt(clientCounterElem.getText());
    }

    protected WebElement getIncrementButton() {
        return getIncrementButton(this);
    }

    protected WebElement getServerCounterStartButton() {
        return getServerCounterStartButton(this);
    }

    public static int getServerCounter(AbstractTB3Test t) {
        WebElement serverCounterElem = t
                .findElement(By.id(BasicPush.SERVER_COUNTER_ID));
        return Integer.parseInt(serverCounterElem.getText());
    }

    public static WebElement getServerCounterStartButton(AbstractTB3Test t) {
        return t.findElement(By.id(BasicPush.START_TIMER_ID));
    }

    public static WebElement getServerCounterStopButton(AbstractTB3Test t) {
        return t.findElement(By.id(BasicPush.STOP_TIMER_ID));
    }

    public static WebElement getIncrementButton(AbstractTB3Test t) {
        return t.findElement(By.id(BasicPush.INCREMENT_BUTTON_ID));
    }

    protected void waitUntilClientCounterChanges(final int expectedValue) {
        waitUntil(input -> {
            try {
                return BasicPushTest
                        .getClientCounter(BasicPushTest.this) == expectedValue;
            } catch (NoSuchElementException e) {
                return false;
            }
        }, 10);
    }

    protected void waitUntilServerCounterChanges() {
        final int counter = BasicPushTest.getServerCounter(this);
        waitUntil(input -> BasicPushTest
                .getServerCounter(BasicPushTest.this) > counter, 10);
    }

}
