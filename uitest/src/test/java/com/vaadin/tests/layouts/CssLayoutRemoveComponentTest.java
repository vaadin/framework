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
package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CssLayoutRemoveComponentTest extends SingleBrowserTest {
    @Test
    public void testRemoveOnlyNecessaryComponentsFromDom() {
        openTestURL();

        String script = "document.mutationEventCount = 0;"
                + "var observer = new MutationObserver(function(mutations) {"
                + "mutations.forEach(function(mutation) { document.mutationEventCount += mutation.removedNodes.length; });"
                + "});"
                + "observer.observe(arguments[0].parentNode, { childList: true });";

        executeScript(script,
                $(TextFieldElement.class).caption("Caption1").first());

        $(ButtonElement.class).first().click();

        Long mutationEvents = (Long) executeScript(
                "return document.mutationEventCount;");
        assertEquals(
                "Parent should only have two mutation events (remove field and its caption)",
                2, mutationEvents.intValue());
    }
}
