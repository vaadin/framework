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

import org.junit.Test;

import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.EscalatorUpdaterUi;

public class EscalatorUpdaterUiTest extends EscalatorBasicClientFeaturesTest {
    @Override
    protected Class<?> getUIClass() {
        return EscalatorUpdaterUi.class;
    }

    @Test
    public void testHeaderPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    private void testPaintOrder(String tableSection, boolean addColumnFirst,
            boolean removeColumnFirst) {
        openTestURL();

        if (addColumnFirst) {
            selectMenuPath(COLUMNS_AND_ROWS, COLUMNS,
                    ADD_ONE_COLUMN_TO_BEGINNING);
            selectMenuPath(COLUMNS_AND_ROWS, tableSection,
                    ADD_ONE_ROW_TO_BEGINNING);
        } else {
            selectMenuPath(COLUMNS_AND_ROWS, tableSection,
                    ADD_ONE_ROW_TO_BEGINNING);
            selectMenuPath(COLUMNS_AND_ROWS, COLUMNS,
                    ADD_ONE_COLUMN_TO_BEGINNING);
        }

        assertLogContainsInOrder("preAttach: elementIsAttached == false",
                "postAttach: elementIsAttached == true",
                "update: elementIsAttached == true");
        assertLogDoesNotContain("preDetach");
        assertLogDoesNotContain("postDetach");

        if (removeColumnFirst) {
            selectMenuPath(COLUMNS_AND_ROWS, COLUMNS,
                    REMOVE_ONE_COLUMN_FROM_BEGINNING);
            selectMenuPath(COLUMNS_AND_ROWS, tableSection,
                    REMOVE_ONE_ROW_FROM_BEGINNING);
        } else {
            selectMenuPath(COLUMNS_AND_ROWS, tableSection,
                    REMOVE_ONE_ROW_FROM_BEGINNING);
            selectMenuPath(COLUMNS_AND_ROWS, COLUMNS,
                    REMOVE_ONE_COLUMN_FROM_BEGINNING);
        }

        assertLogContainsInOrder("preDetach: elementIsAttached == true",
                "postDetach: elementIsAttached == false");
    }

}
