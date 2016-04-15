package com.vaadin.tests.components.formlayout;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

public class NestedFormLayouts extends AbstractTestUI {

    private FormLayout outer;
    private FormLayout inner1;
    private FormLayout inner2;
    private FormLayout inner21;
    private FormLayout inner3;
    private FormLayout inner31;
    private FormLayout inner4;

    @Override
    protected void setup(VaadinRequest request) {
        outer = new FormLayout();
        outer.setSizeUndefined();
        outer.setWidth("100%");

        inner1 = new FormLayout();
        inner1.addComponent(new Label("Test"));
        inner1.addComponent(new Label("Test2"));
        outer.addComponent(inner1);

        outer.addComponent(new Label("Test"));
        outer.addComponent(new Label("Test2"));

        inner2 = new FormLayout();
        inner2.addComponent(new Label("Test"));
        inner2.addComponent(new Label("Test2"));
        inner21 = new FormLayout();
        inner21.addComponent(new Label("Test"));
        inner21.addComponent(new Label("Test2"));
        inner2.addComponent(inner21);
        outer.addComponent(inner2);

        inner3 = new FormLayout();
        inner3.addComponent(new Label("Test"));
        inner3.addComponent(new Label("Test2"));
        // this layout never gets spacing or margin
        inner31 = new FormLayout();
        inner31.addComponent(new Label("Test"));
        inner31.addComponent(new Label("Test2"));
        inner31.setSpacing(false);
        inner31.setMargin(false);
        inner3.addComponent(inner31);
        outer.addComponent(inner3);

        inner4 = new FormLayout();
        inner4.addComponent(new Label("Test"));
        inner4.addComponent(new Label("Test2"));
        outer.addComponent(inner4);

        addComponent(outer);

        final CheckBox spacingCheckBox = new CheckBox("Spacings", false);
        spacingCheckBox.setId("spacings");
        spacingCheckBox.setImmediate(true);
        spacingCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setLayoutSpacing(spacingCheckBox.getValue());
            }
        });
        addComponent(spacingCheckBox);

        final CheckBox marginCheckBox = new CheckBox("Margins", false);
        marginCheckBox.setId("margins");
        marginCheckBox.setImmediate(true);
        marginCheckBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setLayoutMargin(marginCheckBox.getValue());
            }
        });
        addComponent(marginCheckBox);

        setLayoutSpacing(false);
        setLayoutMargin(false);
    }

    private void setLayoutSpacing(boolean value) {
        outer.setSpacing(value);
        inner1.setSpacing(value);
        inner2.setSpacing(value);
        inner21.setSpacing(value);
        inner3.setSpacing(value);
        inner4.setSpacing(value);
    }

    private void setLayoutMargin(boolean value) {
        outer.setMargin(value);
        inner1.setMargin(value);
        inner2.setMargin(value);
        inner21.setMargin(value);
        inner3.setMargin(value);
        inner4.setMargin(value);
    }

    @Override
    protected String getTestDescription() {
        return "Excess padding applied in FormLayouts nested as first or last rows in a FormLayout";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9427;
    }

}
