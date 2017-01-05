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
package com.vaadin.tests.components;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that a user is notified about a missing component from the widgetset
 */
public class UnknownComponentConnectorTest extends MultiBrowserTest {

    @Test
    public void testConnectorNotFoundInWidgetset() throws Exception {
        openTestURL();
        WebElement component = vaadinElementById("no-connector-component");
        assertTrue(component.getText().startsWith(
                "Widgetset 'com.vaadin.DefaultWidgetSet' does not contain an "
                        + "implementation for com.vaadin.tests.components.UnknownComponentConnector."
                        + "ComponentWithoutConnector."));
    }
}
