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
package com.vaadin.v7.tests.server.component.combobox;

import org.junit.Test;

import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.ComboBox;

public class ComboBoxDeclarativeTest extends DeclarativeTestBase<ComboBox> {

    @Test
    public void testReadOnlyWithOptionsRead() {
        testRead(getReadOnlyWithOptionsDesign(),
                getReadOnlyWithOptionsExpected());
    }

    private ComboBox getReadOnlyWithOptionsExpected() {
        ComboBox cb = new ComboBox();
        cb.setTextInputAllowed(false);
        cb.addItem("Hello");
        cb.addItem("World");
        return cb;
    }

    private String getReadOnlyWithOptionsDesign() {
        return "<vaadin7-combo-box text-input-allowed='false'><option>Hello</option><option>World</option></vaadin7-combo-box>";
    }

    @Test
    public void testReadOnlyWithOptionsWrite() {
        testWrite(stripOptionTags(getReadOnlyWithOptionsDesign()),
                getReadOnlyWithOptionsExpected());
    }

    @Test
    public void testBasicRead() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testBasicWrite() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin7-combo-box readonly value='foo'><option selected>foo</option></vaadin7-combo-box>";

        ComboBox comboBox = new ComboBox();
        comboBox.addItems("foo", "bar");
        comboBox.setValue("foo");
        comboBox.setReadOnly(true);

        testRead(design, comboBox);

        // Selects items are not written out by default
        String design2 = "<vaadin7-combo-box readonly></vaadin7-combo-box>";
        testWrite(design2, comboBox);
    }

    private String getBasicDesign() {
        return "<vaadin7-combo-box input-prompt=\"Select something\" filtering-mode=\"off\" scroll-to-selected-item='false'>";
    }

    private ComboBox getBasicExpected() {
        ComboBox cb = new ComboBox();
        cb.setInputPrompt("Select something");
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setScrollToSelectedItem(false);
        return cb;
    }
}
