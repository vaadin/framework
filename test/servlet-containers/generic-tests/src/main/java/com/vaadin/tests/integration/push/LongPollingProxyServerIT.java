/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.integration.push;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.integration.AbstractIntegrationTest;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RunWith(Parameterized.class)
public class LongPollingProxyServerIT extends AbstractIntegrationTest {

    @Parameters(name = "{0}")
    public static List<String[]> getTestParameters() {
        List<String[]> parameters = new ArrayList<String[]>();
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
    @Before
    public void setup() throws Exception {
        Assume.assumeTrue(
                "wildfly9-nginx".equals(System.getProperty("server-name")));

        super.setup();
    }

    @Test
    public void actionAfterFirstTimeout() throws Exception {
        // The wildfly9-nginx server has a configured timeout of 10s for
        // *-timeout urls
        Thread.sleep(15000);
        assertEquals(0, getClientCounter());
        getIncrementButton().click();
        assertEquals(1, getClientCounter());
    }

    @Test
    public void basicPush() {
        assertEquals(0, getServerCounter());
        getServerCounterStartButton().click();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return getServerCounter() > 1;
            }
        });
    }

    @Override
    protected String getContextPath() {
        // Prefix with the context path with the parameter
        return "/" + path + super.getContextPath();
    }

    @Override
    protected String getTestPath() {
        return "/";
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
