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
package com.vaadin.tests.elements;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ColorPicker;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.TreeTable;
import com.vaadin.v7.ui.TwinColSelect;

/**
 *
 * Base testUI class for testing getCaption method. Captions of elements
 * implemented differently in different layouts (FormLayout) This class adds all
 * elements to the layout. The exact layout is created in a child class.
 */
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public abstract class CompatibilityElementComponentGetCaptionBase
        extends AbstractTestUI {
    // Specify exact type of Layout in a child class
    AbstractLayout mainLayout = null;
    // default captions for all elements
    public static final String[] DEFAULT_CAPTIONS = { "Combobox", "table",
            "treeTable", "tree", "TwinColSelect", "optionGroup", "ListSelect",
            "ColorPicker", "CheckBox", "TextField", "TextArea", "DateField", };

    @Override
    protected void setup(VaadinRequest request) {
        Component[] comps = { new ComboBox(), new Table(), new TreeTable(),
                new Tree(), new TwinColSelect(), new OptionGroup(),
                new ListSelect(), new ColorPicker(), new CheckBox(),
                new TextField(), new TextArea(), new DateField(), };
        addComponent(mainLayout);
        for (int i = 0; i < comps.length; i++) {
            Component component = comps[i];
            component.setCaption(DEFAULT_CAPTIONS[i]);
            mainLayout.addComponent(component);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Test getCaption method for vaadin compatibility components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14453;
    }
}
