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
package com.vaadin.tests.elements.progressbar;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ProgressBarValueTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ProgressBarUI.class;
    }

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void progressBar_differentValues_valuesFetchedCorrectly() {
        assertEquals(1, $(ProgressBarElement.class).id("complete").getValue(),
                0);
        assertEquals(0.5,
                $(ProgressBarElement.class).id("halfComplete").getValue(), 0);
        assertEquals(0, $(ProgressBarElement.class).id("notStarted").getValue(),
                0);
    }
}
