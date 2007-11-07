package com.itmill.toolkit.tests.magi;
import java.net.URL;

import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.terminal.*;

public class MagiTestApplication extends com.itmill.toolkit.Application implements URIHandler {
	Window main = new Window("Application window");

	TheButton butts1;
	TheButtons butts2;
	TheButtons2 butts3;

	Label mylabel1;
	Label mylabel2;
	Label mylabel3;
	
	StreamResource strres;
	OrderedLayout ol;

	public void init() {
		setTheme("tests-magi");

		setMainWindow(main);
	}

	public DownloadStream handleURI(URL context, String relativeUri) {
		/* Ignore ansynchronous request URIs in IT Mill Toolkit 4.0.x. */
		if (relativeUri.compareTo("UIDL/") == 0)
			return null;
		
		main.removeAllComponents();
		
		String test = relativeUri.substring(5);
		
		if (test.equals("defaultbutton"))			defaultButtonTest(main);
		else if (test.equals("tree"))				treeTest(main);
		else if (test.equals("embedded"))			init_embeddedTest(main);
		else if (test.equals("textfield"))			init_textFieldTest(main);
		else if (test.equals("textfieldvalidation"))textFieldValidation(main);
		else if (test.equals("datefield"))			init_dateFieldTest(main);
		else if (test.equals("button"))				init_buttonTest(main);
		else if (test.equals("select"))				init_selectTest(main);
		else if (test.equals("tabsheet"))			init_tabSheetTest(main);
		else if (test.equals("validator"))			init_validatorTest(main);
		else if (test.equals("table"))				init_tableTest(main);
		else if (test.equals("upload"))				init_uploadTest(main);
		else if (test.equals("link"))				init_linkTest(main);
		else if (test.equals("gridlayout"))			init_gridLayoutTest(main);
		else if (test.equals("panellayout"))		init_panelLayoutTest(main);
		else
			main.addComponent(new Label("Unknown test '"+test+"'."));		
			
		return null;
	}

	public void handleButton (Button.ClickEvent event) {
		ol.setStyle("myLayout2");
	}

	void defaultButtonTest(Window main) {
		main.addComponent(new DefaultButtonExample());
	}
	
	void treeTest(Window main) {
		final Object[][] planets = new Object[][]{
				new Object[]{"Mercury"}, 
				new Object[]{"Venus"},
				new Object[]{"Earth", "The Moon"},	
				new Object[]{"Mars", "Phobos", "Deimos"},
				new Object[]{"Jupiter", "Io", "Europa", "Ganymedes", "Callisto"},
				new Object[]{"Saturn", "Titan", "Tethys", "Dione", "Rhea", "Iapetus"},
				new Object[]{"Uranus", "Miranda", "Ariel", "Umbriel", "Titania", "Oberon"},
				new Object[]{"Neptune", "Triton", "Proteus", "Nereid", "Larissa"}};
		
		Tree tree = new Tree("The Planets and Major Moons");
		
		/* Add planets as root items in the tree. */
		for (int i=0; i<planets.length; i++) {
			String planet = (String) (planets[i][0]);
			tree.addItem(planet);
			
			if (planets[i].length == 1) {
				/* The planet has no moons so make it a leaf. */
				tree.setChildrenAllowed(planet, false);
			} else {
				/* Add children (moons) under the planets. */
				for (int j=1; j<planets[i].length; j++) {
					String moon = (String) planets[i][j];
					
					/* Add the item as a regular item. */
					tree.addItem(moon);
					
					/* Set it to be a child. */
					tree.setParent(moon, planet);
					
					/* Make the moons look like leaves. */
					tree.setChildrenAllowed(moon, false);
				}

				/* Expand the subtree. */
				tree.expandItemsRecursively(planet);
			}
		}

		
		main.addComponent(tree);
	}

	void init_selectTest(Window main) {
		main.addComponent(new SelectExample(this));
	}
	
	void init_textFieldTest(Window main) {
		/* Add a single-line text field. */
		TextField subject = new TextField("Subject");
		subject.setColumns(40);
		main.addComponent(subject);

		/* Add a multi-line text field. */
		TextField message = new TextField("Message");
		message.setRows(7);
		message.setColumns(40);
		main.addComponent(message);
	}
	
