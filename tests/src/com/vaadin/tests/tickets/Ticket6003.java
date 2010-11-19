package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class Ticket6003 extends TestBase {

    @Override
    public void setup() {
        Window main = new Window("The Main Window");
        setMainWindow(main);

        final VerticalLayout mainLayout = new VerticalLayout();
        main.setContent(mainLayout);

        HorizontalLayout layout = new HorizontalLayout();

        TextArea area1 = new TextArea("Wrapping");
        area1.setWordwrap(true); // The default
        area1.setValue("A quick brown fox jumps over the lazy dog");

        final TextArea area2 = new TextArea("Nonwrapping");
        area2.setWordwrap(false);
        area2.setValue("Victor jagt zwölf Boxkämpfer quer "
                + "über den Sylter Deich");

        layout.addComponent(area1);
        layout.addComponent(area2);
        layout.setSpacing(true);

        mainLayout.addComponent(layout);

        CheckBox onoff = new CheckBox("Wrap state for the right field");
        onoff.setValue(false);
        onoff.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                area2.setWordwrap((Boolean) event.getProperty().getValue());
            }
        });
        onoff.setImmediate(true);

        mainLayout.addComponent(onoff);
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
