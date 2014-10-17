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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.vaadin.testbench.elements.NotificationElement;

public class EscalatorBasicsTest extends EscalatorBasicClientFeaturesTest {

    @Test
    public void testDetachingAnEmptyEscalator() {
        setDebug(true);
        openTestURL();

        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingASemiPopulatedEscalator() {
        setDebug(true);
        openTestURL();

        selectMenuPath(COLUMNS_AND_ROWS, ADD_ONE_OF_EACH_ROW);
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingAPopulatedEscalator() {
        setDebug(true);
        openTestURL();

        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    private void assertEscalatorIsRemovedCorrectly() {
        assertFalse($(NotificationElement.class).exists());
        assertNull(getEscalator());
    }
}
