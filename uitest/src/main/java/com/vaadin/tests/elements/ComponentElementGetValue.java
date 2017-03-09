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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

/**
 * UI test for getValue() method of components: TextField, TextArea,
 * PasswordField, ComboBox, ListSelect, NativeSelect, OptionGroup, CheckBox,
 * DateField, TwinColSelect
 *
 * @since
 * @author Vaadin Ltd
 */
public class ComponentElementGetValue extends AbstractTestUI {

    public static final String TEST_STRING_VALUE = "item 2";
    public static final int TEST_SLIDER_VALUE = 42;
    public static final float TEST_FLOAT_VALUE = 0.42f;
    public static final LocalDate TEST_DATE_VALUE = LocalDate.now();
    DateField df;
    final Label valueChangeLabel = new Label("Initial value");

    // These constants are used to check that change value event was
    // called
    public static final String[] FIELD_VALUES = { "textFieldValueChange",
            "textAreaValueChange", "passwordValueChange" };
    public static final String CHECKBOX_VALUE_CHANGE = "checkboxValueChange";
    public static final String DATEFIELD_VALUE_CHANGE = "dateFieldValueChange";
    public static final String MULTI_SELECT_VALUE_CHANGE = "multiSelectValueChange";

    private List<String> createTestItems() {
        List<String> options = new ArrayList<String>();
        options.add("item 1");
        options.add(TEST_STRING_VALUE);
        options.add("item 3");
        return options;
    }

    private void addSingleSelectComponents() {
        List<String> options = new ArrayList<String>();
        options.add("item 1");
        options.add(TEST_STRING_VALUE);
        options.add("item 3");

        ComboBox<String> cb = new ComboBox<>("", options);
        cb.setValue(TEST_STRING_VALUE);
        addComponent(cb);

        NativeSelect<String> nativeSelect = new NativeSelect<>("", options);
        nativeSelect.setValue(TEST_STRING_VALUE);
        addComponent(nativeSelect);

        RadioButtonGroup<String> rbGroup = new RadioButtonGroup<>("", options);
        rbGroup.setValue(TEST_STRING_VALUE);
        addComponent(rbGroup);
    }

    private List<String> createData() {
        List<String> options = new ArrayList<String>();
        options.add("item 1");
        options.add(TEST_STRING_VALUE);
        options.add("item 3");
        options.add("item 4");
        return options;
    }

    private void addMultiSelectComponents() {

        List<MultiSelect<String>> components = new ArrayList<>();
        components.add(new ListSelect("", createData()));
        components.add(new CheckBoxGroup("", createData()));
        components.add(new TwinColSelect("", createData()));
        components.forEach(c -> {
            c.select(TEST_STRING_VALUE);
            c.addValueChangeListener(event -> {
                valueChangeLabel.setValue(MULTI_SELECT_VALUE_CHANGE);
            });
            addComponent((Component) c);
        });
    }

    @Override
    protected void setup(VaadinRequest request) {

        AbstractTextField[] fieldComponents = { new TextField(), new TextArea(),
                new PasswordField() };
        addSingleSelectComponents();
        addMultiSelectComponents();

        for (int i = 0; i < fieldComponents.length; i++) {
            AbstractTextField field = fieldComponents[i];
            field.setValue(TEST_STRING_VALUE);
            String value = FIELD_VALUES[i];
            field.addValueChangeListener(event -> {
                valueChangeLabel.setValue(value);
            });
            addComponent(field);
        }

        addComponent(createCheckBox());
        addComponent(createSlider());
        addComponent(createDateField());
        valueChangeLabel.setId("valueChangeLabel");
        addComponent(valueChangeLabel);
    }

    private DateField createDateField() {
        DateField df = new DateField();
        df.setDateFormat("yyyy-MM-dd");
        df.setValue(TEST_DATE_VALUE);
        df.addValueChangeListener(event -> {
            valueChangeLabel.setValue(DATEFIELD_VALUE_CHANGE);
        });
        return df;
    }

    private Slider createSlider() {
        Slider sl = new Slider(0, 100);
        sl.setWidth("100px");
        sl.setValue(new Double(TEST_SLIDER_VALUE));
        return sl;
    }

    private CheckBox createCheckBox() {
        CheckBox cb = new CheckBox();
        cb.setValue(true);
        cb.addValueChangeListener(event -> {
            valueChangeLabel.setValue(CHECKBOX_VALUE_CHANGE);
        });
        return cb;
    }

    @Override
    protected String getTestDescription() {
        return "Field elements getValue() should return test value";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13455;
    }

}
