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
package com.vaadin.tests.integration;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.push.BasicPushLongPolling;
import com.vaadin.tests.push.BasicPushTest;
import com.vaadin.tests.tb3.IncludeIfProperty;

@IncludeIfProperty(property = "server-name", value = "wildfly9-nginx")
public class LongPollingProxyServerTest extends AbstractIntegrationTest {

    @Override
    protected Class<?> getUIClass() {
        return BasicPushLongPolling.class;
    }

    @Test
    public void bufferingTimeoutBasicPush() throws Exception {
        basicPush("buffering-timeout");
    }

    @Test
    public void nonbufferingTimeoutBasicPush() throws Exception {
        basicPush("nonbuffering-timeout");
    }

    @Test
    public void bufferingBasicPush() throws Exception {
        basicPush("buffering");
    }

    @Test
    public void nonbufferingBasicPush() throws Exception {
        basicPush("nonbuffering");
    }

    @Test
    public void bufferingTimeoutActionAfterFirstTimeout() throws Exception {
        actionAfterFirstTimeout("buffering-timeout");
    }

    @Test
    public void nonbufferingTimeoutActionAfterFirstTimeout() throws Exception {
        actionAfterFirstTimeout("nonbuffering-timeout");
    }

    private String getUrl(String bufferingOrNot) {
        return getBaseURL() + "/" + bufferingOrNot + "/demo"
                + getDeploymentPath();
    }

    private void actionAfterFirstTimeout(String bufferingOrNot)
            throws Exception {
        String url = getUrl(bufferingOrNot);
        getDriver().get(url);
        // The wildfly9-nginx server has a configured timeout of 10s for
        // *-timeout urls
        Thread.sleep(15000);
        Assert.assertEquals(0, BasicPushTest.getClientCounter(this));
        BasicPushTest.getIncrementButton(this).click();
        Assert.assertEquals(1, BasicPushTest.getClientCounter(this));
    }

    private void basicPush(String bufferingOrNot) throws Exception {
        String url = getUrl(bufferingOrNot);
        getDriver().get(url);

        Assert.assertEquals(0, BasicPushTest.getServerCounter(this));
        BasicPushTest.getServerCounterStartButton(this).click();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return BasicPushTest
                        .getServerCounter(LongPollingProxyServerTest.this) > 1;
            }
        });
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections
                .singletonList(Browser.PHANTOMJS.getDesiredCapabilities());
    }
}
