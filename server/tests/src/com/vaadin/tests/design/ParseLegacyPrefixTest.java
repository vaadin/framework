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
package com.vaadin.tests.design;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test reading a design with all components using the legacy prefix.
 */
public class ParseLegacyPrefixTest {

    @Test
    public void allComponentsAreParsed() throws FileNotFoundException {
        DesignContext ctx = Design
                .read(new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/all-components-legacy.html"),
                        null);

        assertThat(ctx, is(not(nullValue())));
        assertThat(ctx.getRootComponent(), is(not(nullValue())));
    }
}
