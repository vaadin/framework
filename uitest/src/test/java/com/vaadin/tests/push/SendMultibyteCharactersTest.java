/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

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
        for (int i = 0; i < 20; i++) {
            text.append("之は日本語です、テストです。");
        }

        // timing matters for Firefox, this needs to be before sendKeys
        clearDebugMessages();

        textArea.sendKeys(text.toString());

        findElement(By.tagName("body")).click();

        waitForDebugMessage("RPC invocations to be sent to the server:", 5);
        waitForDebugMessage("Handling message from server", 10);
    }

}
