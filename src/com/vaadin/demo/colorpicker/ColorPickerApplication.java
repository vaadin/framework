/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.colorpicker;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Demonstration application that shows how to use a simple custom client-side
 * GWT component, the ColorPicker.
 */
public class ColorPickerApplication extends com.vaadin.Application {
    Window main = new Window("Color Picker Demo");

    /* The custom component. */
    ColorPicker colorselector = new ColorPicker();

    /* Another component. */
    Label colorname;

    @Override
    public void init() {
        setMainWindow(main);

        // Listen for value change events in the custom component,
        // triggered when user clicks a button to select another color.
        colorselector.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                // Provide some server-side feedback
                colorname.setValue("Selected color: "
                        + colorselector.getColor());
            }
        });
        main.addComponent(colorselector);

        // Add another component to give feedback from server-side code
        colorname = new Label("Selected color: " + colorselector.getColor());
        main.addComponent(colorname);

        // Server-side manipulation of the component state
        final Button button = new Button("Set to white");
        button.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                colorselector.setColor("white");
            }
        });
        main.addComponent(button);
    }
}
