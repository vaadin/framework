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
package com.vaadin.v7.data.util.sqlcontainer.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.util.filter.Compare;

public class CompareTest {

    @Test
    public void testEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Equal("prop1", "val1");
        assertTrue(c1.equals(c2));
    }

    @Test
    public void testDifferentTypeEquals() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        Compare c2 = new Compare.Greater("prop1", "val1");
        assertFalse(c1.equals(c2));
    }

    @Test
    public void testEqualsNull() {
        Compare c1 = new Compare.Equal("prop1", "val1");
        assertFalse(c1.equals(null));
    }
}
