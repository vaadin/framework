package com.vaadin.tests.components.customcomponent;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ClipContent extends TestBase {

    @Override
    protected void setup() {

        Label text = new Label(
                "1_long_line_that_should_be_clipped<br/>2_long_line_that_should_be_clipped<br/>3_long_line_that_should_be_clipped<br/>4_long_line_that_should_be_clipped<br/>",
                ContentMode.HTML);

        final CustomComponent cc = new CustomComponent(text);
        cc.setWidth("20px");
        cc.setHeight("20px");

        final TextField w = new TextField("Width");
        w.setValue("20px");
        w.addListener(new TextField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                cc.setWidth(w.getValue());
            }
        });
        addComponent(w);
        final TextField h = new TextField("Height");
        h.setValue("20px");
        h.addListener(new TextField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                cc.setHeight(h.getValue());
            }
        });
        addComponent(h);
        Button b = new Button("apply");
        addComponent(b);

        addComponent(cc);

    }

    @Override
    protected String getDescription() {
        return "The text in CustomComponent should be clipped if it has size defined.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
