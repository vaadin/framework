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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Select;
import com.vaadin.v7.ui.TwinColSelect;
import com.vaadin.v7.ui.themes.ChameleonTheme;

public class SelectsCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public SelectsCssTest(TestSampler parent) {
        super(8, 1);
        this.parent = parent;
        setSpacing(true);
        setWidth(null);

        Select s = new Select("Basic select");
        s.setId("select" + debugIdCounter++);
        addComponent(s);

        s = new Select("Select with items");
        s.setId("select" + debugIdCounter++);
        createDummyData(s);
        addComponent(s);

        TwinColSelect tws = new TwinColSelect();
        tws.setId("select" + debugIdCounter++);
        createDummyData(tws);
        addComponent(tws);

        OptionGroup og = new OptionGroup();
        og.setId("select" + debugIdCounter++);
        createDummyData(og, 4);
        addComponent(og);

        og = new OptionGroup();
        og.setId("select" + debugIdCounter++);
        createDummyData(og, 4);
        og.setItemEnabled("Foo2", false);
        og.setItemEnabled("Foo3", false);
        addComponent(og);

        NativeSelect ns = new NativeSelect();
        ns.setId("select" + debugIdCounter++);
        createDummyData(ns);
        addComponent(ns);

        createComboBoxWith(null, null, null);
        createComboBoxWith("CB Search", ChameleonTheme.COMBOBOX_SEARCH, null);
        createComboBoxWith("SelectButton",
                ChameleonTheme.COMBOBOX_SELECT_BUTTON, null);

        ListSelect ls = new ListSelect();
        ls.setId("select" + debugIdCounter++);
        createDummyData(ls);
        addComponent(ls);

        s = new Select("Basic select");
        s.setId("select" + debugIdCounter++);
        s.setWidth("100px");
        addComponent(s);

        s = new Select("Select with items");
        s.setWidth("100px");
        s.setId("select" + debugIdCounter++);
        createDummyData(s);
        addComponent(s);

        createComboBoxWith(null, null, "100px");
        createComboBoxWith("CB Search", ChameleonTheme.COMBOBOX_SEARCH,
                "100px");
        createComboBoxWith("SelectButton",
                ChameleonTheme.COMBOBOX_SELECT_BUTTON, "100px");
    }

    private void createComboBoxWith(String caption, String primaryStyleName,
            String width) {
        ComboBox cb = new ComboBox();
        cb.setId("select" + debugIdCounter++);
        if (caption != null) {
            cb.setCaption(caption);
        }

        if (primaryStyleName != null) {
            cb.addStyleName(primaryStyleName);
        }
        if (width != null) {
            cb.setWidth(width);
        }

        createDummyData(cb);
        addComponent(cb);
    }

    @Override
    public void addComponent(Component c) {
        parent.registerComponent(c);
        super.addComponent(c);
    }

    private void createDummyData(AbstractSelect select) {
        createDummyData(select, 20);
    }

    private void createDummyData(AbstractSelect select, int items) {
        for (int i = 0; i < items; i++) {
            select.addItem("Foo" + i);
        }
    }
}
