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
package com.vaadin.v7.data.util.sqlcontainer;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

    @Test
    public void escapeSQL_noQuotes_returnsSameString() {
        Assert.assertEquals("asdf", SQLUtil.escapeSQL("asdf"));
    }

    @Test
    public void escapeSQL_singleQuotes_returnsEscapedString() {
        Assert.assertEquals("O''Brien", SQLUtil.escapeSQL("O'Brien"));
    }

    @Test
    public void escapeSQL_severalQuotes_returnsEscapedString() {
        Assert.assertEquals("asdf''ghjk''qwerty",
                SQLUtil.escapeSQL("asdf'ghjk'qwerty"));
    }

    @Test
    public void escapeSQL_doubleQuotes_returnsEscapedString() {
        Assert.assertEquals("asdf\\\"foo", SQLUtil.escapeSQL("asdf\"foo"));
    }

    @Test
    public void escapeSQL_multipleDoubleQuotes_returnsEscapedString() {
        Assert.assertEquals("asdf\\\"foo\\\"bar",
                SQLUtil.escapeSQL("asdf\"foo\"bar"));
    }

    @Test
    public void escapeSQL_backslashes_returnsEscapedString() {
        Assert.assertEquals("foo\\\\nbar\\\\r",
                SQLUtil.escapeSQL("foo\\nbar\\r"));
    }

    @Test
    public void escapeSQL_x00_removesX00() {
        Assert.assertEquals("foobar", SQLUtil.escapeSQL("foo\\x00bar"));
    }

    @Test
    public void escapeSQL_x1a_removesX1a() {
        Assert.assertEquals("foobar", SQLUtil.escapeSQL("foo\\x1abar"));
    }
}
