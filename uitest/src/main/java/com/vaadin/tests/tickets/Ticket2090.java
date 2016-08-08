package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2090 extends LegacyApplication {

    Label label = new Label();
    Button target = new Button();
    LegacyWindow w = new LegacyWindow("#2090");

    @Override
    public void init() {
        setMainWindow(w);
        final LegacyTextField width = new LegacyTextField("Width");
        width.setImmediate(true);
        final LegacyTextField height = new LegacyTextField("Height");
        height.setImmediate(true);
        w.addComponent(width);
        w.addComponent(height);
        w.addComponent(label);
        w.addComponent(target);
        height.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    target.setHeight(height.getValue());
                    height.setComponentError(null);
                    updateLabel();
                } catch (Exception e) {
                    height.setComponentError(new UserError(e.getMessage()));
                }
            }
        });
        width.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    target.setWidth(width.getValue());
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
                + target.getWidthUnits().getSymbol() + ", height: "
                + target.getHeight() + target.getHeightUnits().getSymbol());
    }

}
