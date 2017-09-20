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
package com.vaadin.tests.components.nativeselect;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NativeSelectStyleNamesTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NativeSelectInit.class;
    }

    @Test
    public void correctStyleNames() {
        openTestURL();

        Set<String> expected = Stream.of("v-select", "v-widget")
                .collect(Collectors.toSet());
        Assert.assertEquals(expected,
                $(NativeSelectElement.class).first().getClassNames());
    }
}
