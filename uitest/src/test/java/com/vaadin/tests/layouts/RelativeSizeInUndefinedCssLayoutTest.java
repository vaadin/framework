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
package com.vaadin.tests.layouts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class RelativeSizeInUndefinedCssLayoutTest
        extends SingleBrowserTestPhantomJS2 {

    @Test
    public void relativeSizeInUndefinedCssLayout() {
        openTestURL();
        int w = $(FormLayoutElement.class).first().getSize().getWidth();
        assertEquals(w, 520);

        int w2 = $(TextFieldElement.class).first().getSize().getWidth();
        assertTrue(w2 > 400);
    }
}
