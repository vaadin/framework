package com.vaadin.tests.tb3.newelements;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ComboBox")
public class ComboBoxElement
        extends com.vaadin.testbench.elements.ComboBoxElement {

    private static org.openqa.selenium.By bySuggestionPopup = By
            .vaadin("#popup");

    public WebElement getInputField() {
        return findElement(By.vaadin("#textbox"));
    }

    @Override
    public String getText() {
        return getInputField().getAttribute("value");
    }

    @Override
    public void clear() {
        getInputField().clear();
    }

    public WebElement getSuggestionPopup() {
        return findElement(bySuggestionPopup);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        sendKeys(50, keysToSend);
    }

    @Override
    public void openPopup() {
        if (isFirefox()) {
            getTestBenchCommandExecutor().executeScript("arguments[0].click();",
                    findElement(By.vaadin("#button")));
        } else {
            super.openPopup();
        }
    }

    /**
     * Use this method to simulate typing into an element, which may set its
     * value.
     *
     * @param delay
     *            delay after sending each individual key (mainly needed for
     *            PhantomJS)
     * @param keysToSend
     *            keys to type into the element
     */
    public void sendKeys(int delay, CharSequence... keysToSend) {
        WebElement input = getInputField();

        for (CharSequence key : keysToSend) {
            input.sendKeys(key);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    public boolean isFirefox() {
        return BrowserType.FIREFOX.equals(getCapabilities().getBrowserName());
    }
}
