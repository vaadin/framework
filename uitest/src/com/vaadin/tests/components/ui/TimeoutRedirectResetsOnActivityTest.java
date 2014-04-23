/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TimeoutRedirectResetsOnActivityTest extends MultiBrowserTest {
    @Test
    @Ignore("The test modifies the system messages, which are global and the changes will affect other tests")
    public void verifyRedirectWorks() throws Exception {
        setDebug(true);
        openTestURL();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 30000) {
            clickTheButton();
            Thread.sleep(1000);
        }

        assertTrue("button disappeared before timeout", buttonIsStillThere());

        Thread.sleep(30000);
        assertTrue("no redirection occurred within 30 seconds",
                !buttonIsStillThere());
    }

    private boolean buttonIsStillThere() {
        try {
            return getButton() != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void clickTheButton() {
        getButton().click();
    }

    private WebElement getButton() {
        /*
         * For some reason, the vaadinElement() method doesn't work when tests
         * are run outside of "/run/" and "/run-push/" contexts. The given error
         * message says that the generated Vaadin path doesn't match any
         * elements, but when that selector is put into the recorder, the
         * recorder finds it.
         * 
         * XPath works fine.
         */
        /*-
        return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VButton[0]");
         */
        return getDriver().findElement(
                By.xpath("//div[contains(@class,'v-button')]"));
    }

    @Override
    protected String getDeploymentPath() {
        /*
         * AbstractTB3Test assumes only /run/ and /run-push/ contexts, so this
         * method needs some overriding.
         */
        return "/12446/"
                + TimeoutRedirectResetsOnActivity.class.getCanonicalName()
                + "?restartApplication&debug";
    }
}
