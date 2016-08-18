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
package com.vaadin.tests.validation;

import java.util.Set;

import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.data.validator.LegacyStringLengthValidator;
import com.vaadin.legacy.ui.LegacyAbstractField;
import com.vaadin.legacy.ui.LegacyField;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

public class FieldErrorIndication extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        addComponent(hl);

        VerticalLayout vl = new VerticalLayout();
        hl.addComponent(vl);

        ComboBox comboBox = new ComboBox("ComboBox");
        comboBox.addItem("ok");
        comboBox.addItem("error");
        comboBox.addValidator(
                new LegacyStringLengthValidator("fail", 0, 2, false));
        comboBox.setValue("error");

        ListSelect listSelect = new ListSelect("ListSelect");
        listSelect.addItem("ok");
        listSelect.addItem("error");
        listSelect.addValidator(
                new LegacyStringLengthValidator("fail", 0, 2, false));
        listSelect.setValue("error");

        NativeSelect nativeSelect = new NativeSelect("NativeSelect");
        nativeSelect.addItem("ok");
        nativeSelect.addItem("error");
        nativeSelect.addValidator(
                new LegacyStringLengthValidator("fail", 0, 2, false));
        nativeSelect.setValue("error");
        TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect");
        twinColSelect.addItem("ok");
        twinColSelect.addItem("error");
        twinColSelect.addValidator(new Validator() {

            @Override
            public void validate(Object value) throws InvalidValueException {
                if (value instanceof Set && ((Set) value).size() == 1
                        && ((Set) value).contains("ok")) {
                    return;
                }

                throw new InvalidValueException("fail");
            }

        });
        twinColSelect.setValue("error");

        vl.addComponents(comboBox, listSelect, nativeSelect, twinColSelect);

        Class<? extends LegacyAbstractField>[] textFields = new Class[] {
                LegacyTextField.class, TextArea.class, RichTextArea.class,
                PasswordField.class };
        vl = new VerticalLayout();
        hl.addComponent(vl);
        for (Class<? extends LegacyField> fieldClass : textFields) {
            vl.addComponent(getField(fieldClass));
        }

    }

    /**
     * @since
     * @param fieldClass
     * @return
     */
    private Component getField(Class<? extends LegacyField> fieldClass) {
        LegacyAbstractField f;
        try {
            f = (LegacyAbstractField) fieldClass.newInstance();
            f.setCaption(fieldClass.getSimpleName());
            f.setComponentError(new UserError("fail"));
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
