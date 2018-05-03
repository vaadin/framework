package com.vaadin.tests.components.nativeselect;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NativeSelectsFocusAndBlurListenerTests extends MultiBrowserTest {

    @Test
    public void testFocusAndBlurListener() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(200);
        menu("Component");
        menuSub("Listeners");
        menuSub("Focus listener");
        menu("Component");
        menuSub("Listeners");
        menuSub("Blur listener");

        findElement(By.tagName("body")).click();

        NativeSelectElement s = $(NativeSelectElement.class).first();
        s.selectByText("Item 3");
        getDriver().findElement(By.tagName("body")).click();

        // Somehow selectByText causes focus + blur + focus + blur on
        // Chrome/PhantomJS
        if (BrowserUtil.isChrome(getDesiredCapabilities())
                || BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            Assert.assertEquals("4. FocusEvent", getLogRow(1));
            Assert.assertEquals("5. BlurEvent", getLogRow(0));
        } else {
            Assert.assertEquals("2. FocusEvent", getLogRow(1));
            Assert.assertEquals("3. BlurEvent", getLogRow(0));
        }

    }

    @Override
    protected Class<?> getUIClass() {
        return NativeSelects.class;
    }

    private void menuSub(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();
        new Actions(getDriver()).moveByOffset(100, 0).build().perform();
    }

    private void menu(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();

    }

}
