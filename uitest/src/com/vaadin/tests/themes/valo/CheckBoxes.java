/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.themes.valo;

import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

public class CheckBoxes extends VerticalLayout implements View {
    public CheckBoxes() {
        setMargin(true);

        Label h1 = new Label("Check Boxes");
        h1.addStyleName("h1");
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        CheckBox check = new CheckBox("Checked", true);
        row.addComponent(check);

        check = new CheckBox(
                "Checked, explicit width, so that the caption should wrap",
                true);
        row.addComponent(check);
        check.setWidth("150px");

        check = new CheckBox("Not checked");
        row.addComponent(check);

        check = new CheckBox(null, true);
        check.setDescription("No caption");
        row.addComponent(check);

        check = new CheckBox("Custom color", true);
        check.addStyleName("color1");
        row.addComponent(check);

        TestIcon testIcon = new TestIcon(30);
        check = new CheckBox("Custom color", true);
        check.addStyleName("color2");
        check.setIcon(testIcon.get());
        row.addComponent(check);

        check = new CheckBox("With Icon", true);
        check.setIcon(testIcon.get());
        row.addComponent(check);

        check = new CheckBox();
        check.setIcon(testIcon.get(true));
        row.addComponent(check);

        check = new CheckBox("Small", true);
        check.addStyleName("small");
        row.addComponent(check);

        check = new CheckBox("Large", true);
        check.addStyleName("large");
        row.addComponent(check);

        h1 = new Label("Option Groups");
        h1.addStyleName("h1");
        addComponent(h1);

        row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        OptionGroup options = new OptionGroup("Choose one, explicit width");
        options.setWidth("200px");
        options.addItem("Option One");
        Item two = options
                .addItem("Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, explicit width");
        options.setMultiSelect(true);
        options.setWidth("200px");
        options.addItem("Option One");
        two = options
                .addItem("Option Two, with a longer caption that should wrap when the components width is explicitly set.");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose one, small");
        options.addStyleName("small");
        options.setMultiSelect(false);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, small");
        options.addStyleName("small");
        options.setMultiSelect(true);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose one, large");
        options.addStyleName("large");
        options.setMultiSelect(false);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Choose many, large");
        options.addStyleName("large");
        options.setMultiSelect(true);
        options.addItem("Option One");
        options.addItem("Option Two");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon("Option Two", testIcon.get());
        options.setItemIcon("Option Three", testIcon.get(true));
        row.addComponent(options);

        options = new OptionGroup("Horizontal items");
        options.addStyleName("horizontal");
        options.addItem("Option One");
        two = options.addItem("Option Two, with a longer caption");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get());
        row.addComponent(options);

        options = new OptionGroup("Horizontal items, explicit width");
        options.setMultiSelect(true);
        options.setWidth("500px");
        options.addStyleName("horizontal");
        options.addItem("Option One");
        two = options.addItem("Option Two, with a longer caption");
        options.addItem("Option Three");
        options.select("Option One");
        options.setItemIcon("Option One", testIcon.get());
        options.setItemIcon(two, testIcon.get());
        options.setItemIcon("Option Three", testIcon.get());
        row.addComponent(options);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
