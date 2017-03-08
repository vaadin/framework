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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class InitiallyEmptyFragmentTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return InitialFragmentEvent.class;
    }

    @Test
    public void testNoFragmentChangeEventWhenInitiallyEmpty() throws Exception {
        openTestURL();
        /*
         * There is no fragment change event when the fragment is initially
         * empty
         */
        assertLogText(" ");
        executeScript("window.location.hash='bar'");
        assertLogText("1. Fragment changed from \"no event received\" to bar");
    }

    private void assertLogText(String expected) {
        Assert.assertEquals("Unexpected log contents,", expected, getLogRow(0));
    }
}
