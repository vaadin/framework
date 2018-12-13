package com.vaadin.tests.components.checkboxgroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CheckBoxGroupReadOnly extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<Integer> cbg = new CheckBoxGroup<>();
        cbg.setId("cbg");
        cbg.setItems(1, 2, 3, 4);
        cbg.setReadOnly(true);
        addComponent(cbg);

        Button changeReadOnly = new Button("Change Read-Only", e -> {
            cbg.setReadOnly(!cbg.isReadOnly());
        });
        changeReadOnly.setId("changeReadOnly");
        Button changeEnabled = new Button("Change Enabled", e -> {
            cbg.setEnabled(!cbg.isEnabled());
        });
        changeEnabled.setId("changeEnabled");
        addComponent(changeReadOnly);
        addComponent(changeEnabled);
    }
}
