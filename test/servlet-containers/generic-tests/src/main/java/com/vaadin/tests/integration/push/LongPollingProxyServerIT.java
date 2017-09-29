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
package com.vaadin.tests.integration.push;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.integration.AbstractIntegrationTest;

@RunWith(Parameterized.class)
public class LongPollingProxyServerIT extends AbstractIntegrationTest {

    @Parameters(name = "{0}")
    public static List<String[]> getTestParameters() {
        List<String[]> parameters = new ArrayList<>();
        addTestParams(parameters, "Buffering+Timeout", "buffering-timeout");
        addTestParams(parameters, "NonBuffering+Timeout",
                "nonbuffering-timeout");
        addTestParams(parameters, "Buffering", "buffering");
        addTestParams(parameters, "NonBuffering", "nonbuffering");
        return parameters;
    }

    private static void addTestParams(List<String[]> parameters,
            String... pair) {
        parameters.add(pair);
    }

    @Parameter(0)
    public String name;

    @Parameter(1)
    public String path;

    @Override
    public void setup() throws Exception {
        setDesiredCapabilities(Browser.PHANTOMJS.getDesiredCapabilities());

        super.setup();
    }

    @Test
    public void actionAfterFirstTimeout() throws Exception {
        // The wildfly9-nginx server has a configured timeout of 10s for
        // *-timeout urls
        Thread.sleep(15000);
        Assert.assertEquals(0, getClientCounter());
        getIncrementButton().click();
        Assert.assertEquals(1, getClientCounter());
    }

    @Test
    public void basicPush() {
        Assert.assertEquals(0, getServerCounter());
        getServerCounterStartButton().click();
        waitUntil(e -> getServerCounter() > 1, 10);
    }

    @Override
    protected String getTestPath() {
        return "/" + path + "/demo";
    }

    private int getClientCounter() {
        WebElement clientCounterElem = findElement(
                By.id(BasicPush.CLIENT_COUNTER_ID));
        return Integer.parseInt(clientCounterElem.getText());
    }

    private int getServerCounter() {
        WebElement serverCounterElem = findElement(
                By.id(BasicPush.SERVER_COUNTER_ID));
        return Integer.parseInt(serverCounterElem.getText());
    }

    private WebElement getServerCounterStartButton() {
        return findElement(By.id(BasicPush.START_TIMER_ID));
    }

    private WebElement getIncrementButton() {
        return findElement(By.id(BasicPush.INCREMENT_BUTTON_ID));
    }
}
