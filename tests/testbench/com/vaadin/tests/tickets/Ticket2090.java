package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2090 extends Application {

    Label label = new Label();
    Button target = new Button();
    Window w = new Window("#2090");

    @Override
    public void init() {
        setMainWindow(w);
        final TextField width = new TextField("Width");
        width.setImmediate(true);
        final TextField height = new TextField("Height");
        height.setImmediate(true);
        w.addComponent(width);
        w.addComponent(height);
        w.addComponent(label);
        w.addComponent(target);
        height.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                try {
                    target.setHeight(height.toString());
                    height.setComponentError(null);
                    updateLabel();
                } catch (Exception e) {
                    height.setComponentError(new UserError(e.getMessage()));
                }
            }
        });
        width.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                try {
                    target.setWidth(width.toString());
                    width.setComponentError(null);
                    updateLabel();
                } catch (Exception e) {
                    width.setComponentError(new UserError(e.getMessage()));
                }
            }
        });

    }

    private void updateLabel() {
        label.setValue("width: " + target.getWidth()
                + Sizeable.UNIT_SYMBOLS[target.getWidthUnits()] + ", height: "
                + target.getHeight()
                + Sizeable.UNIT_SYMBOLS[target.getHeightUnits()]);
    }

}
