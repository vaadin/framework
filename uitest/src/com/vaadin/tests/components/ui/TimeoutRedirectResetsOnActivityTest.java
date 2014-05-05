/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.RetryOnFail;

public class TimeoutRedirectResetsOnActivityTest extends MultiBrowserTest {

    @Rule
    // Timing issues are really hard to resolve in a way that this test would be
    // 100% reliable on all browsers. Hence we shall allow one retry.
    public RetryOnFail retry = new RetryOnFail();

    private int waitBeforeActivity = 4000;
    private int communicationOverhead = 2000;

    private static int i = 0;

    @Test
    @Ignore("The test modifies the system messages, which are global and the changes will affect other tests")
    public void verifyRedirectWorks() throws Exception {
        setDebug(true);
        openTestURL();

        long startedTime = getTime("startedTime");
        long originalExpireTime = getTime("originalExpireTime");

        Thread.sleep(waitBeforeActivity);
        hitButton("reset");

        Thread.sleep(200);

        long actualExpireTime = getTime("actualExpireTime");

        Thread.sleep(originalExpireTime - startedTime - waitBeforeActivity);

        assertThat(driver.getCurrentUrl(), is(getTestUrl()));

        testBench().disableWaitForVaadin();
        Thread.sleep(actualExpireTime - originalExpireTime
                + communicationOverhead);

        assertThat(driver.getCurrentUrl(), is(not(getTestUrl())));
    }

    private long getTime(String id) {
        WebElement element = vaadinElementById(id);
        return Long.parseLong(element.getText());
    }

    @Override
    protected String getTestUrl() {
        return super.getTestUrl() + "?restartApplication";
    }
}
