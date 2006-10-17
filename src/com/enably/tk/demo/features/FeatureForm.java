/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.demo.features;

import java.util.Date;

import com.enably.tk.data.Property;
import com.enably.tk.ui.*;

public class FeatureForm
	extends Feature
	implements Property.ValueChangeListener {

	OrderedLayout demo = null;
	Form test;
	Layout formLayout = null;
	Select addField = new Select("Add field");
	Select resetLayout = new Select("Restart");

	protected Component getDemoComponent() {

		if (demo == null) {
			demo = new OrderedLayout();
			createDemo();
		}

		return demo;
	}

	private void createDemo() {

		demo.removeAllComponents();

		// Test form
		Panel testPanel = new Panel("Form component");
		if (formLayout == null)
			test = new Form();
		else
			test = new Form(formLayout);
		testPanel.addComponent(test);
		demo.addComponent(testPanel);
		OrderedLayout actions =
			new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		demo.addComponent(actions);

		// form adder
		addField.setImmediate(true);
		addField.addItem("Add field");
		addField.setNullSelectionItemId("Add field");
		addField.addItem("Text field");
		addField.addItem("Time");
		addField.addItem("Option group");
		addField.addItem("Calendar");
		addField.addListener(this);
		actions.addComponent(addField);

		// Layout reset
		resetLayout.setImmediate(true);
		resetLayout.addItem("Select layout example");
		resetLayout.setNullSelectionItemId("Select layout example");
		resetLayout.addItem("Vertical form (OrderedLayout form-style)");
		resetLayout.addItem("Two columns (2x1 GridLayout)");
		resetLayout.addItem("Flow (OrderedLayout flow-orientation)");
		resetLayout.addListener(this);
		actions.addComponent(resetLayout);

		// Properties
		PropertyPanel p = new PropertyPanel(test);
		p.addProperties("Form special properties", new Form());
		demo.addComponent(p);
	}

	public void valueChange(Property.ValueChangeEvent event) {

		if (event.getProperty() == resetLayout) {

			String value = (String) resetLayout.getValue();

			if (value != null) {
				formLayout = null;

				if (value.equals("Two columns (2x1 GridLayout)"))
					formLayout = new GridLayout(2, 1);
				if (value.equals("Horizontal (OrderedLayout)"))
					formLayout =
						new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);

				createDemo();
				resetLayout.setValue(null);
			}
		}

		if (event.getProperty() == addField) {

			String value = (String) addField.getValue();

			if (value != null) {
				if (value.equals("Text field"))
					test.addField(new Object(), new TextField("Test field"));
				if (value.equals("Time")) {
					DateField d = new DateField("Time", new Date());
					d.setDescription(
						"This is a DateField-component with text-style");
					d.setResolution(DateField.RESOLUTION_MIN);
					d.setStyle("text");
					test.addField(new Object(), d);
				}
				if (value.equals("Calendar")) {
					DateField c = new DateField("Calendar", new Date());
					c.setDescription(
						"DateField-component with calendar-style and day-resolution");
					c.setStyle("calendar");
					c.setResolution(DateField.RESOLUTION_DAY);
					test.addField(new Object(), c);
				}
				if (value.equals("Option group")) {
					Select s = new Select("Options");
					s.setDescription("Select-component with optiongroup-style");
					s.addItem("Linux");
					s.addItem("Windows");
					s.addItem("Solaris");
					s.addItem("Symbian");
					s.setStyle("optiongroup");

					test.addField(new Object(), s);
				}

				addField.setValue(null);
			}
		}
	}

	protected String getDescriptionXHTML() {
		return 
			"<p>Form is a flexible, yet simple container for fields. "
				+ " It provides support for any layouts and provides buffering interface for"
				+ " easy connection of commit- and discard buttons. All the form"
				+ " fields can be customized by adding validators, setting captions and icons, "
				+ " setting immediateness, etc. Also direct mechanism for replacing existing"
				+ " fields with selections is given.</p>"
				+ " <p>Form provides customizable editor for classes implementing"
				+ " Item-interface. Also the form itself"
				+ " implements this interface for easier connectivity to other items."
				+ " To use the form as editor for an item, just connect the item to"
				+ " form.After the item has been connected to the form,"
				+ " the automatically created fields can be customized and new fields can"
				+ " be added. If you need to connect a class that does not implement"
				+ " Item-interface, most properties of any"
				+ " class following bean pattern, can be accessed trough"
				+ " BeanItem.</p>"
				+ " <p>The best example of Form usage is the this feature browser itself; "
				+ " all the Property-panels in demos are composed of Form-components.</p>";
		}
		
		
	protected String getTitle() {
		return "Form";
	}

	protected String getImage() {
		return "form.jpg";
	}
	
}
