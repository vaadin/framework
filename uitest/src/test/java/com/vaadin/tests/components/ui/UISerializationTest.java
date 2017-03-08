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
package com.vaadin.tests.components.ui;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UISerializationTest extends SingleBrowserTest {

    @Test
    public void uiIsSerialized() throws Exception {
        openTestURL();

        serialize();

        assertThat(getLogRow(0), startsWith("3. Diff states match, size: "));
        assertThat(getLogRow(1), startsWith("2. Deserialized UI in "));
        assertThat(getLogRow(2), allOf(startsWith("1. Serialized UI in"),
                containsString(" into "), endsWith(" bytes")));
    }

    private void serialize() {
        $(ButtonElement.class).first().click();
    }
}
