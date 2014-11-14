package com.vaadin.tests.push;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

@TestCategory("push")
public abstract class SendMultibyteCharactersTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return SendMultibyteCharacters.class;
    }

    protected abstract String getTransport();

    @Test
    public void transportSupportsMultibyteCharacters() {
        setDebug(true);
        openTestURL("transport=" + getTransport());
        openDebugLogTab();

        TextAreaElement textArea = $(TextAreaElement.class).first();

        StringBuilder text = new StringBuilder();
        for(int i=0;i < 20;i++) {
            text.append("之は日本語です、テストです。");
        }

        textArea.sendKeys(text.toString());

        clearDebugMessages();

        findElement(By.tagName("body")).click();

        waitForDebugMessage("Variable burst to be sent to server:", 5);
        waitForDebugMessage("Handling message from server", 10);
    }

}