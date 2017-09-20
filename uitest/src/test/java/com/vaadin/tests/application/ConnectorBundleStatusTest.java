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
package com.vaadin.tests.application;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ConnectorBundleStatusTest extends SingleBrowserTest {

    @Test
    public void testConnectorBundleLoading() {
        openTestURL();

        assertLoaded("__eager");

        $(ButtonElement.class).id("refresh").click();

        assertLoaded("__eager", "__deferred");

        $(ButtonElement.class).id("rta").click();
        $(ButtonElement.class).id("refresh").click();

        assertLoaded("__eager", "__deferred",
                "com.vaadin.client.ui.richtextarea.RichTextAreaConnector");
    }

    private void assertLoaded(String... expectedNames) {
        String bundleStatus = findElement(By.id("bundleStatus")).getText();
        Assert.assertEquals(Arrays.asList(expectedNames).toString(),
                bundleStatus);
    }
}
