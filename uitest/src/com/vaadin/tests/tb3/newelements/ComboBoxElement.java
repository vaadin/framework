package com.vaadin.tests.tb3.newelements;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elementsbase.ServerClass;

@ServerClass("com.vaadin.ui.ComboBox")
public class ComboBoxElement extends
        com.vaadin.testbench.elements.ComboBoxElement {

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

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        sendKeys(50, keysToSend);
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
}
