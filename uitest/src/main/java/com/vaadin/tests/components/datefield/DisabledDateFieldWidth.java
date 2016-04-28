package com.vaadin.tests.components.datefield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;

public class DisabledDateFieldWidth extends TestBase {
    @Override
    public void setup() {

        final DateField dateField1 = new DateField("DateField");
        dateField1.setResolution(DateField.RESOLUTION_YEAR);
        dateField1.setEnabled(false);

        Button button = new Button("Repaint datefield",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        dateField1.markAsDirty();
                    }
                });

        GridLayout gl = new GridLayout(3, 1);
        gl.addComponent(dateField1);
        gl.addComponent(button);

        addComponent(gl);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }
}
