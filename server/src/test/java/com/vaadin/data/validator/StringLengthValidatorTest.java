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
package com.vaadin.data.validator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class StringLengthValidatorTest extends ValidatorTestBase {

    private static final String LONG_STRING = Stream.generate(() -> "x")
            .limit(1000).collect(Collectors.joining());

    @Test
    public void testNullStringFails() {
        assertPasses(null, new StringLengthValidator("", 0, 10));
    }

    @Test
    public void testMaxLengthTooLongStringFails() {
        assertFails(LONG_STRING,
                new StringLengthValidator("Should be at most 10", null, 10));
    }

    @Test
    public void testMaxLengthStringPasses() {
        assertPasses(LONG_STRING, new StringLengthValidator(
                "Should be at most 1000", null, 1000));
    }

    @Test
    public void testMinLengthEmptyStringFails() {
        assertFails("",
                new StringLengthValidator("Should be at least 1", 1, null));
    }

    @Test
    public void testMinLengthStringPasses() {
        assertPasses("å",
                new StringLengthValidator("Should be at least 1", 1, null));
    }
}
