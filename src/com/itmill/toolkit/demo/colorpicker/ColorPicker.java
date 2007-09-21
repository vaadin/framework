package com.itmill.toolkit.demo.colorpicker;

import java.util.Map;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.*;

public class ColorPicker extends AbstractField {
	
	public ColorPicker() {
		super();
		setValue(new String("white"));
	}

	/** The property value of the field is an Integer. */
	public Class getType() {
		return String.class;
	}

	/** Tag is the UIDL element name for client-server communications. */
	public String getTag() {
		return "colorpicker";
	}
	
	/** Encode the property value of the field from RGB components. */
	public void setColor(String newcolor) {
		setValue(new String(newcolor));
	}
	
	/** Decode the property value of the field to RGB components. */
	public String getColor() {
		return (String) getValue();
	}

	/* Paint (serialize) the component for the client. */
	public void paintContent(PaintTarget target) throws PaintException {
		// Superclass writes any common attributes in the paint target.
		super.paintContent(target);
		
		// Set any values as variables of the paint target.
		target.addVariable(this, "colorname", getColor());
	}
	
	public void changeVariables(Object source, Map variables) {
		// Sets the currently selected color
		if (variables.containsKey("colorname") && !isReadOnly()) {
			String newValue = (String) variables.get("colorname");
			setValue(newValue,true);
		}
	}
}
