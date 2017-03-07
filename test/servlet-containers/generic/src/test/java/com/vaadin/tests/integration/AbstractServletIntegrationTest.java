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

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;

public abstract class AbstractServletIntegrationTest
        extends AbstractIntegrationTest {

    @Test
    public void runTest() throws Exception {
        // make sure no fading progress indicator from table update is lingering
        Thread.sleep(2000);
        compareScreen("initial");
        $(TableElement.class).first().getCell(0, 1).click();
        // without this, table fetch might have a fading progress indicator
        Thread.sleep(2000);
        compareScreen("finland");
    }

}
