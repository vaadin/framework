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
package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowShouldRemoveActionHandlerTest extends MultiBrowserTest {

    @Test
    public void testRemovingActionHandlers() {
        openTestURL();

        addActionHandler();
        addAnotherActionHandler();

        assertState("An UI with 2 action handlers");
        addActionHandler();

        assertState("An UI with 3 action handlers");
        removeActionHandler();
        removeActionHandler();

        assertState(
                "An UI with 3 action handlers - Removed handler - Removed handler");
        addActionHandler();

        assertState("An UI with 2 action handlers");
    }

    private void removeActionHandler() {
        $(ButtonElement.class).caption("Remove an action handler").first()
                .click();
    }

    private void addAnotherActionHandler() {
        $(ButtonElement.class).caption("Add another action handler").first()
                .click();
    }

    private void addActionHandler() {
        $(ButtonElement.class).caption("Add an action handler").first().click();
    }

    private void assertState(String expected) {
        Assert.assertEquals("Unexpected state,", expected,
                $(LabelElement.class).id("state").getText());
    }
}
