package com.itmill.toolkit.demo.colorpicker;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Demonstration application that shows how to use a simple
 * custom client-side GWT component, the ColorPicker.
 * 
 * @author magi
 */
public class ColorPickerApplication extends com.itmill.toolkit.Application {
	Window main = new Window("Color Picker Demo");
	
	/* The custom component. */
	ColorPicker colorselector = new ColorPicker();
	
	/* Another component. */
	Label colorname;
	
	public void init() {
		setMainWindow(main);
		setTheme("demo");
		
		// Listen for value change events in the custom component,
		// triggered when user clicks a button to select another color.
		colorselector.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// Provide some server-side feedback
				colorname.setValue("Selected color: " + colorselector.getColor());
			}
		});
		main.addComponent(colorselector);
		
		// Add another component to give feedback from server-side code
		colorname = new Label("Selected color: "+colorselector.getColor());
		main.addComponent(colorname);
		
		// Server-side manipulation of the component state
		Button button = new Button("Set to white");
		button.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				colorselector.setColor("white");
			}
		});
		main.addComponent(button);
	}
}
