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
package com.vaadin.tests.elements;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ElementExistsTest extends MultiBrowserTest {
    @Test
    public void testExistsWithoutUI() {
        // Test that an exists query does not throw an exception even when the
        // initialization of the UI has not been done (#14808).
        boolean buttonExists = $(ButtonElement.class).exists();
        assertFalse(
                "$(ButtonElement.class).exists() returned true, but there should be no buttons.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        assertFalse(
                "$(ButtonElement.class).caption(\"b\").exists() returned true, "
                        + "but there should be no buttons.",
                buttonExists);
    }

    @Test
    public void testExistsWithUI() {
        // Test the expected case where the UI has been properly set up.
        openTestURL();
        boolean buttonExists = $(ButtonElement.class).exists();
        assertTrue(
                "No button was found, although one should be present in the UI.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        assertTrue(
                "No button with caption 'b' was found, although one should be present in the UI.",
                buttonExists);
        buttonExists = $(ButtonElement.class).caption("Button 2").exists();
        assertFalse(
                "$(ButtonElement.class).caption(\"Button 2\") returned true, but "
                        + "there should be no button with that caption.",
                buttonExists);
    }
}
