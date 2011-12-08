package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Root;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * Test case for Ticket 2119.
 */
public class Ticket2119 extends Application.LegacyApplication {

    private ObjectProperty<String> globalValue;

    @Override
    public void init() {
        globalValue = new ObjectProperty<String>(null, String.class);
        Root main = createWindow();
        setMainWindow(main);
    }

    @Override
    public Root getWindow(String name) {
        if (!isRunning()) {
            return null;
        }
        // If we already have the requested window, use it
        Root w = super.getWindow(name);
        if (w == null) {
            // If no window found, create it
            w = createWindow();
            addWindow(w);
            w.open(new ExternalResource(getWindowUrl(w)));
        }
        return w;
    }

    private Root createWindow() {
        Root main = new Root("Test for ticket XXX");
        main.setContent(testLayout());
        return main;
    }

    private Layout testLayout() {
        final Layout layout = new VerticalLayout();
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

        final ObjectProperty<String> valueProperty = new ObjectProperty<String>(
                "1", String.class);
        select.setPropertyDataSource(valueProperty);
        layout.addComponent(select);

        globalValue.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                valueProperty.setValue((null != value) ? value.toString()
                        : null);
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
