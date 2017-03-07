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
package com.vaadin.tests.elements.combobox;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxPopupTest extends MultiBrowserTest {

    private ComboBoxElement comboBoxElement;

    @Override
    protected Class<?> getUIClass() {
        return ComboBoxUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        comboBoxElement = $(ComboBoxElement.class).first();
    }

    @Test
    public void comboBoxPopup_popupOpen_popupFetchedSuccessfully() {
        comboBoxElement.openPopup();

        assertNotNull(comboBoxElement.getSuggestionPopup());
    }

    @Test
    public void comboBoxPopup_popupClosed_popupFetchedSuccessfully() {
        assertNotNull(comboBoxElement.getSuggestionPopup());
    }
}
