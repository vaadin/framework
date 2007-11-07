package com.itmill.toolkit.tests;

import java.io.File;
import java.sql.SQLException;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.util.FilesystemContainer;
import com.itmill.toolkit.data.util.QueryContainer;
import com.itmill.toolkit.demo.util.SampleDatabase;
import com.itmill.toolkit.demo.util.SampleDirectory;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.UserError;

import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Component.Event;
import com.itmill.toolkit.ui.Component.Listener;

public class TestComponentsAndLayouts extends Application implements Listener,
		Action.Handler {

	OrderedLayout main = new OrderedLayout();

	// event listener feedback (see console)
	Label eventListenerFeedback = new Label(
			"See console for event listener log.");
	int eventCount = 0;

	// component counter
	int count = 0;

	SampleDatabase sampleDatabase;

	// Example Actions for table
	private Action ACTION1 = new Action("Upload");
	private Action ACTION2 = new Action("Download");
	private Action ACTION3 = new Action("Show history");
	private Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

	public void init() {
		sampleDatabase = new SampleDatabase();
		createNewView();
	}

	public void createNewView() {
		Window main = new Window("Main window");
		setMainWindow(main);

		main
				.addComponent(new Label(
						"Each Layout and their contained components should "
								+ "have icon, caption, description, user error defined. "
								+ "Eeach layout should contain similar components. "
								+ "All components are in immmediate mode."));
		main.addComponent(eventListenerFeedback);

		main.addComponent(new Label("<hr /><h1>OrderedLayout</h3>",
				Label.CONTENT_XHTML));
		main.addComponent(new Label("OrderedLayout"));
		test(main);
		populateLayout(main);

		main
				.addComponent(new Label("<hr /><h1>Panel</h3>",
						Label.CONTENT_XHTML));
		Panel panel = new Panel("Panel");
		test(panel);
		populateLayout(panel);
		main.addComponent(panel);

		main.addComponent(new Label("<hr /><h1>TabSheet</h3>",
				Label.CONTENT_XHTML));
		TabSheet tabsheet = new TabSheet();
		test(tabsheet);
		OrderedLayout tab1 = new OrderedLayout();
		tab1.addComponent(new Label("try tab2"));
		OrderedLayout tab2 = new OrderedLayout();
		test(tab2);
		populateLayout(tab2);
		tabsheet
				.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif", this));
		tabsheet
				.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif", this));
		main.addComponent(tabsheet);

		main.addComponent(new Label("<hr /><h1>ExpandLayout</h3>",
				Label.CONTENT_XHTML));
		ExpandLayout expandLayout = new ExpandLayout();
		test(expandLayout);
		populateLayout(expandLayout);
		main.addComponent(expandLayout);

		main.addComponent(new Label("<hr /><h1>GridLayout</h3>",
				Label.CONTENT_XHTML));
		GridLayout gridLayout = new GridLayout(4, 100);
		test(gridLayout);
		populateLayout(gridLayout);
		main.addComponent(gridLayout);

		Window window = new Window("TEST: Window");
		test(window);
		populateLayout(window);
		getMainWindow().addWindow(window);
	}

	void populateLayout(Layout layout) {
		Button button = new Button("Button " + count++);
		test(layout, button);

		DateField df = new DateField("DateField " + count++);
		test(layout, df);

		CheckBox cb = new CheckBox("Checkbox " + count++);
		test(layout, cb);

		ClassResource flashResource = new ClassResource("itmill_spin.swf", this);
		Embedded emb = new Embedded("Embedded " + count++, flashResource);
		test(layout, emb);
		emb.setType(Embedded.TYPE_OBJECT);
		emb.setMimeType("application/x-shockwave-flash");
		emb.setWidth(250);
		emb.setHeight(100);

		Panel panel = new Panel("Panel " + count++);
		test(layout, panel);

		Label label = new Label("Label " + count++);
		test(layout, label);

		Link link = new Link("Link " + count++, new ExternalResource(
				"www.itmill.com"));
		test(layout, link);

		NativeSelect nativeSelect = new NativeSelect("NativeSelect " + count++);
		test(layout, nativeSelect);
		nativeSelect.setContainerDataSource(getContainer());

		OptionGroup optionGroup = new OptionGroup("OptionGroup " + count++);
		test(layout, optionGroup);
		optionGroup.setContainerDataSource(getSmallContainer());
		optionGroup.setItemCaptionPropertyId("UNIT");

		ProgressIndicator pi = new ProgressIndicator(50.0f);
		pi.setCaption("ProgressIndicator");
		test(layout, pi);

		RichTextArea rta = new RichTextArea();
		test(layout, rta);

		Select select = new Select("Select " + count++);
		test(layout, select);
		select.setContainerDataSource(getSmallContainer());
		select.setItemCaptionPropertyId("UNIT");

		Slider slider = new Slider("Slider " + count++);
		test(layout, slider);

		Table table = new Table("Table " + count++);
		test(layout, table);
		table.setPageLength(10);
		table.setSelectable(true);
		table.setRowHeaderMode(Table.ROW_HEADER_MODE_INDEX);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.addActionHandler(this);
		table.setContainerDataSource(getContainer());
		table.setVisibleColumns(new Object[] { "FIRSTNAME", "LASTNAME",
				"TITLE", "UNIT" });
		table.setItemCaptionPropertyId("ID");

		TabSheet tabsheet = new TabSheet();
		OrderedLayout tab1 = new OrderedLayout();
		tab1.addComponent(new Label("tab1 " + count++));
		OrderedLayout tab2 = new OrderedLayout();
		tab2.addComponent(new Label("tab2"));
		tabsheet
				.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif", this));
		tabsheet
				.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif", this));

		TextField tf = new TextField("Textfield " + count++);
		test(layout, tf);

		Tree tree = new Tree("Tree " + count++);
		test(layout, tree);
		File sampleDir = SampleDirectory.getDirectory(this);
		FilesystemContainer fsc = new FilesystemContainer(sampleDir, true);
		tree.setContainerDataSource(fsc);

		TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
				+ count++);
		test(layout, twinColSelect);
		twinColSelect.setContainerDataSource(getSmallContainer());
		twinColSelect.setItemCaptionPropertyId("UNIT");

		Upload upload = new Upload("Upload (non-functional)", null);
		test(layout, upload);

		// Custom components
		layout.addComponent(new Label("<B>Below are few custom components</B>",
				Label.CONTENT_XHTML));
		TestForUpload tfu = new TestForUpload();
		layout.addComponent(tfu);
		layout.addComponent(new Label("<HR />", Label.CONTENT_XHTML));

		// DISABLED
		// TableSelectTest tst = new TableSelectTest();
		// layout.addComponent(tst);
		// layout.addComponent(new Label("<HR />", Label.CONTENT_XHTML));

	}

	Container getContainer() {
		// populate container with test SQL table rows
		try {
			return new QueryContainer("SELECT * FROM employee", sampleDatabase
					.getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	Container getSmallContainer() {
		// populate container with test SQL table rows
		try {
			return new QueryContainer(
					"SELECT DISTINCT UNIT AS UNIT FROM employee",
					sampleDatabase.getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Stresses component by configuring it
	 * 
	 * @param c
	 */
	void test(AbstractComponent c) {
		ClassResource res = new ClassResource("m.gif", this);
		ErrorMessage errorMsg = new UserError("User error " + c);

		if ((c.getCaption() == null) || (c.getCaption().length() <= 0))
			c.setCaption("Caption " + c);
		c.setDescription("Description " + c);
		c.setComponentError(errorMsg);
		c.setIcon(res);
		c.setImmediate(true);
		// c.setEnabled(false);
		// c.setVisible(false);
		// c.setStyle("testStyle");
		// c.setStyleName("testStyleName");
		// c.setReadOnly(true);

		// try to add listener
		try {
			c.addListener(this);
		} catch (Exception e) {
			System.err.println("Could not add listener for component " + c
					+ ", count was " + count);
		}
	}

	/**
	 * Stresses component by configuring it in a given layout
	 * 
	 * @param c
	 */
	void test(Layout layout, AbstractComponent c) {
		test(c);
		layout.addComponent(c);
		// add separator
		if (!(layout instanceof GridLayout))
			layout.addComponent(new Label("<HR />", Label.CONTENT_XHTML));
	}

	public void componentEvent(Event event) {
		eventCount++;
		String feedback = "eventCount=" + eventCount + ", class="
				+ event.getClass() + ", source=" + event.getSource()
				+ ", toString()=" + event.toString();
		System.out.println("eventListenerFeedback: " + feedback);
		eventListenerFeedback.setValue("Events: " + eventCount);
	}

	// For sample actions
	public Action[] getActions(Object target, Object sender) {
		return actions;
	}

	// For sample actions
	public void handleAction(Action action, Object sender, Object target) {
		System.out.println("ACTION: " + action.getCaption() + " on item "
				+ target);
	}

}
