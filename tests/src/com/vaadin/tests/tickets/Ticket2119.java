package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

/**
 * Test case for Ticket 2119.
 */
public class Ticket2119 extends Application {

    private ObjectProperty globalValue;

    @Override
    public void init() {
        globalValue = new ObjectProperty(null, String.class);
        Window main = createWindow();
        setMainWindow(main);
    }

    @Override
    public Window getWindow(String name) {
        if (!isRunning()) {
            return null;
        }
        // If we already have the requested window, use it
        Window w = super.getWindow(name);
        if (w == null) {
            // If no window found, create it
            w = createWindow();
            addWindow(w);
            w.open(new ExternalResource(w.getURL()));
        }
        return w;
    }

    private Window createWindow() {
        Window main = new Window("Test for ticket XXX");
        main.setLayout(testLayout());
        return main;
    }

    private Layout testLayout() {
        final Layout layout = new OrderedLayout();
        final Label label = new Label(
                "Instructions to reproduce:\n"
                        + "  - Open this application in two browser windows\n"
                        + "  - Click the Button in first Window\n"
                        + "  - Go to the second Window\n"
                        + "     - Click the arrow in the Select\n"
                        + "  --> The opened list correctly shows the new value but the old one is shown in the \"input\" part");
        label.setContentMode(Label.CONTENT_PREFORMATTED);
        layout.addComponent(label);

        final Select select = new Select("Test Select");
        select.setWidth("100px");
        select.setImmediate(true);
        select.setNullSelectionAllowed(false);
        select.addItem("1");
        select.addItem("2");
        select.addItem("3");

        final ObjectProperty valueProperty = new ObjectProperty("1",
                String.class);
        select.setPropertyDataSource(valueProperty);
        layout.addComponent(select);

        globalValue.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                valueProperty.setValue(event.getProperty().getValue());
            }
        });

        final Button changeValueButton = new Button("Change Value to 2");
        changeValueButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                globalValue.setValue("2");
            }
        });

        layout.addComponent(changeValueButton);

        return layout;
    }
}