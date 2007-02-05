/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.demo.features;

import java.util.Locale;

import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.*;

public class FeatureDateField extends Feature {

	static private IndexedContainer localeContainer;
	static {
		localeContainer = new IndexedContainer();
		localeContainer.addContainerProperty("name", String.class, "");
		Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++)
			localeContainer.addItem(locales[i]).getItemProperty("name")
					.setValue(locales[i].getDisplayName());
	}

	public FeatureDateField() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		DateField df = new DateField();
		df.setValue(new java.util.Date());
		l.addComponent(df);

		// Create locale selector
		// TODO: see #244 (broken for AJAX mode), known issue exists
		Select selector = new Select("Application Locale", localeContainer);
		selector.setItemCaptionPropertyId("name");
		selector.setImmediate(true);
		selector.setPropertyDataSource(new MethodProperty(
				this.getApplication(), "locale"));
		l.addComponent(selector);

		// Properties
		propertyPanel = new PropertyPanel(df);
		Form ap = propertyPanel
				.createBeanPropertySet(new String[] { "resolution" });
		ap.replaceWithSelect("resolution", new Object[] {
				new Integer(DateField.RESOLUTION_YEAR),
				new Integer(DateField.RESOLUTION_MONTH),
				new Integer(DateField.RESOLUTION_DAY),
				new Integer(DateField.RESOLUTION_HOUR),
				new Integer(DateField.RESOLUTION_MIN),
				new Integer(DateField.RESOLUTION_SEC),
				new Integer(DateField.RESOLUTION_MSEC) }, new Object[] {
				"Year", "Month", "Day", "Hour", "Minute", "Second",
				"Millisecond" });
		ap.getField("resolution").setValue(
				new Integer(DateField.RESOLUTION_DAY));
		Select themes = (Select) propertyPanel.getField("style");
		themes.addItem("text").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("text");
		themes.addItem("calendar").getItemProperty(
				themes.getItemCaptionPropertyId()).setValue("calendar");
		df.setStyle("calendar");
		propertyPanel.addProperties("DateField Properties", ap);

		return l;
	}

	protected String getExampleSrc() {
		return "DateField df = new DateField(\"Caption\");\n"
				+ "df.setValue(new java.util.Date());\n";

	}

	protected String getDescriptionXHTML() {
		return "<p>Representing Dates and times and providing a way to select "
				+ "or enter some specific date and/or time is an typical need in "
				+ "data-entry userinterfaces. IT Mill Toolkit provides a DateField "
				+ "component that is intuitive to use and yet controllable through "
				+ "its properties.</p>"
				+ "<p>The calendar-style allows point-and-click selection "
				+ "of dates while text-style shows only minimalistic user interface."
				+ "Validators may be bound to the component to check and "
				+ "validate the given input.</p>"
				+ "<p>On the demo tab you can try out how the different properties affect the "
				+ "presentation of the component.</p>";
	}

	protected String getImage() {
		return "datefield.jpg";
	}

	protected String getTitle() {
		return "DateField";
	}

}
