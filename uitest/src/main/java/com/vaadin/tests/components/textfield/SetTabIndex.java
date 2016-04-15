package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

public class SetTabIndex extends TestBase {

    @Override
    protected void setup() {
        final TextField field1 = new TextField("Field1 (tabindex 1)");
        field1.setTabIndex(1);
        addComponent(field1);

        final TextField field3 = new TextField("Field3 (tabindex 3)");
        field3.setTabIndex(3);
        addComponent(field3);

        final TextField field2 = new TextField("Field2 (tabindex 2)");
        field2.setTabIndex(2);
        addComponent(field2);

        final TextField field0 = new TextField("Field2 (tabindex 0)");
        addComponent(field0);

        final CheckBox readonly = new CheckBox("Readonly");
        readonly.setImmediate(true);
        readonly.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean readonly = Boolean.valueOf(event.getProperty()
                        .getValue().toString());
                field1.setReadOnly(readonly);
                field2.setReadOnly(readonly);
                field3.setReadOnly(readonly);
                field0.setReadOnly(readonly);
            }
        });
        addComponent(readonly);

    }

    @Override
    protected String getDescription() {
        return "One should be able to set the TabIndex";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5487;
    }

}
