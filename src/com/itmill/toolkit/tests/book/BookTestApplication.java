/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.book;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.terminal.gwt.server.WebApplicationContext;
import com.itmill.toolkit.ui.AbstractSelect;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.InlineDateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.MenuBar;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.PopupDateField;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;
import com.itmill.toolkit.ui.MenuBar.MenuItem;

public class BookTestApplication extends com.itmill.toolkit.Application {
	Window main = new Window("Application window");

	TheButton butts1;
	TheButtons butts2;
	TheButtons2 butts3;

	Label mylabel1;
	Label mylabel2;
	Label mylabel3;

	StreamResource strres;
	VerticalLayout ol;
	int getwincount = 0;

	@Override
	public void init() {
		setTheme("tests-book");

		setMainWindow(main);

		// Demo the use of parameter and URI handlers
		main.addParameterHandler(new MyParameterHandler());
		main.addURIHandler(new MyURIHandler());

		MyDynamicResource myresource = new MyDynamicResource();
		main.addParameterHandler(myresource);
		main.addURIHandler(myresource);

		main.addURIHandler(new BookTestURIHandler());
	}

	class MyParameterHandler implements ParameterHandler {
		public void handleParameters(Map parameters) {
			// Print out the parameters to standard output
			for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				String value = ((String[]) parameters.get(key))[0];
				System.out.println("Key: " + key + ", value: " + value);
			}
		}
	}

	class MyURIHandler implements URIHandler {
		public DownloadStream handleURI(URL context, String relativeUri) {
			System.out.println("Context: " + context.toString()
					+ ", relative: " + relativeUri);
			return null; // Let the Application provide the response
		}
	}

	class BookTestURIHandler implements URIHandler {
		public DownloadStream handleURI(URL context, String relativeUri) {
			String example;
			String param = null;

			final int slashPos = relativeUri.indexOf("/");
			if (slashPos > 0) {
				example = relativeUri.substring(0, slashPos);
				param = relativeUri.substring(slashPos + 1);
			} else {
				example = relativeUri;
			}

			/* Remove existing components and windows. */
			main.removeAllComponents();
			final Set childwindows = main.getChildWindows();
			for (final Iterator cwi = childwindows.iterator(); cwi.hasNext();) {
				final Window child = (Window) cwi.next();
				main.removeWindow(child);
			}

			// The index is listed inside a grid layout
			main.setLayout(new VerticalLayout());
			GridLayout grid = new GridLayout(4, 4);
			grid.addStyleName("index");
			main.addComponent(grid);

			if (example.equals("index")) {
				final String examples[] = { "defaultbutton", "label",
						"labelcontent", "tree", "embedded", "textfield",
						"textfieldvalidation", "datefield", "button",
						"select/select", "select/native", "select/optiongroup",
						"select/twincol", "filterselect", "validator", "table",
						"table/select", "table/component", "table/paging",
						"table/editable", "upload", "link", "gridlayout",
						"orderedlayout", "formlayout", "form", "form/simple",
						"form/layout", "panel", "expandlayout",
						"expandlayout/root", "tabsheet", "alignment",
						"alignment/grid", "window", "window/opener",
						"window/multiple", "classresource", "usererror",
						"progress/window", "progress/thread", "progress",
						"customlayout", "spacing", "margin", "clientinfo",
						"fillinform/templates", "notification", "print",
						"richtextfield", "querycontainer", "menubar" };
				for (int i = 0; i < examples.length; i++) {
					grid.addComponent(new Label("<a href='"
							+ context.toString() + examples[i] + "'>"
							+ examples[i] + "</a>", Label.CONTENT_XHTML));
				}
				return null;
			}

			if (example.equals("defaultbutton")) {
				example_defaultButton(main, param);
			} else if (example.equals("label")) {
				example_Label(main, param);
			} else if (example.equals("labelcontent")) {
				example_LabelContent(main, param);
			} else if (example.equals("tree")) {
				example_Tree(main, param);
			} else if (example.equals("embedded")) {
				example_Embedded(main, param);
			} else if (example.equals("textfield")) {
				example_TextField(main, param);
			} else if (example.equals("textfieldvalidation")) {
				example_TextFieldValidation(main, param);
			} else if (example.equals("usererror")) {
				example_UserError(main, param);
			} else if (example.equals("datefield")) {
				example_DateField(main, param);
			} else if (example.equals("button")) {
				example_Button(main, param);
			} else if (example.equals("checkbox")) {
				example_CheckBox(main, param);
			} else if (example.equals("select")) {
				example_Select(main, param);
			} else if (example.equals("filterselect")) {
				example_FilterSelect(main, param);
			} else if (example.equals("validator")) {
				example_Validator(main, param);
			} else if (example.equals("table")) {
				example_Table(main, param);
			} else if (example.equals("upload")) {
				example_Upload(main, param);
			} else if (example.equals("link")) {
				example_Link(main, param);
			} else if (example.equals("gridlayout")) {
				example_GridLayout(main, param);
			} else if (example.equals("orderedlayout")) {
				example_OrderedLayout(main, param);
			} else if (example.equals("formlayout")) {
				example_FormLayout(main, param);
			} else if (example.equals("form")) {
				example_Form(main, param);
			} else if (example.equals("tabsheet")) {
				example_TabSheet(main, param);
			} else if (example.equals("panel")) {
				example_Panel(main, param);
			} else if (example.equals("expandlayout")) {
				example_ExpandLayout(main, param);
			} else if (example.equals("alignment")) {
				example_Alignment(main, param);
			} else if (example.equals("window")) {
				example_Window(main, param);
			} else if (example.equals("classresource")) {
				example_ClassResource(main, param);
			} else if (example.equals("progress")) {
				example_ProgressIndicator(main, param);
			} else if (example.equals("customlayout")) {
				example_CustomLayout(main, param);
			} else if (example.equals("spacing")) {
				example_Spacing(main, param);
			} else if (example.equals("margin")) {
				example_Margin(main, param);
			} else if (example.equals("clientinfo")) {
				example_ClientInfo(main, param);
			} else if (example.equals("fillinform")) {
				example_FillInForm(main, param);
			} else if (example.equals("notification")) {
				example_Notification(main, param);
			} else if (example.equals("print")) {
				example_Print(main, param);
			} else if (example.equals("richtextfield")) {
				example_RichTextArea(main, param);
			} else if (example.equals("querycontainer")) {
				example_QueryContainer(main, param);
			} else if (example.equals("menubar")) {
				example_MenuBar(main, param);
			} else {
				; // main.addComponent(new
				// Label("Unknown test '"+example+"'."));
			}

			return null;
		}
	}

	/*
	 * public Window getWindow(String name) { Window superwin =
	 * super.getWindow(name); if (superwin != null) return superwin;
	 * 
	 * main.addComponent(new Label("Request 2 for window '"+name+"'.")); if
	 * (name.equals("panel")) { Window window = new Window("Other Window " +
	 * getwincount++); example_Panel(window, null); return window; } return
	 * null; }
	 */
	public void handleButton(Button.ClickEvent event) {
		ol.addStyleName("myLayout2");
	}

	void example_defaultButton(Window main, String param) {
		main.addComponent(new DefaultButtonExample());
	}

	void example_Label(Window main, String param) {
		/* Some container for the Label. */
		final Panel panel = new Panel("Panel Containing a Label");
		main.addComponent(panel);

		panel.addComponent(new Label(
				"This is a Label inside a Panel. There is enough "
						+ "text in the label to make the text wrap if it "
						+ "exceeds the width of the panel."));
	}

	void example_LabelContent(Window main, String param) {
		final GridLayout labelgrid = new GridLayout(2, 1);
		labelgrid.addStyleName("labelgrid");
		labelgrid.addComponent(new Label("CONTENT_DEFAULT"));
		labelgrid.addComponent(new Label(
				"This is a label in default mode: <plain text>",
				Label.CONTENT_DEFAULT));
		labelgrid.addComponent(new Label("CONTENT_PREFORMATTED"));
		labelgrid
				.addComponent(new Label(
						"This is a preformatted label.\nThe newline character \\n breaks the line.",
						Label.CONTENT_PREFORMATTED));
		labelgrid.addComponent(new Label("CONTENT_RAW"));
		labelgrid
				.addComponent(new Label(
						"This is a label in raw mode.<br>It can contain, for example, unbalanced markup.",
						Label.CONTENT_RAW));
		labelgrid.addComponent(new Label("CONTENT_TEXT"));
		labelgrid.addComponent(new Label(
				"This is a label in (plain) text mode", Label.CONTENT_TEXT));
		labelgrid.addComponent(new Label("CONTENT_XHTML"));
		labelgrid.addComponent(new Label(
				"<i>This</i> is an <b>XHTML<b> formatted label",
				Label.CONTENT_XHTML));
		labelgrid.addComponent(new Label("CONTENT_XML"));
		labelgrid.addComponent(new Label(
				"This is an <myelement>XML</myelement> formatted label",
				Label.CONTENT_XML));
		main.addComponent(labelgrid);

		final ClassResource labelimage = new ClassResource("smiley.jpg", this);
		main.addComponent(new Label("Here we have an image <img src=\""
				+ getRelativeLocation(labelimage) + "\"/> within some text.",
				Label.CONTENT_XHTML));
	}

	void example_Tree(Window main, String param) {
		final Object[][] planets = new Object[][] {
				new Object[] { "Mercury" },
				new Object[] { "Venus" },
				new Object[] { "Earth", "The Moon" },
				new Object[] { "Mars", "Phobos", "Deimos" },
				new Object[] { "Jupiter", "Io", "Europa", "Ganymedes",
						"Callisto" },
				new Object[] { "Saturn", "Titan", "Tethys", "Dione", "Rhea",
						"Iapetus" },
				new Object[] { "Uranus", "Miranda", "Ariel", "Umbriel",
						"Titania", "Oberon" },
				new Object[] { "Neptune", "Triton", "Proteus", "Nereid",
						"Larissa" } };

		final Tree tree = new Tree();

		// Add planets as root items in the tree.
		for (int i = 0; i < planets.length; i++) {
			final String planet = (String) (planets[i][0]);
			tree.addItem(planet);

			if (planets[i].length == 1) {
				// The planet has no moons so make it a leaf.
				tree.setChildrenAllowed(planet, false);
			} else {
				// Add children (moons) under the planets.
				for (int j = 1; j < planets[i].length; j++) {
					final String moon = (String) planets[i][j];

					// Add the item as a regular item.
					tree.addItem(moon);

					// Set it to be a child.
					tree.setParent(moon, planet);

					// Make the moons look like leaves.
					tree.setChildrenAllowed(moon, false);
				}

				// Expand the subtree.
				tree.expandItemsRecursively(planet);
			}
		}

		// Horizontal layout with the tree on the left and a details panel on
		// the right.
		final HorizontalLayout horlayout = new HorizontalLayout();
		horlayout.addStyleName("treeexample");
		horlayout.setSizeFull();

		final Panel treepanel = new Panel("The Planets and Major Moons");
		treepanel.addComponent(tree);
		horlayout.addComponent(treepanel);

		final Panel detailspanel = new Panel("Details");
		horlayout.addComponent(detailspanel);
		horlayout.setExpandRatio(detailspanel, 1);

		final VerticalLayout detailslayout = new VerticalLayout();
		detailspanel.setLayout(detailslayout);

		// Allow null selection - this is the default actually.
		tree.setNullSelectionAllowed(true);

		// When a tree item (planet or moon) is clicked, open the item in
		// Details view.
		tree.setImmediate(true);
		tree.addListener(new ValueChangeListener() {
			String lastselected = null;

			public void valueChange(ValueChangeEvent event) {
				String planet = (String) tree.getValue();

				// Reselect a selected item if it is unselected by clicking it.
				if (planet == null) {
					planet = lastselected;
					tree.setValue(planet);
				}
				lastselected = planet;

				detailspanel.setCaption("Details on " + planet);
				detailslayout.removeAllComponents();

				// Put some stuff in the Details view.
				detailslayout.addComponent(new Label("Where is the cat?"));
				detailslayout.addComponent(new Label("The cat is in " + planet
						+ "."));

			}
		});

		main.setLayout(horlayout);
	}

	void example_Select(Window main, String param) {
		final HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName("aligntop");

		if (param.equals("twincol")) {
			final SelectExample select1 = new SelectExample(this, param,
					"Select some items", true);
			layout.addComponent(select1);
		} else if (param.equals("filter")) {
			final SelectExample select1 = new SelectExample(this, param,
					"Enter containing substring", false);
			layout.addComponent(select1);
		} else {
			final SelectExample select1 = new SelectExample(this, param,
					"Single Selection Mode", false);
			final SelectExample select2 = new SelectExample(this, param,
					"Multiple Selection Mode", true);
			layout.addComponent(select1);
			layout.addComponent(select2);
		}
		main.addComponent(layout);
	}

	void example_FilterSelect(Window main, String param) {
		final Select select = new Select("Enter containing substring");
		main.addComponent(select);

		select
				.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);

		/* Fill the component with some items. */
		final String[] planets = new String[] { "Mercury", "Venus", "Earth",
				"Mars", "Jupiter", "Saturn", "Uranus", "Neptune" };

		for (int i = 0; i < planets.length; i++) {
			for (int j = 0; j < planets.length; j++) {
				select.addItem(planets[j] + " to " + planets[i]);
			}
		}
	}

	void example_TextField(Window main, String param) {
		/* Add a single-line text field. */
		final TextField subject = new TextField("Subject");
		subject.setColumns(40);
		main.addComponent(subject);

		/* Add a multi-line text field. */
		final TextField message = new TextField("Message");
		message.setRows(7);
		message.setColumns(40);
		main.addComponent(message);
	}

	void example_TextFieldValidation(Window main, String param) {
		// Create a text field with a label
		final TextField username = new TextField("Username");
		main.addComponent(username);

		// Set visible length to 16 characters
		username.setColumns(16);

		// Set content length to minimum of 6 and maximum of 16 characters.
		// The string also may not be null.
		username.addValidator(new StringLengthValidator(
				"Must be 6 to 16 characters long", 6, 16, false));

		// Setting component immediate causes a ValueChangeEvent to occur
		// when the TextField loses focus.
		username.setImmediate(true);

		// Listen for ValueChangeEvents and handle them
		username.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				// Get the source of the event
				final TextField username = (TextField) (event.getProperty());

				try {
					// Validate the field value.
					username.validate();
				} catch (final Validator.InvalidValueException e) {
					// The value was not ok. The error was set.
				}
			}
		});
	}

	void example_UserError(final Window main, String param) {
		if (param != null) {
			if (param.equals("form")) {

				final FormLayout layout = new FormLayout();
				main.addComponent(layout);

				final TextField textfield = new TextField("Enter code");
				layout.addComponent(textfield);
				textfield.setComponentError(null);

				final Button button = new Button("Ok!");
				layout.addComponent(button);

				button.addListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						if (((String) textfield.getValue()).length() == 0) {
							textfield.setComponentError(new UserError(
									"Must be letters and numbers."));
						} else {
							textfield.setComponentError(null);
						}
					}
				});
			}
		} else {
			main.setLayout(new HorizontalLayout());

			// Create a field.
			final TextField textfield = new TextField("Enter code");
			main.addComponent(textfield);

			// Let the component error be initially clear. (It actually is by
			// default.)
			textfield.setComponentError(null);

			// Have a button right of the field (and align it properly).
			final Button button = new Button("Ok!");
			main.addComponent(button);
			((HorizontalLayout) main.getLayout()).setComponentAlignment(button,
					HorizontalLayout.ALIGNMENT_LEFT,
					HorizontalLayout.ALIGNMENT_BOTTOM);

			// Handle button clicks
			button.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					// If the field value is bad, set its error.
					// (Here the content must be only alphanumeric characters.)
					if (!((String) textfield.getValue()).matches("^\\w*$")) {
						// Put the component in error state and set the error
						// message.
						textfield.setComponentError(new UserError(
								"Must be letters and numbers"));
					} else {
						// Otherwise clear it.
						textfield.setComponentError(null);
					}
				}
			});
		}
	}

	void example_DateField(Window main, String param) {
		HorizontalLayout layout = new HorizontalLayout();

		/* Create a DateField with the calendar style. */
		final DateField popupdate = new PopupDateField("Popup calendar field");

		/* Set resolution of the date/time display. */
		popupdate.setResolution(DateField.RESOLUTION_MIN);

		/* Set the date and time to present. */
		popupdate.setValue(new java.util.Date());

		/* Create a DateField with the calendar style. */
		final DateField inlinedate = new InlineDateField(
				"Inline calendar field");

		/* Set locale of the DateField to American English. */
		inlinedate.setLocale(new Locale("en", "US"));

		/* Set the date and time to present. */
		inlinedate.setValue(new java.util.Date());

		/* Set resolution of the date/time display. */
		inlinedate.setResolution(DateField.RESOLUTION_MIN);

		layout.addComponent(popupdate);
		layout.addComponent(inlinedate);
		layout.setSpacing(true);
		main.addComponent(layout);
	}

	void example_Validator(Window main, String param) {
		if (param != null && param.equals("required")) {
			Form form = new Form();
			form.setCaption("My Form");
			form.setRequired(true);
			main.addComponent(form);

			TextField text = new TextField("This is a required text field");
			text.setRequired(true);
			text.setImmediate(true);
			form.getLayout().addComponent(text);
			return;
		}
		main.addComponent(new SSNField());
	}

	class PagingTable extends Table {
		@Override
		public String getTag() {
			return "pagingtable";
		}
	}

	void example_Table(Window main, String param) {
		if (param != null) {
			if (param.equals("select")) {
				main.addComponent(new TableExample2());
			} else if (param.equals("component")) {
				main.addComponent(new TableExample3());
			} else if (param.equals("editable")) {
				main.addComponent(new TableEditable());
			} else if (param.equals("bean")) {
				main.addComponent(new TableEditableBean());
			} else if (param.equals("long")) {
				main.addComponent(new TableExample());
			} else if (param.equals("cellstyle")) {
				main.addComponent(new TableCellStyle());
			} else if (param.equals("huge")) {
				main.addComponent(new TableHuge());
			} else if (param.equals("paging")) {
				PagingTable table = new PagingTable();
				table.addContainerProperty("Column 1", String.class, null);
				for (int i = 0; i < 100; i++) {
					table.addItem(new Object[] { "Item " + i }, new Integer(i));
				}
				main.addComponent(table);
			}
		} else {
			main.addComponent(new TableExample1());
		}
	}

	void example_Upload(Window main, String param) {
		main.addComponent(new MyUploader());
	}

	void example_Link(Window main, String param) {

		/* Create a link that opens the popup window. */
		final Link alink = new Link();

		/* Set the resource to be opened in the window. */
		alink.setResource(new ExternalResource("http://www.itmill.com/"));

		main.addComponent(alink);

		final ClassResource mydocument = new ClassResource("mydocument.pdf",
				this);
		main.addComponent(new Link("The document (pdf)", mydocument));
		main.addComponent(new Link("link to a resource", new ExternalResource(
				"http://www.itmill.com/")));
	}

	void example_Button(Window main, String param) {
		if (param != null) {
			if (param.equals("buttons")) {
				main.addComponent(new TheButton());
			}
			return;
		}

		// butts1 = new TheButton ();
		// main.addComponent(butts1);

		// butts2 = new TheButtons (main);
		// butts3 = new TheButtons2 (main);

		// Button checkbox = new Button ("This is a checkbox");

		// main.addComponent(checkbox);
		final Button button = new Button("My Button");
		main.addComponent(button);
	}

	void example_CheckBox(Window main, String param) {
		/* A check box with default state (not checked, i.e., false). */
		final CheckBox checkbox1 = new CheckBox("My CheckBox");
		checkbox1.addStyleName("mybox");
		main.addComponent(checkbox1);

		/* Another check box with explicitly set checked state. */
		final CheckBox checkbox2 = new CheckBox("Checked CheckBox");
		/*
		 * @TODO: Build fails here, why? checkbox2.setValue(true);
		 */
		main.addComponent(checkbox2);

		/*
		 * Make some application logic. We use anynymous listener classes here.
		 * The above references were defined as "final" to allow accessing them
		 * from inside anonymous classes.
		 */
		checkbox1.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				/* Copy the value to the other checkbox. */
				checkbox2.setValue(checkbox1.getValue());
			}
		});
		checkbox2.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				/* Copy the value to the other checkbox. */
				checkbox1.setValue(checkbox2.getValue());
			}
		});
	}

	void example_Panel(Window main, String param) {
		// Create a panel with a caption.
		final Panel panel = new Panel("Contact Information");

		// Create a layout inside the panel
		final FormLayout form = new FormLayout();

		// Set the layout as the root layout of the panel
		panel.setLayout(form);

		// Add some components
		form.addComponent(new TextField("Name"));
		form.addComponent(new TextField("Email"));

		// Add the panel to the main window
		final ClassResource icon = new ClassResource("smiley.jpg", main
				.getApplication());
		form.addComponent(new Embedded("Image", icon));
		panel.setIcon(icon);
		panel.addComponent(form);
		main.addComponent(panel);
	}

	void example_GridLayout(Window main, String param) {
		if (param.equals("embedded")) {
			final GridLayout grid = new GridLayout(3, 3);
			for (int i = 0; i < 3 * 3; i++) {
				ClassResource img = new ClassResource("smiley.jpg", main
						.getApplication());
				Embedded embedded = new Embedded("", img);
				grid.addComponent(embedded);
			}
			main.addComponent(grid);
			return;
		}
		/* Create a 4 by 4 grid layout. */
		final GridLayout grid = new GridLayout(4, 4);
		grid.addStyleName("example-gridlayout");

		/* Fill out the first row using the cursor. */
		grid.addComponent(new Button("R/C 1"));
		for (int i = 0; i < 3; i++) {
			grid.addComponent(new Button("Col " + (grid.getCursorX() + 1)));
		}

		/* Fill out the first column using coordinates. */
		for (int i = 1; i < 4; i++) {
			grid.addComponent(new Button("Row " + i), 0, i);
		}

		/* Add some components of various shapes. */
		grid.addComponent(new Button("3x1 button"), 1, 1, 3, 1);
		grid.addComponent(new Label("1x2 cell"), 1, 2, 1, 3);
		final InlineDateField date = new InlineDateField("A 2x2 date field");
		date.setResolution(DateField.RESOLUTION_DAY);
		grid.addComponent(date, 2, 2, 3, 3);

		main.addComponent(grid);
	}

	void example_Alignment(Window main, String param) {
		if (param.equals("grid")) {
			/* Create a 3 by 3 grid layout. */
			final GridLayout layout = new GridLayout(3, 3);
			// OrderedLayout layout = new
			// OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
			main.setLayout(layout);
			layout.addStyleName("example-alignment");

			layout.setWidth(400, Sizeable.UNITS_PIXELS);
			layout.setHeight(400, Sizeable.UNITS_PIXELS);

			/* Define cells and their layouts to create. */

			Object cells[][] = {
					{ new Button("Top Left"),
							new Integer(AlignmentHandler.ALIGNMENT_LEFT),
							new Integer(AlignmentHandler.ALIGNMENT_TOP) },
					{
							new Label("Top Center"),
							new Integer(
									AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER),
							new Integer(AlignmentHandler.ALIGNMENT_TOP) },
					{ new Label("Top Right"),
							new Integer(AlignmentHandler.ALIGNMENT_RIGHT),
							new Integer(AlignmentHandler.ALIGNMENT_TOP) },
					{
							new Button("Center Left"),
							new Integer(AlignmentHandler.ALIGNMENT_LEFT),
							new Integer(
									AlignmentHandler.ALIGNMENT_VERTICAL_CENTER) },
					{
							new Button("Center Center"),
							new Integer(
									AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER),
							new Integer(
									AlignmentHandler.ALIGNMENT_VERTICAL_CENTER) },
					{
							new Button("Center Right"),
							new Integer(AlignmentHandler.ALIGNMENT_RIGHT),
							new Integer(
									AlignmentHandler.ALIGNMENT_VERTICAL_CENTER) },
					{ new Button("Bottom Left"),
							new Integer(AlignmentHandler.ALIGNMENT_LEFT),
							new Integer(AlignmentHandler.ALIGNMENT_BOTTOM) },
					{
							new Button("Bottom Center"),
							new Integer(
									AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER),
							new Integer(AlignmentHandler.ALIGNMENT_BOTTOM) },
					{ new Button("Bottom Right"),
							new Integer(AlignmentHandler.ALIGNMENT_RIGHT),
							new Integer(AlignmentHandler.ALIGNMENT_BOTTOM) } };

			for (int i = 0; i < 9; i++) {
				HorizontalLayout celllayout = new HorizontalLayout();
				celllayout.addComponent((Component) cells[i][0]);
				if (i == 0) {
					celllayout.setExpandRatio((Component) cells[i][0], 1);
				}

				celllayout.setComponentAlignment((Component) cells[i][0],
						((Integer) cells[i][1]).intValue(),
						((Integer) cells[i][2]).intValue());
				layout.addComponent(celllayout);
				// layout.setComponentAlignment((Component)cells[i][0],
				// ((Integer)cells[i][1]).intValue(),
				// ((Integer)cells[i][2]).intValue());
			}
		} else {
			final Panel panel = new Panel("A Panel with a Layout");
			main.addComponent(panel);

			// panel.addComponent(new )
		}
	}

	void example_OrderedLayout(Window main, String param) {
		final VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new TextField("Name"));
		layout.addComponent(new TextField("Street address"));
		layout.addComponent(new TextField("Postal code"));
		main.addComponent(layout);
	}

	void example_FormLayout(Window main, String param) {
		final FormLayout layout = new FormLayout();
		layout.addComponent(new TextField("Text Field"));
		layout.addComponent(new CheckBox("Check Box"));
		layout.addComponent(new Select("Select"));
		main.addComponent(layout);
	}

	void example_Form(Window main, String param) {
		if (param != null && param.equals("simple")) {
			main.addComponent(new FormExample2());
		} else if (param != null && param.equals("layout")) {
			Form form = new Form();
			form.setCaption("Form Caption");
			form
					.setDescription("This is a description of the Form that is "
							+ "displayed in the upper part of the form. You normally enter some "
							+ "descriptive text about the form and its use here.");

			// Add a field directly to the layout. This field will not be bound
			// to
			// the data source Item of the form.
			form.getLayout().addComponent(new TextField("A Field"));

			// Add a field and bind it to an named item property.
			form.addField("another", new TextField("Another Field"));

			form.setComponentError(new UserError(
					"This is the error indicator of the Form."));

			// Set the footer layout and add some text.
			form.setFooter(new VerticalLayout());
			form
					.getFooter()
					.addComponent(
							new Label(
									"This is the footer area of the Form. "
											+ "You can use any layout here. This is nice for buttons."));

			// Add an Ok (commit), Reset (discard), and Cancel buttons for the
			// form.
			HorizontalLayout okbar = new HorizontalLayout();
			okbar.setHeight("25px");
			Button okbutton = new Button("OK", form, "commit");
			okbar.addComponent(okbutton);
			okbar.setExpandRatio(okbutton, 1);
			okbar.setComponentAlignment(okbutton,
					AlignmentHandler.ALIGNMENT_RIGHT,
					AlignmentHandler.ALIGNMENT_TOP);
			okbar.addComponent(new Button("Reset", form, "discard"));
			okbar.addComponent(new Button("Cancel"));
			form.getFooter().addComponent(okbar);

			main.addComponent(form);
		} else {
			main.addComponent(new FormExample());
		}
	}

	void example_ExpandLayout(Window main, String param) {
		if (param != null && param.equals("centered")) {
			Label widget = new Label("Here is text");

			HorizontalLayout layout = new HorizontalLayout();
			layout.addComponent(widget);
			layout.setExpandRatio(widget, 1);
			layout.setComponentAlignment(widget,
					AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER,
					AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);
			layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
			layout.setHeight(100, Sizeable.UNITS_PERCENTAGE);

			main.setLayout(layout);

			return;
		} else if (param != null && param.equals("window")) {
			Window window = new Window("Progress");
			window.setHeight(100, Sizeable.UNITS_PIXELS);
			window.setWidth(200, Sizeable.UNITS_PIXELS);
			main.addWindow(window);

			ProgressIndicator progress = new ProgressIndicator(new Float(0.4));
			progress.addStyleName("fullwidth");
			progress.setPollingInterval(1000000);
			progress.setIndeterminate(false);

			HorizontalLayout layout = new HorizontalLayout();
			layout.setHeight(100, Sizeable.UNITS_PERCENTAGE);
			layout.setComponentAlignment(progress,
					HorizontalLayout.ALIGNMENT_HORIZONTAL_CENTER,
					HorizontalLayout.ALIGNMENT_VERTICAL_CENTER);
			window.setLayout(layout);
			window.addComponent(progress);

			return;
		} else if (param != null && param.equals("root")) {
			final Window mainwin = main;

			// Layout to switch to
			final VerticalLayout expand2 = new VerticalLayout();
			expand2.addComponent(new Label("I am layout too."));

			// Original layout
			final VerticalLayout expand1 = new VerticalLayout();
			Button switchButton = new Button("Switch to other layout");
			switchButton.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					mainwin.setLayout(null);
					mainwin.setLayout(expand2);
				}
			});
			expand1.addComponent(switchButton);
			main.setLayout(expand1);

			return;
		} else if (param != null && param.equals("size")) {
			VerticalLayout layout = new VerticalLayout();
			layout.setSizeFull();
			main.setLayout(layout);

			Button button = new Button("This is a button in middle of nowhere");
			layout.addComponent(button);
			layout.setComponentAlignment(button,
					VerticalLayout.ALIGNMENT_HORIZONTAL_CENTER,
					VerticalLayout.ALIGNMENT_VERTICAL_CENTER);
			layout.setExpandRatio(button, 1.0f);
			return;
		}

		for (int w = 0; w < 2; w++) {
			final VerticalLayout layout = new VerticalLayout();

			/* Set the expanding layout as the root layout of a child window. */
			final Window window = new Window("A Child Window", layout);
			main.addWindow(window);

			/* Add some component above the expanding one. */
			layout.addComponent(new Label("Here be some component."));

			/* Create the expanding component. */
			final Table table = new Table("My Ever-Expanding Table");
			/*
			 * FIXME Java 5 -> 1.4 for (int i=0; i<5; i++)
			 * table.addContainerProperty("col "+(i+1), Integer.class, 0); for
			 * (int j=0; j<20; j++) table.addItem(new Object[]{1j,2j,3j,4j,5j},
			 * j);
			 */
			layout.addComponent(table);

			/* Designate the table to be the expanding component. */
			layout.setExpandRatio(table, 1.0f);

			/* Set it to use all available area. */
			table.setSizeFull();

			/* Add some component below the expanding one. */
			final Button button2 = new Button("Ok");
			layout.addComponent(button2);
			layout.setComponentAlignment(button2,
					AlignmentHandler.ALIGNMENT_RIGHT, 0);
		}
	}

	void example_TabSheet(Window main, String param) {
		if (param.equals("icon")) {
			final TabSheet tabsheet = new TabSheet();

			tabsheet.addTab(new Label("Contents of the first tab"),
					"First Tab", new ClassResource("images/Mercury_small.png",
							main.getApplication()));
			tabsheet.addTab(new Label("Contents of the second tab"),
					"Second Tab", new ClassResource("images/Venus_small.png",
							this));
			tabsheet.addTab(new Label("Contents of the third tab"),
					"Third tab", new ClassResource("images/Earth_small.png",
							this));

			main.addComponent(tabsheet);
			// main.addComponent(new Embedded("Emb", new ClassResource
			// ("images/Mercury_small.png", this)));
		} else if (param.equals("expanding")) {
			// Create the layout
			VerticalLayout expanding = new VerticalLayout();

			// It is important to set the expanding layout as the root layout
			// of the containing window, in this case the main window, and not
			// use addComponent(), which would place the layout inside the
			// default root layout.
			main.setLayout(expanding);

			// Create a tab sheet that fills the expanding layout
			final TabSheet tabsheet = new TabSheet();
			tabsheet.addTab(new Label("Contents of the first tab"),
					"First Tab", null);
			tabsheet.addTab(new Label("Contents of the second tab"),
					"Second Tab", null);
			tabsheet.addTab(new Label("Contents of the third tab"),
					"Third tab", null);

			// Set the tabsheet to scale to full size inside its container
			tabsheet.setWidth(100, Sizeable.UNITS_PERCENTAGE);
			tabsheet.setHeight(100, Sizeable.UNITS_PERCENTAGE);

			// Add the tab sheet to the layout as usual
			expanding.addComponent(tabsheet);

			// Set the tab sheet to be the expanding component
			expanding.setExpandRatio(tabsheet, 1);
		} else if (param.equals("ordered")) {
			// Create the layout
			VerticalLayout layout = new VerticalLayout();

			// It is important to set the expanding layout as the root layout
			// of the containing window, in this case the main window, and not
			// use addComponent(), which would place the layout inside the
			// default root layout.
			main.setLayout(layout);

			// Create a tab sheet that fills the expanding layout
			final TabSheet tabsheet = new TabSheet();
			tabsheet.addTab(new Label("Contents of the first tab"),
					"First Tab", null);
			tabsheet.addTab(new Label("Contents of the second tab"),
					"Second Tab", null);
			tabsheet.addTab(new Label("Contents of the third tab"),
					"Third tab", null);

			// Set the tabsheet to scale to full size inside its container
			tabsheet.setWidth(100, Sizeable.UNITS_PERCENTAGE);
			// tabsheet().setHeight(100, Sizeable.UNITS_PERCENTAGE);

			// Add the tab sheet to the layout as usual
			layout.addComponent(tabsheet);
		} else {
			main.addComponent(new TabSheetExample());
		}
	}

	void example_Embedded(Window main, String param) {
		final Embedded image = new Embedded("", new ClassResource("smiley.jpg",
				this));
		image.addStyleName("omaimage");
		main.addComponent(image);

		final EmbeddedButton button = new EmbeddedButton(new ClassResource(
				"smiley.jpg", this));
		main.addComponent(button);
	}

	void example_Window(Window main, String param) {
		if (param != null) {
			if (param.equals("opener")) {
				main.addComponent(new WindowOpener("Window Opener", main));
			} else if (param.equals("multiple")) {
				/* Create a new window. */
				final Window mywindow = new Window("Second Window");
				mywindow.setName("mywindow");
				mywindow.addComponent(new Label("This is a second window."));

				/* Add the window to the application. */
				main.getApplication().addWindow(mywindow);

				/* Add link to the second window in the main window. */
				main.addComponent(new Label("Second window: <a href='"
						+ mywindow.getURL() + "'>middle-click to open</a>",
						Label.CONTENT_XHTML));
				main.addComponent(new Label(
						"The second window can be accessed through URL: "
								+ mywindow.getURL()));
			}
			return;
		}

		/* Create a new window. */
		final Window mywindow = new Window("My Window");
		mywindow.setName("mywindow");

		/* Add some components in the window. */
		mywindow.addComponent(new Label("A text label in the window."));
		final Button okbutton = new Button("OK");
		mywindow.addComponent(okbutton);

		/* Set window size. */
		mywindow.setHeight("200px");
		mywindow.setWidth("400px");

		/* Set window position. */
		mywindow.setPositionX(200);
		mywindow.setPositionY(50);

		/* Add the window to the Application object. */
		main.addWindow(mywindow);

	}

	void example_ClassResource(Window main, String param) {
		final DateField df = new DateField();
		main.addComponent(df);
		df.setIcon(new ClassResource("smiley.jpg", main.getApplication()));
		main.addComponent(new Embedded("This is Embedded", new ClassResource(
				"smiley.jpg", main.getApplication())));
	}

	void example_ProgressIndicator(final Window main, String param) {
		if (param != null) {
			if (param.equals("thread")) {

				// Create the indicator
				final ProgressIndicator indicator = new ProgressIndicator(
						new Float(0.0));
				main.addComponent(indicator);

				// Set polling frequency to 0.5 seconds.
				indicator.setPollingInterval(1000);

				// indicator.addStyleName("invisible");
				final Label text = new Label("-- Not running --");
				main.addComponent(text);

				// Add a button to start the progress
				final Button button = new Button("Click to start");
				main.addComponent(button);

				// Another thread to do some work
				class WorkThread extends Thread {
					@Override
					public void run() {
						double current = 0.0;
						while (true) {
							// Do some "heavy work"
							try {
								sleep(50); // Sleep for 50 milliseconds
							} catch (InterruptedException e) {
							}

							// Grow the progress value until it reaches 1.0.
							current += 0.01;
							if (current > 1.0) {
								indicator.setValue(new Float(1.0));
							} else {
								indicator.setValue(new Float(current));
							}

							// After the progress is full for a while, stop.
							if (current > 1.2) {
								// Restore the state to initial.
								indicator.setValue(new Float(0.0));
								button.setVisible(true);
								break;
							}
						}
					}
				}

				// Clicking the button creates and runs a work thread
				button.addListener(new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						final WorkThread thread = new WorkThread();
						thread.start();

						// The button hides until the work is done.
						button.setVisible(false);
					}
				});
			} else if (param.equals("window")) {
				// Create a table in the main window to hold items added in the
				// second window
				final Table table = new Table();
				table.setPageLength(5);
				table.setWidth(100, Sizeable.UNITS_PERCENTAGE);
				table.addContainerProperty("Name", String.class, "");
				main.addComponent(table);

				// Create the second window
				final Window adderWindow = new Window("Add Items");
				adderWindow.setName("win-adder");
				main.getApplication().addWindow(adderWindow);

				// Create selection component to add items to the table
				final NativeSelect select = new NativeSelect(
						"Select item to add");
				select.setImmediate(true);
				adderWindow.addComponent(select);

				// Add some items to the selection
				String items[] = new String[] { "-- Select --", "Mercury",
						"Venus", "Earth", "Mars", "Jupiter", "Saturn",
						"Uranus", "Neptune" };
				for (int i = 0; i < items.length; i++) {
					select.addItem(items[i]);
				}
				select.setNullSelectionItemId(items[0]);

				// When an item is selected in the second window, add
				// table in the main window
				select.addListener(new ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						// If the selected value is something else but null
						// selection item.
						if (select.getValue() != null) {
							// Add the selected item to the table in the main
							// window
							table.addItem(new Object[] { select.getValue() },
									new Integer(table.size()));
						}
					}
				});

				// Link to open the selection window
				Link link = new Link("Click to open second window",
						new ExternalResource(adderWindow.getURL()), "_new", 50,
						200, Link.TARGET_BORDER_DEFAULT);
				main.addComponent(link);

				// Enable polling to update the main window
				ProgressIndicator poller = new ProgressIndicator();
				poller.addStyleName("invisible");
				main.addComponent(poller);
			} else if (param.equals("centered")) {
				/*
				 * GridLayout grid = new GridLayout(3,3); main.setLayout(grid);
				 * grid().setWidth(100, Sizeable.UNITS_PERCENTAGE);
				 * 
				 * ExpandLayout layout2 = new
				 * ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
				 * layout2().setWidth(50, Sizeable.UNITS_PERCENTAGE);
				 * 
				 * ProgressIndicator poller = new ProgressIndicator(new
				 * Float(0.4)); poller.setPollingInterval(1000000);
				 * poller.setIndeterminate(false); layout2.addComponent(poller);
				 * 
				 * grid.addComponent(layout2, 1, 1);
				 */

				// ExpandLayout layout2 = new
				// ExpandLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
				/*
				 * ProgressIndicator poller = new ProgressIndicator(new
				 * Float(0.4)); poller.setPollingInterval(1000000);
				 * poller.setIndeterminate(false);
				 */
				/*
				 * layout2.addComponent(poller); layout2().setWidth(50,
				 * Sizeable.UNITS_PERCENTAGE);
				 */

				// layout.setComponentAlignment(poller,
				// AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER,
				// AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);
				/*
				 * GridLayout grid = new GridLayout(1,1);
				 * grid.addComponent(layout2, 0, 0); grid().setWidth(100,
				 * Sizeable.UNITS_PERCENTAGE);
				 */

				/*
				 * GridLayout layout = new GridLayout(1,1);
				 * //OrderedLayout.ORIENTATION_HORIZONTAL);
				 * layout.addComponent(poller); //layout.expand(poller);
				 * layout.setComponentAlignment(poller,
				 * AlignmentHandler.ALIGNMENT_HORIZONTAL_CENTER,
				 * AlignmentHandler.ALIGNMENT_VERTICAL_CENTER);
				 * layout().setWidth(100, Sizeable.UNITS_PERCENTAGE);
				 * layout().setHeight(100, Sizeable.UNITS_PERCENTAGE);
				 */

			}
		} else {
			ProgressIndicator poller = new ProgressIndicator(new Float(0.0));
			poller.setPollingInterval(1000000);
			poller.setIndeterminate(true);
			main.addComponent(poller);
		}
	}

	void example_CustomLayout(final Window main, String param) {
		Window sub = new Window("Login");
		sub.setModal(true);
		main.addWindow(sub);

		// Create the custom layout and set it as the root layout of
		// the containing window.
		final CustomLayout custom = new CustomLayout("layoutname");
		sub.setLayout(custom);

		// Create components and bind them to the location tags
		// in the custom layout.
		TextField username = new TextField();
		custom.addComponent(username, "username");

		TextField password = new TextField();
		custom.addComponent(password, "password");

		final Button ok = new Button("Login");
		custom.addComponent(ok, "okbutton");

		final Button deny = new Button("No can do!");

		Button.ClickListener listener = new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// Switch between ok and deny
				if (custom.getComponent("okbutton") == ok) {
					System.out.println("Changing to deny button.");
					custom.addComponent(deny, "okbutton");
				} else {
					System.out.println("Changing to ok button.");
					custom.addComponent(ok, "okbutton");
				}
			}
		};

		ok.addListener(listener);
		deny.addListener(listener);
	}

	void example_Spacing(final Window main, String param) {
		VerticalLayout containinglayout = new VerticalLayout();
		main.setLayout(containinglayout);

		GridLayout grid = new GridLayout(4, 3);
		grid.addStyleName("spacingexample");
		containinglayout.addComponent(grid);
		grid.addComponent(new Label(""), 0, 0);
		grid.addComponent(new Label(""), 1, 0);

		grid.addComponent(new Label("No spacing:"), 0, 1);
		HorizontalLayout layout1 = new HorizontalLayout();
		grid.addComponent(layout1, 1, 1);
		layout1.addStyleName("spacingexample");
		layout1.addComponent(new Button("Component 1"));
		layout1.addComponent(new Button("Component 2"));
		layout1.addComponent(new Button("Component 3"));

		grid.addComponent(new Label("Horizontal spacing:"), 0, 2);
		HorizontalLayout layout2 = new HorizontalLayout();
		grid.addComponent(layout2, 1, 2);
		layout2.addStyleName("spacingexample");
		layout2.setSpacing(true);
		layout2.addComponent(new Button("Component 1"));
		layout2.addComponent(new Button("Component 2"));
		layout2.addComponent(new Button("Component 3"));

		grid.addComponent(new Label("No spacing:"), 2, 0);
		VerticalLayout layout3 = new VerticalLayout();
		grid.addComponent(layout3, 2, 1, 2, 2);
		layout3.addStyleName("spacingexample");
		layout3.addComponent(new Button("Component 1"));
		layout3.addComponent(new Button("Component 2"));
		layout3.addComponent(new Button("Component 3"));

		grid.addComponent(new Label("Vertical spacing:"), 3, 0);
		VerticalLayout layout4 = new VerticalLayout();
		grid.addComponent(layout4, 3, 1, 3, 2);
		layout4.addStyleName("spacingexample");
		layout4.setSpacing(true);
		layout4.addComponent(new Button("Component 1"));
		layout4.addComponent(new Button("Component 2"));
		layout4.addComponent(new Button("Component 3"));
	}

	void example_Margin(final Window main, String param) {
		HorizontalLayout hor = new HorizontalLayout();
		main.setLayout(hor);

		VerticalLayout containinglayout = new VerticalLayout();
		hor.addComponent(containinglayout);

		VerticalLayout layout1 = new VerticalLayout();
		containinglayout.addComponent(new Label("Regular layout margins:"));
		containinglayout.addComponent(layout1);
		layout1.addStyleName("marginexample1");
		layout1.addComponent(new Button("Component 1"));
		layout1.addComponent(new Button("Component 2"));
		layout1.addComponent(new Button("Component 3"));

		// Create a layout
		VerticalLayout layout2 = new VerticalLayout();
		containinglayout.addComponent(new Label(
				"Layout with a special margin element:"));
		containinglayout.addComponent(layout2);

		// Set style name for the layout to allow styling it
		layout2.addStyleName("marginexample2");

		// Have margin on all sides around the layout
		layout2.setMargin(true);

		// Put something inside the layout
		layout2.addComponent(new Button("Component 1"));
		layout2.addComponent(new Button("Component 2"));
		layout2.addComponent(new Button("Component 3"));
	}

	void example_ClientInfo(final Window main, String param) {
		// Get the client identification string
		WebApplicationContext context2 = (WebApplicationContext) getContext();
		String browserApplication = context2.getBrowser()
				.getBrowserApplication();

		// Add a browser-dependent style name for the main window
		if (browserApplication.indexOf("Firefox/2") != -1) {
			main.addStyleName("firefox2");
		}

		// Display the client identification string
		main.addComponent(new Label(browserApplication));
	}

	void example_FillInForm(final Window main, String param) {
		if (param.equals("templates")) {
			// Create a custom layout from the fill-in-form.html template.
			CustomLayout fillinlayout = new CustomLayout("fill-in-form");

			// The style will set the display to be "inline".
			fillinlayout.addStyleName("fillinlayout");

			// Create the fields that occur in the text.
			TextField field1 = new TextField();
			TextField field2 = new TextField();
			fillinlayout.addComponent(field1, "q1");
			fillinlayout.addComponent(field2, "q2");

			main.addComponent(fillinlayout);
		} else {
			String fillintext = "The <q1> is mightier than <q2>.";
			int pos = 0;
			while (pos < fillintext.length()) {
				int nexttag = fillintext.indexOf("<", pos);
				if (nexttag == -1) {

				}
			}
		}
	}

	void example_Notification(final Window main, String param) {
		// final Window sub1 = new Window("");
		// main.addWindow(sub1);
		if (param.equals("example")) {
			main.showNotification("This is the caption",
					"This is the description");
			return;
		} else if (param.equals("type")) {
			main.showNotification("This is a warning",
					"<br/>This is the <i>last</i> warning",
					Window.Notification.TYPE_WARNING_MESSAGE);
			return;
		} else if (param.equals("pos")) {
			// Create a notification with the default settings for a warning.
			Window.Notification notif = new Window.Notification("Be warned!",
					"This message lurks in the top-left corner!",
					Window.Notification.TYPE_WARNING_MESSAGE);

			// Set the position.
			notif.setPosition(Window.Notification.POSITION_TOP_LEFT);

			// Let it stay there until the user clicks it
			notif.setDelayMsec(-1);

			// Show it in the main window.
			main.showNotification(notif);
			return;
		}

		main.setLayout(new HorizontalLayout());

		final Integer type_humanized = Window.Notification.TYPE_HUMANIZED_MESSAGE;
		final Integer type_warning = Window.Notification.TYPE_WARNING_MESSAGE;
		final Integer type_error = Window.Notification.TYPE_ERROR_MESSAGE;
		final Integer type_tray = Window.Notification.TYPE_TRAY_NOTIFICATION;
		final NativeSelect types = new NativeSelect();
		main.addComponent(types);
		types.addItem(type_humanized);
		types.addItem(type_warning);
		types.addItem(type_error);
		types.addItem(type_tray);
		types.setItemCaption(type_humanized, "Humanized");
		types.setItemCaption(type_warning, "Warning");
		types.setItemCaption(type_error, "Error");
		types.setItemCaption(type_tray, "Tray");

		Button show = new Button("Show Notification");
		main.addComponent(show);

		show.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String caption = "";
				String description = "";
				switch (((Integer) types.getValue()).intValue()) {
				case Window.Notification.TYPE_HUMANIZED_MESSAGE:
					caption = "Humanized message";
					description = "<br/>For minimal annoyance";
					break;
				case Window.Notification.TYPE_WARNING_MESSAGE:
					caption = "Warning message";
					description = "<br/>For notifications of medium importance";
					break;
				case Window.Notification.TYPE_ERROR_MESSAGE:
					caption = "Error message";
					description = "<br/>For important notifications";
					break;
				case Window.Notification.TYPE_TRAY_NOTIFICATION:
					caption = "Tray notification";
					description = "<br/>Stays up longer - but away";
				}
				// main.showNotification("The default notification");
				Window.Notification notif = new Window.Notification(caption,
						description, (Integer) types.getValue());
				// notif.setPosition(Window.Notification.POSITION_TOP_LEFT);
				notif.setDelayMsec(-1);
				main.showNotification(notif);
			}
		});

		// Notification notif = new Notification("Title");
	}

	void example_Print(final Window main, String param) {
		if (param != null && param.equals("simple")) {
			main
					.addComponent(new Label(
							"<input type='button' onClick='print()' value='Click to Print'/>",
							Label.CONTENT_XHTML));
			return;
		}

		// A button to open the printer-friendly page.
		Button printButton = new Button("Click to Print");
		main.addComponent(printButton);
		printButton.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// Create a window that contains stuff you want to print.
				Window printWindow = new Window("Window to Print");

				// Have some content to print.
				printWindow.addComponent(new Label(
						"Here's some dynamic content."));

				// To execute the print() JavaScript, we need to run it
				// from a custom layout.
				CustomLayout scriptLayout = new CustomLayout("printpage");
				printWindow.addComponent(scriptLayout);

				// Add the printing window as an application-level window.
				main.getApplication().addWindow(printWindow);

				// Open the printing window as a new browser window
				main.open(new ExternalResource(printWindow.getURL()), "_new");
			}
		});

		// main.addComponent(new
		// Label("<p>Print this!</p>\n<script type='text/javascript'>print();</script>",
		// Label.CONTENT_XHTML));
	}

	void example_RichTextArea(final Window main, String param) {
		main.setLayout(new HorizontalLayout());

		// Create a rich text area
		final RichTextArea rtarea = new RichTextArea();
		rtarea.addStyleName("richtextexample");
		// rtarea.setCaption("My Rich Text Area");

		// Set initial content as HTML
		rtarea
				.setValue("<h1>Hello</h1>\n<p>This rich text area contains some text.</p>");

		// Show the text edited in the rich text area as HTML.
		final Button show = new Button("Show HTML");
		final Label html = new Label((String) rtarea.getValue());
		show.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				html.setValue(rtarea.getValue());
			}
		});

		Panel rtPanel = new Panel("Rich Text Area");
		rtPanel.addComponent(rtarea);
		rtPanel.addComponent(show);

		Panel valuePanel = new Panel("Value");
		valuePanel.addComponent(html);

		main.addComponent(rtPanel);
		main.addComponent(valuePanel);
	}

	void example_QueryContainer(final Window main, String param) {
		try {
			// Create a database connection
			Class.forName("org.hsqldb.jdbcDriver");
			final Connection connection = DriverManager.getConnection(
					"jdbc:hsqldb:mem:qcexample", "sa", "");

			// Create an example table and put some data in it.
			Statement st = connection.createStatement();
			st
					.executeQuery("CREATE TABLE Prisoners (id INTEGER, name VARCHAR)");
			st.close();
			for (int i = 0; i < 100; i++) {
				st = connection.createStatement();
				st.executeQuery("INSERT INTO Prisoners (id, name) VALUES (" + i
						+ ",'I am number " + (i + 1) + "')");
				st.close();
			}

			// Query the database
			final QueryContainer qc = new QueryContainer(
					"SELECT id,name FROM Prisoners", connection);

			// Create a component for selecting a query result item.
			Select select = new Select("Select an item");

			// The items shown in the selection component are obtained from the
			// query.
			select.setContainerDataSource(qc);

			// The item captions are obtained from a field in the query result.
			select.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);

			// Set the name of the field from which the item captions are
			// obtained.
			select.setItemCaptionPropertyId("name");

			// When selection changes, display the selected item.
			select.setImmediate(true);
			final Label selection = new Label("Currently selected: -");
			select.addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					// Get the item id of the currently selected item
					Integer itemId = (Integer) event.getProperty().getValue();

					// Use the item ID to get the actual row from the query
					// result.
					Item qrItem = qc.getItem(itemId);

					// Display the item ID
					selection.setValue("Currently selected: result row "
							+ itemId.intValue() + " (id="
							+ qrItem.getItemProperty("id") + ", " + "name="
							+ qrItem.getItemProperty("name") + ")");
				}
			});

			main.addComponent(select);
			main.addComponent(selection);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	void example_MenuBar(final Window main, String param) {
		// Create a menu bar
		final MenuBar menubar = new MenuBar();
		main.addComponent(menubar);

		// A feedback component
		final Label selection = new Label("");
		main.addComponent(selection);

		// Define a common menu command for all the menu items.
		MenuBar.Command mycommand = new MenuBar.Command() {
			public void menuSelected(MenuItem selectedItem) {
				selection.setValue("Ordered a " + selectedItem.getText()
						+ " from menu.");
			}
		};

		// Put some items in the menu hierarchically
		MenuBar.MenuItem beverages = menubar.addItem("Beverages", null, null);
		MenuBar.MenuItem hot_beverages = beverages.addItem("Hot", null, null);
		hot_beverages.addItem("Tea", null, mycommand);
		hot_beverages.addItem("Coffee", null, mycommand);
		MenuBar.MenuItem cold_beverages = beverages.addItem("Cold", null, null);
		cold_beverages.addItem("Milk", null, mycommand);

		// Another top-level item
		MenuBar.MenuItem snacks = menubar.addItem("Snacks", null, null);
		snacks.addItem("Weisswurst", null, mycommand);
		snacks.addItem("Salami", null, mycommand);

		// Yet another top-level item
		MenuBar.MenuItem services = menubar.addItem("Services", null, null);
		services.addItem("Car Service", null, mycommand);
	}
}