	void textFieldValidation(Window main) {
		// Create a text field with a label
		TextField username = new TextField("Username");
		main.addComponent(username);
		
		// Set visible length to 16 characters
		username.setColumns(16);
		
		// Set content length to minimum of 6 and maximum of 16 characters.
		// The string also may not be null.
		username.addValidator(
				new StringLengthValidator("Must be 6 to 16 characters long",
										  6, 16, false));

		// Setting component immediate causes a ValueChangeEvent to occur
		// when the TextField loses focus.
		username.setImmediate(true);
		
		// Listen for ValueChangeEvents and handle them
		username.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// Get the source of the event
				TextField username = (TextField)(event.getProperty());

				try {
					// Validate the field value.
					username.validate();
					
					// The value was ok, reset a possible error
					username.setComponentError(null);
				} catch (Validator.InvalidValueException e) {
					// The value was not ok. Set the error.
					username.setComponentError(new UserError(e.getMessage()));
				}
			}
		});
	}

	void init_dateFieldTest(Window main) {
		/* Create a DateField with the calendar style. */
		DateField date = new DateField("Here is a calendar field");
		//date.setStyle("calendar");
		
		/* Set the date and time to present. */
		date.setValue(new java.util.Date());

		main.addComponent(date);

		//date.setResolution(DateField.RESOLUTION_DAY);
	}
	void init_tabSheetTest(Window main) {
		//main.addComponent(new TabSheetExample());

		TabSheet tabsheet = new TabSheet();
		/*
		tabsheet.addTab(new Label("Contents of the first tab"), "First Tab", null);
		tabsheet.addTab(new Label("Contents of the second tab"), "Second Tab", null);
		tabsheet.addTab(new Label("Contents of the third tab"), "Third Tab", null);
		*/
		
		tabsheet.addTab(new Label("Contents of the first tab"),
						"First Tab",
						new ClassResource ("images/Mercury_small.png", main.getApplication()));
		tabsheet.addTab(new Label("Contents of the second tab"),
						"Second Tab",
						new ClassResource ("images/Venus_small.png", main.getApplication()));
		tabsheet.addTab(new Label("Contents of the third tab"),
						"Third tab",
						new ClassResource ("images/Earth_small.png", main.getApplication()));
			
		main.addComponent(tabsheet);
	}

	void init_validatorTest(Window main) {
		main.addComponent(new SSNField());
	}

	void init_tableTest(Window main) {
		main.addComponent(new TableExample());
	}

	void init_uploadTest(Window main) {
		main.addComponent(new MyUploader());
	}

	void init_linkTest(Window main) {
		/* Create a native window object to be opened as a popup window when 
		 * the link is clicked. */
		Window popup = new Window("Open a window");
		popup.setStyle("native");
		popup.addComponent(new Button("Some component"));
		main.getApplication().addWindow(popup);
		
		/* Create a link that opens the popup window. */
		Link alink = new Link (popup);
		
		/* Set the resource to be opened in the window. */
		alink.setResource(new ExternalResource("http://www.itmill.com/"));
		
		main.addComponent(alink);

		ClassResource mydocument = new ClassResource ("mydocument.pdf", this);
		main.addComponent(new Link ("The document (pdf)", mydocument));
		main.addComponent(new Link ("link to a resource", new ExternalResource("http://www.itmill.com/")));
	}

	void init_labelTest(Window main) {
		GridLayout labelgrid = new GridLayout (2,1);
		labelgrid.setStyle("labelgrid");
		labelgrid.addComponent (new Label ("CONTENT_DEFAULT"));
		labelgrid.addComponent (new Label ("This is a label in default mode: <plain text>", Label.CONTENT_DEFAULT));
		labelgrid.addComponent (new Label ("CONTENT_PREFORMATTED"));
		labelgrid.addComponent (new Label ("This is a preformatted label.\nThe newline character \\n breaks the line.", Label.CONTENT_PREFORMATTED));
		labelgrid.addComponent (new Label ("CONTENT_RAW"));
		labelgrid.addComponent (new Label ("This is a label in raw mode.<br>It can contain, for example, unbalanced markup.", Label.CONTENT_RAW));
		labelgrid.addComponent (new Label ("CONTENT_TEXT"));
		labelgrid.addComponent (new Label ("This is a label in (plain) text mode", Label.CONTENT_TEXT));
		labelgrid.addComponent (new Label ("CONTENT_XHTML"));
		labelgrid.addComponent (new Label ("<i>This</i> is an <b>XHTML<b> formatted label", Label.CONTENT_XHTML));
		labelgrid.addComponent (new Label ("CONTENT_XML"));
		labelgrid.addComponent (new Label ("This is an <myelement>XML</myelement> formatted label", Label.CONTENT_XML));
		main.addComponent(labelgrid);
		
		ClassResource labelimage = new ClassResource ("smiley.jpg", this);
		main.addComponent(new Label("Here we have an image <img src=\""
									+ this.getRelativeLocation(labelimage) + "\"/> within some text.",
									Label.CONTENT_XHTML));
	}
		
	void init_buttonTest(Window main) {
		/*
		main.addComponent(mylabel1 = new Label ("Laabeli 1"));
		main.addComponent(mylabel2 = new Label ("Laabeli 2"));
		main.addComponent(mylabel3 = new Label ("Laabeli 3"));
		*/
		//butts1 = new TheButton ();
		//main.addComponent(butts1);
		
		//butts2 = new TheButtons (main);
		//butts3 = new TheButtons2 (main);
		
		//Button checkbox = new Button ("This is a checkbox");
		
		//main.addComponent(checkbox);
		Button button = new Button("My Button");
		button.setStyle("link");
		main.addComponent(button);
	}
	
	void init_panelLayoutTest(Window main) {
		Panel panel = new Panel ("Contact Information");
		OrderedLayout ordered = new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL);
		ordered.addComponent(new TextField("Name"));
		ordered.addComponent(new TextField("Email"));
		ordered.setStyle("form");
		for (int i=0; i<20; i++)
			ordered.addComponent(new Label("a row"));
		panel.setIcon(new ClassResource ("smiley.jpg", main.getApplication()));
		panel.addComponent(ordered);
		main.addComponent(panel);
	}
	
	void init_gridLayoutTest(Window main) {
		/* Create a 4 by 4 grid layout. */
		GridLayout gridLO = new GridLayout(4, 4);

		/* Fill out the first row using the cursor. */
		gridLO.addComponent(new Button("R/C 1"));
		for (int i=0; i<3; i++) /* Add a few buttons. */
			gridLO.addComponent(new Button("Col " + (gridLO.getCursorX()+1)));

		/* Fill out the first column using coordinates. */
		for (int i=1; i<4; i++)
			gridLO.addComponent(new Button("Row " + i), 0, i);

		/* Add some components of various shapes. */
		gridLO.addComponent(new Button("3x1 button"), 1, 1, 3, 1);
		gridLO.addComponent(new Label("1x2 cell"), 1, 2, 1, 3);
		DateField date = new DateField("A 2x2 date field");
		date.setStyle("calendar");
		gridLO.addComponent(date, 2, 2, 3, 3);

		//gridLO.setStyle("example-bordered");
		main.addComponent(gridLO);
	}
	
	void init_orderedLayoutTest(Window main) {
		OrderedLayout orderedLO = new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL);
		orderedLO.addComponent(new TextField("Name"));
		orderedLO.addComponent(new TextField("Street address"));
		orderedLO.addComponent(new TextField("Postal code"));
		/* orderedLO.setStyle("form"); */
		main.addComponent(orderedLO);
	}

	void init_windowTest() {
		Window mydialog = new Window("My Dialog");
		mydialog.addComponent(new Label("A text label in the window."));
		Button okbutton = new Button("OK");
		mydialog.addComponent(okbutton);
		addWindow(mydialog);
	}

	void init_embeddedTest(Window main) {
		//main.addComponent(new Embedded("Image title", new ClassResource("smiley.jpg", this)));
		Embedded image = new Embedded ("", new ClassResource("smiley.jpg", this));
		image.setStyle("omaimage");
		main.addComponent(image);
		
		EmbeddedButton button = new EmbeddedButton(new ClassResource("smiley.jpg", this));
		main.addComponent(button);
		
	}

}
