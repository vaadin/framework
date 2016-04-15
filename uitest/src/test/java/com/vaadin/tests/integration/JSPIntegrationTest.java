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
package com.vaadin.tests.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.PrivateTB3Configuration;

public class JSPIntegrationTest extends PrivateTB3Configuration {

    final String appRunnerTestUrl = getBaseURL() + "/run/Buttons";
    final String jspUrl = getBaseURL() + "/statictestfiles/vaadinsessions.jsp";
    final String integrationUrl = getBaseURL() + "/integration";

    @Test
    public void listVaadinSessions() {

        assertUICount(0);

        // Open a new UI
        getDriver().get(integrationUrl);
        assertUICount(1);

        // Open a new UI
        getDriver().get(integrationUrl);

        // Should now have two UIs for the same service with different uiIds
        List<UIData> twoUIs = getUIs();
        assertEquals(2, twoUIs.size());
        assertNotEquals(twoUIs.get(0).uiId, twoUIs.get(1).uiId);
        assertEquals(twoUIs.get(0).serviceName, twoUIs.get(1).serviceName);

        getDriver().get(appRunnerTestUrl);
        // Should now have two services with 2 + 1 UIs
        List<UIData> threeUIs = getUIs();
        assertEquals(3, threeUIs.size());
        Set<String> serviceNames = new HashSet<String>();
        Set<Integer> uiIds = new HashSet<Integer>();
        for (UIData uiData : threeUIs) {
            serviceNames.add(uiData.serviceName);
            uiIds.add(uiData.uiId);
        }
        assertGreaterOrEqual(
                "There should be at least two unique service names",
                serviceNames.size(), 2);
        assertGreaterOrEqual("There should be at least two unique ui ids",
                uiIds.size(), 2);
    }

    private static class UIData {
        private String serviceName;
        private int uiId;
    }

    private List<UIData> getUIs() {
        List<UIData> uis = new ArrayList<UIData>();

        getDriver().get(jspUrl);
        List<WebElement> rows = getDriver().findElements(
                By.xpath("//tr[@class='uirow']"));
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
}
