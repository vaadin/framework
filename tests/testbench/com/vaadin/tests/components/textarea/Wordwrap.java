package com.vaadin.tests.components.textarea;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

public class Wordwrap extends TestBase {

    @Override
    public void setup() {
        HorizontalLayout layout = new HorizontalLayout();

        TextArea area1 = new TextArea("Wrapping");
        area1.setWordwrap(true); // The default
        area1.setValue(LoremIpsum.get(50) + "\n" + "Another row");

        final TextArea area2 = new TextArea("Nonwrapping");
        area2.setWordwrap(false);
        area2.setValue(LoremIpsum.get(50) + "\n" + "Another row");

        layout.addComponent(area1);
        layout.addComponent(area2);
        layout.setSpacing(true);

        addComponent(layout);

        CheckBox onoff = new CheckBox("Wrap state for the right field");
        onoff.setValue(false);
        onoff.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean wrap = (Boolean) event.getProperty().getValue();
                area2.setWordwrap(wrap);
                if (wrap) {
                    area2.setCaption("Wrapping");
                } else {
                    area2.setCaption("Nonwrapping");
                }

            }
        });
        onoff.setImmediate(true);

        addComponent(onoff);
    }

    @Override
    protected String getDescription() {
        return "";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6003;
    }
}
