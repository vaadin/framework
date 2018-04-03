package com.vaadin.tests.validation;

import java.util.Set;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.validator.StringLengthValidator;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.TwinColSelect;

public class FieldErrorIndication extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        addComponent(hl);

        VerticalLayout vl = new VerticalLayout();
        hl.addComponent(vl);

        ComboBox comboBox = new ComboBox("ComboBox");
        comboBox.addItem("ok");
        comboBox.addItem("error");
        comboBox.addValidator(new StringLengthValidator("fail", 0, 2, false));
        comboBox.setValue("error");

        ListSelect listSelect = new ListSelect("ListSelect");
        listSelect.addItem("ok");
        listSelect.addItem("error");
        listSelect.addValidator(new StringLengthValidator("fail", 0, 2, false));
        listSelect.setValue("error");

        NativeSelect nativeSelect = new NativeSelect("NativeSelect");
        nativeSelect.addItem("ok");
        nativeSelect.addItem("error");
        nativeSelect
                .addValidator(new StringLengthValidator("fail", 0, 2, false));
        nativeSelect.setValue("error");
        TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect");
        twinColSelect.addItem("ok");
        twinColSelect.addItem("error");
        twinColSelect.addValidator(value -> {
            if (value instanceof Set && ((Set) value).size() == 1
                    && ((Set) value).contains("ok")) {
                return;
            }

            throw new InvalidValueException("fail");
        });
        twinColSelect.setValue("error");

        vl.addComponents(comboBox, listSelect, nativeSelect, twinColSelect);

        Class<? extends AbstractField>[] textFields = new Class[] {
                TextField.class, TextArea.class, RichTextArea.class,
                PasswordField.class };
        vl = new VerticalLayout();
        hl.addComponent(vl);
        for (Class<? extends AbstractField> fieldClass : textFields) {
            vl.addComponent(getField(fieldClass));
        }

    }

    /**
     * @since
     * @param fieldClass
     * @return
     */
    private Component getField(Class<? extends AbstractField> fieldClass) {
        try {
            AbstractField<?> f = fieldClass.newInstance();
            f.setCaption(fieldClass.getSimpleName());
            f.setComponentError(new UserError("fail"));
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
