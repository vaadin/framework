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
package com.vaadin.tests.components.textfield;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextFieldsValueChangeModeTest extends MultiBrowserTest {

    @Test
    public void textFieldEager() {
        testEager("textfield-eager");
    }

    @Test
    public void textAreaEager() {
        testEager("textarea-eager");
    }

    @Test
    @Ignore("No support for typing in a RichTextArea in TestBench")
    public void richTextAreaEager() {
        testEager("richtextarea-eager");
    }

    @Test
    public void textFieldDefault() {
        testDefault("textfield-default");
    }

    @Test
    public void textAreaDefault() {
        testDefault("textarea-default");
    }

    @Test
    @Ignore("No support for typing in a RichTextArea in TestBench")
    public void richTextAreaDefault() {
        testEager("richtextarea-default");
    }

    @Test
    public void textFieldTimeout() {
        testTimeout("textfield-timeout");
    }

    @Test
    public void textAreaTimeout() {
        testTimeout("textarea-timeout");
    }

    @Test
    @Ignore("No support for typing in a RichTextArea in TestBench")
    public void richTextAreaTimeout() {
        testEager("richtextarea-timeout");
    }

    private void testEager(String id) {
        openTestURL();
        WebElement eagerTextField = findElement(By.id(id));
        eagerTextField.sendKeys("f");
        eagerTextField.sendKeys("o");
        eagerTextField.sendKeys("o");
        assertLog(id, "f", "fo", "foo");
    }

    private void testDefault(String id) {
        openTestURL();
        WebElement eagerTextField = findElement(By.id(id));
        eagerTextField.sendKeys("f");
        eagerTextField.sendKeys("o");
        eagerTextField.sendKeys("o");
        sleep(400); // Default timeout is 400ms
        assertLog(id, "foo");

    }

    private void testTimeout(String id) {
        openTestURL();
        WebElement eagerTextField = findElement(By.id(id));
        eagerTextField.sendKeys("foo");
        sleep(1000); // Timer set to 1000ms
        eagerTextField.sendKeys("baa");
        sleep(1000); // Timer set to 1000ms
        assertLog(id, "foo", "foobaa");
    }

    private void assertLog(String id, String... messages) {
        for (int i = 0; i < messages.length; i++) {
            String expected = "Value change event for " + id + ", new value: '"
                    + messages[i] + "'";

            String log = getLogRow(messages.length - 1 - i);
            int tail = log.indexOf(" Cursor at");
            if (tail != -1) {
                log = log.substring(0, tail);
            }
            Assert.assertEquals(expected, log);
        }

    }
}
