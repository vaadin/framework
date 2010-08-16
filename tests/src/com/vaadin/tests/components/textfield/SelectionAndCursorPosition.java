package com.vaadin.tests.components.textfield;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

public class SelectionAndCursorPosition extends TestBase {

    TextField tf = new TextField();

    @Override
    protected void setup() {

        tf.setCaption("Text field");
        tf.setValue("So we have some text to select");
        tf.setWidth("400px");

        FormLayout fl = new FormLayout();
        Panel panel = new Panel(fl);
        panel.setCaption("Hackers panel");
        CheckBox ml = new CheckBox("Multiline");
        ml.setImmediate(true);
        ml.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (tf.getHeight() < 0) {
                    tf.setHeight("50px");
                } else {
                    tf.setSizeUndefined();
                    tf.setRows(0);
                }
                tf.setWidth("400px");
            }
        });
        fl.addComponent(ml);

        Button b = new Button("Select all ( selectAll() )");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                tf.selectAll();
            }
        });
        fl.addComponent(b);

        HorizontalLayout selectRange = new HorizontalLayout();
        selectRange
                .setCaption("Select range of text ( setSelectionRange(int start, int lengt) )");
        final TextField start = new TextField("From:");
        final TextField length = new TextField("Selection length:");
        b = new Button("select");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                int startPos = Integer.parseInt((String) start.getValue());
                int lenght = Integer.parseInt((String) length.getValue());
                tf.setSelectionRange(startPos, lenght);
            }
        });

        selectRange.addComponent(start);
        selectRange.addComponent(length);
        selectRange.addComponent(b);
        fl.addComponent(selectRange);

        HorizontalLayout setCursorPosition = new HorizontalLayout();
        final TextField pos = new TextField("Position:");
        b = new Button("set");
        b.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                int startPos = Integer.parseInt((String) pos.getValue());
                tf.setCursorPosition(startPos);
            }
        });

        setCursorPosition.addComponent(pos);
        setCursorPosition.addComponent(b);
        setCursorPosition
                .setCaption("Set cursor position ( setCursorPosition(int pos) )");
        fl.addComponent(setCursorPosition);

        getLayout().addComponent(tf);
        getLayout().addComponent(panel);

    }

    @Override
    protected String getDescription() {
        return "For usability reasons it is often essential that developer "
                + "can hint how to select the text in the "
                + "field or where to set the cursor position.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2058;
    }

}
