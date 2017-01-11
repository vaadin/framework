package com.vaadin.tests.elements.abstracttextfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractMultiSelect;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

public class AbstractFieldElementSetValueReadOnly extends AbstractTestUI {

    private AbstractField<?>[] fields = { new TextArea(), new TextField(),
            new DateField(), new PasswordField(), new CheckBox(),
            new RichTextArea(), new Slider() };
    private AbstractMultiSelect<?>[] multiSelects = { new ListSelect(),
            new CheckBoxGroup(), new TwinColSelect() };
    private AbstractSingleSelect<?>[] singleSelects = { new ComboBox(),
            new NativeSelect(), new RadioButtonGroup() };

    @Override
    protected void setup(VaadinRequest request) {
        for (AbstractField field : fields) {
            field.setReadOnly(true);
            addComponent(field);
        }
        for (AbstractMultiSelect multiSelect : multiSelects) {
            multiSelect.setReadOnly(true);
            addComponent(multiSelect);
        }
        for (AbstractSingleSelect singleSelect : singleSelects) {
            singleSelect.setReadOnly(true);
            addComponent(singleSelect);
        }
    }

    @Override
    protected String getTestDescription() {
        return "When vaadin element is set ReadOnly, setValue() method should raise an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14068;
    }

}
