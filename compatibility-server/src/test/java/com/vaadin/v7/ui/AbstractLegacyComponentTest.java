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
package com.vaadin.v7.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AbstractLegacyComponentTest {
    AbstractLegacyComponent component = new AbstractLegacyComponent() {
    };

    @Test
    public void testImmediate() {
        assertTrue("Component should be immediate by default",
                component.isImmediate());
        component.setImmediate(false);
        assertFalse(
                "Explicitly non-immediate component should not be immediate",
                component.isImmediate());
    }
}
