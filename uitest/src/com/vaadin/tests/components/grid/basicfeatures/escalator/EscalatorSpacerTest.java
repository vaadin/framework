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
package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorSpacerTest extends EscalatorBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
        populate();
    }

    @Test
    public void openVisibleSpacer() {
        assertNull("No spacers should be shown at the start", getSpacer(1));
        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        assertNotNull("Spacer should be shown after setting it", getSpacer(1));
    }

    @Test
    public void closeVisibleSpacer() {
        selectMenuPath(FEATURES, SPACERS, ROW_1, SET_100PX);
        selectMenuPath(FEATURES, SPACERS, ROW_1, REMOVE);
        assertNull("Spacer should not exist after removing it", getSpacer(1));
    }

}
