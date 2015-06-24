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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Just top level test case that contains all synchronizable components
 * 
 * @author Vaadin Ltd
 */
public class ParseAllSupportedComponentsTest {

    @Test
    public void allComponentsAreParsed() throws FileNotFoundException {
        DesignContext ctx = Design
                .read(new FileInputStream(
                        "server/tests/src/com/vaadin/tests/design/all-components.html"),
                        null);

        assertThat(ctx, is(not(nullValue())));
        assertThat(ctx.getRootComponent(), is(not(nullValue())));
    }
}
