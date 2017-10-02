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
package com.vaadin.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EncodeUtilTest {
    @Test
    public void rfc5987Encode() {
        assertEquals("A", EncodeUtil.rfc5987Encode("A"));
        assertEquals("%20", EncodeUtil.rfc5987Encode(" "));
        assertEquals("%c3%a5", EncodeUtil.rfc5987Encode("å"));
        assertEquals("%e6%97%a5", EncodeUtil.rfc5987Encode("日"));

        assertEquals("A" + "%20" + "%c3%a5" + "%e6%97%a5",
                EncodeUtil.rfc5987Encode("A å日"));
    }
}
