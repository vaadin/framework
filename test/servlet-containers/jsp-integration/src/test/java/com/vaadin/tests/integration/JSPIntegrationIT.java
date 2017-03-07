/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import com.vaadin.testbench.TestBenchTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JSPIntegrationIT extends TestBenchTestCase {

    private static final String URL_PREFIX = "http://localhost:8080/";

    private static final String primaryUIUrl = URL_PREFIX + "primaryui";
    private static final String jspUrl = URL_PREFIX + "staticfiles/vaadinsessions.jsp";
    private static final String secondaryUIUrl = URL_PREFIX + "secondaryui";

    @Test
    public void listVaadinSessions() {

        assertUICount(0);

        // Open a new UI
        getDriver().navigate().to(primaryUIUrl);


        assertUICount(1);
        UIData firstUI = getUIs().get(0);

        // Open a new UI
        getDriver().navigate().to(primaryUIUrl);
        UIData secondUI = getUIs().get(0);

        // Should now have UI for the same service with different uiId
        assertUICount(1);
        assertNotEquals(firstUI.uiId, secondUI.uiId);
        assertEquals(firstUI.serviceName, secondUI.serviceName);

        getDriver().navigate().to(secondaryUIUrl);
        // Should now have another services
        List<UIData> twoUIs = getUIs();
        assertEquals(2, twoUIs.size());
        assertNotEquals(twoUIs.get(0).serviceName, twoUIs.get(1).serviceName);
    }

    private static class UIData {
        private String serviceName;
        private int uiId;
    }

    private List<UIData> getUIs() {
        List<UIData> uis = new ArrayList<>();

        getDriver().get(jspUrl);
        List<WebElement> rows = getDriver()
                .findElements(By.xpath("//tr[@class='uirow']"));
        for (WebElement row : rows) {
            UIData data = new UIData();
            List<WebElement> tds = row.findElements(By.xpath("./td"));

            data.serviceName = tds.get(0).getText();
            data.uiId = Integer.parseInt(tds.get(2).getText());

            uis.add(data);
        }

        return uis;
    }

    private void assertUICount(int i) {
        assertEquals(i, getUIs().size());
    }

    @Before
    public void setup() {
        setDriver(new PhantomJSDriver());
    }

    @After
    public void teardown() {
        getDriver().quit();
    }
}
