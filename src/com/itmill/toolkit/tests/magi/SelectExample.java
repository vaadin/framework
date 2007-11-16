package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.ui.*;

/* Let us add an implementation of the ValueChangeListener interface. */
public class SelectExample extends CustomComponent implements Property.ValueChangeListener {

	class Planet extends Object {
		String planetName;

		Planet(String name) {
			planetName = name;
		}

		public String toString() {
			return "The Planet " + planetName;
		}
	}

	/* Create the Select object with a caption. */
	AbstractSelect select;

	OrderedLayout layout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
	Label status = new Label("");

	SelectExample(Application application, String param, String caption, boolean multiselect) {
		if (param.equals("optiongroup")) {
			select = new OptionGroup(caption);
			select.setMultiSelect(multiselect);
		} else if (param.equals("twincol")) {
			select = new TwinColSelect(caption);
		} else if (param.equals("native")) {
			select = new NativeSelect(caption);
		} else if (param.equals("filter")) {
			select = new Select(caption);
			((Select)select).setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
		} else { 
			select = new Select(caption);
			select.setMultiSelect(multiselect);
		}

		layout.addComponent(select);
		setCompositionRoot(layout);

		/* Fill the component with some items. */
		final String[] planets = new String[] {"Mercury", "Venus", "Earth",
				"Mars", "Jupiter", "Saturn", "Uranus", "Neptune" };

		for (int i = 0; i < planets.length; i++) {
			select.addItem(planets[i]);

			/* Create an item with an Integer as the Item ID. */
			// select.addItem(i);
			// select.addItem(new Planet(planets[i]));
			/* Set the visible caption of the item. */
			// select.setItemCaption(i, planets[i]);
			/*
			 * ClassResource icon = new ClassResource
			 * ("images/"+planets[i]+"_symbol.png", application);
			 * layout.addComponent(new Embedded ("Icon", icon));
			 * select.setItemIcon(i, icon);
			 */
		}

		/* By default, the change event is not triggered immediately when the
		 * selection changes. This enables it. */
		select.setImmediate(true);

		/* Listen for changes in the selection. */
		select.addListener(this);

		//select.setStyle("twincol");
		//select.setMultiSelect(true);
		//select.setNewItemsAllowed(true);
		// int a=1;

		// select.setItemCaptionMode(Select.ITEM_CAPTION_MODE_ICON_ONLY);
		//select.setNullSelectionItemId("-- select somethingd --");
		//select.setNullSelectionAllowed(false);

		layout.addComponent(status);
	}

	/* Respond to change in the selection. */
	public void valueChange(Property.ValueChangeEvent event) {
		/*
		 * The event.getProperty() returns the component. The currently selected
		 * item is the property of the component, retrievable with getValue().
		 */
		if (false) {
			status.setValue("Currently selected item ID: "
					+ event.getProperty().getValue() + "<br/>"
					+ "Class of the Item ID: "
					+ event.getProperty().getValue().getClass().getName()
					+ "<br/>" + "Caption: "
					+ select.getItemCaption(event.getProperty().getValue()));
			status.setContentMode(Label.CONTENT_XHTML);
		}
	}
}
