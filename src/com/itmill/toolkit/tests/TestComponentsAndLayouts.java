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
import com.itmill.toolkit.ui.AbstractComponent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.NativeSelect;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Slider;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.TwinColSelect;
import com.itmill.toolkit.ui.Upload;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Component.Event;
import com.itmill.toolkit.ui.Component.Listener;

/**
 * Search for "TWEAK these" keyword and configure Custom/AbstractComponents to
 * various states and see how they work inside different Layouts.
 * 
 */
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
	private final Action ACTION1 = new Action("Upload");
	private final Action ACTION2 = new Action("Download");
	private final Action ACTION3 = new Action("Show history");
	private final Action[] actions = new Action[] { ACTION1, ACTION2, ACTION3 };

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

		// test layouts
		main.addComponent(new Label(
				"<hr /><h1>Components inside horizontal OrderedLayout</h3>",
				Label.CONTENT_XHTML));
		OrderedLayout ol = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
		populateLayout(ol);
		main.addComponent(ol);
		// test(ol);
		
		main.addComponent(new Label(
				"<br/><br/><br/><hr /><h1>Components inside vertical OrderedLayout</h3>",
				Label.CONTENT_XHTML));
		OrderedLayout ol2 = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
		populateLayout(ol2);
		main.addComponent(ol2);
		// test(ol);

		// test layouts
		main.addComponent(new Label(
				"<hr /><h1>Components inside ExpandLayout (height 250px)</h3>",
				Label.CONTENT_XHTML));
		ExpandLayout el = new ExpandLayout();
		el.setHeight(250);
		el.setHeightUnits(ExpandLayout.UNITS_PIXELS);
		populateLayout(el);
		main.addComponent(el);
		// test(el);

		main.addComponent(new Label("<hr /><h1>Components inside Panel</h3>",
				Label.CONTENT_XHTML));
		Panel panel = new Panel("Panel");
		populateLayout(panel);
		main.addComponent(panel);
		// test(panel);

		main
				.addComponent(new Label(
						"<hr /><h1>Components inside vertical SplitPanel (splitpanel is under 250height ExpandLayout)</h3>",
						Label.CONTENT_XHTML));
		ExpandLayout sp1l = new ExpandLayout();
		sp1l.setHeight(250);
		sp1l.setHeightUnits(ExpandLayout.UNITS_PIXELS);
		SplitPanel sp1 = new SplitPanel(SplitPanel.ORIENTATION_VERTICAL);
		sp1l.addComponent(sp1);
		OrderedLayout sp1first = new OrderedLayout();
		OrderedLayout sp1second = new OrderedLayout();
		sp1.setFirstComponent(sp1first);
		populateLayout(sp1first);
		populateLayout(sp1second);
		sp1.setSecondComponent(sp1second);
		main.addComponent(sp1l);

		main
				.addComponent(new Label(
						"<hr /><h1>Components inside horizontal SplitPanel (splitpanel is under 250px height ExpandLayout)</h3>",
						Label.CONTENT_XHTML));
		ExpandLayout sp2l = new ExpandLayout();
		sp2l.setHeight(250);
		sp2l.setHeightUnits(ExpandLayout.UNITS_PIXELS);
		SplitPanel sp2 = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
		sp2l.addComponent(sp2);
		OrderedLayout sp2first = new OrderedLayout();
		OrderedLayout sp2second = new OrderedLayout();
		sp2.setFirstComponent(sp2first);
		populateLayout(sp2first);
		populateLayout(sp2second);
		sp2.setSecondComponent(sp2second);
		main.addComponent(sp2l);

		main.addComponent(new Label(
				"<hr /><h1>Components inside TabSheet</h3>",
				Label.CONTENT_XHTML));
		TabSheet tabsheet = new TabSheet();
		OrderedLayout tab1 = new OrderedLayout();
		tab1.addComponent(new Label("try tab2"));
		OrderedLayout tab2 = new OrderedLayout();
		populateLayout(tab2);
		tabsheet
				.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif", this));
		tabsheet
				.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif", this));
		main.addComponent(tabsheet);
		// test(tabsheet);
		// test(tab1);
		// test(tab2);
		// test(expandLayout);

		main.addComponent(new Label(
				"<hr /><h1>Components inside GridLayout</h3>",
				Label.CONTENT_XHTML));
		GridLayout gridLayout = new GridLayout(4, 100);
		populateLayout(gridLayout);
		main.addComponent(gridLayout);
		// test(gridLayout);

		Window window = new Window("Components inside Window (TEST: Window)");
		populateLayout(window);
		getMainWindow().addWindow(window);
		// test(window);
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
		emb.setType(Embedded.TYPE_OBJECT);
		emb.setMimeType("application/x-shockwave-flash");
		emb.setWidth(250);
		emb.setHeight(100);
		test(layout, emb);

		Panel panel = new Panel("Panel " + count++);
		test(layout, panel);

		Label label = new Label("Label " + count++);
		test(layout, label);

		Link link = new Link("Link " + count++, new ExternalResource(
				"www.itmill.com"));
		test(layout, link);

		NativeSelect nativeSelect = new NativeSelect("NativeSelect " + count++);
		nativeSelect.setContainerDataSource(getContainer());
		test(layout, nativeSelect);

		OptionGroup optionGroup = new OptionGroup("OptionGroup " + count++);
		optionGroup.setContainerDataSource(getSmallContainer());
		optionGroup.setItemCaptionPropertyId("UNIT");
		test(layout, optionGroup);

		ProgressIndicator pi = new ProgressIndicator();
		pi.setCaption("ProgressIndicator");
		test(layout, pi);

		RichTextArea rta = new RichTextArea();
		test(layout, rta);

		Select select = new Select("Select " + count++);
		select.setContainerDataSource(getSmallContainer());
		select.setItemCaptionPropertyId("UNIT");
		test(layout, select);

		Slider slider = new Slider("Slider " + count++);
		test(layout, slider);

		Table table = new Table("Table " + count++);
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
		test(layout, table);

		TabSheet tabsheet = new TabSheet();
		OrderedLayout tab1 = new OrderedLayout();
		tab1.addComponent(new Label("tab1 " + count++));
		OrderedLayout tab2 = new OrderedLayout();
		tab2.addComponent(new Label("tab2 " + count++));
		tabsheet.addTab(tab1, "Default (not configured) TabSheet tab1",
				new ClassResource("m.gif", this));
		tabsheet.addTab(tab2, "Configured TabSheet tab2", new ClassResource(
				"m.gif", this));
		test(layout, tabsheet);

		TextField tf = new TextField("Textfield " + count++);
		test(layout, tf);
		// do not configure tab1
		// test(tab1);
		test(tab2);

		Tree tree = new Tree("Tree " + count++);
		File sampleDir = SampleDirectory.getDirectory(this);
		FilesystemContainer fsc = new FilesystemContainer(sampleDir, true);
		tree.setContainerDataSource(fsc);
		test(layout, tree);

		TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
				+ count++);
		twinColSelect.setContainerDataSource(getSmallContainer());
		twinColSelect.setItemCaptionPropertyId("UNIT");
		test(layout, twinColSelect);

		Upload upload = new Upload("Upload (non-functional)", null);
		test(layout, upload);

		// Custom components
		layout.addComponent(new Label("<B>Below are few custom components</B>",
				Label.CONTENT_XHTML));
		TestForUpload tfu = new TestForUpload();
		layout.addComponent(tfu);
		layout.addComponent(new Label("<br/><b>----------<br/></p>",
				Label.CONTENT_XHTML));
		test(tfu);

		// DISABLED
		// TableSelectTest tst = new TableSelectTest();
		// layout.addComponent(tst);
		// test(tst);
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

	// common component configuration
	void setComponentProperties(Component c) {
		// TWEAK these
		// c.setEnabled(false);
		// c.setVisible(false);
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
	 * Stresses component by configuring it
	 * 
	 * @param c
	 */
	void test(AbstractComponent c) {
		// configure common component properties
		setComponentProperties(c);

		// AbstractComponent specific configuration
		ClassResource res = new ClassResource("m.gif", this);
		ErrorMessage errorMsg = new UserError("User error " + c);
		if ((c.getCaption() == null) || (c.getCaption().length() <= 0)) {
			c.setCaption("Caption " + c);
		}

		// TWEAK these
		c.setDescription("Description " + c);
		c.setComponentError(errorMsg);
		c.setIcon(res);
		c.setImmediate(true);
		// c.addStyleName("addedTestStyleName");
		// c.setStyleName("singleTestStyleName");
	}

	void test(CustomComponent c) {
		// configure common component properties
		setComponentProperties(c);

		// CustomComponent specific configuration
		// TWEAK these
		// c.setComponentType("foo");
		c.addStyleName("addedTestStyleName");
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
		if (!(layout instanceof GridLayout)) {
			layout.addComponent(new Label("<br/><b>NEXT<br/></p>",
					Label.CONTENT_XHTML));
		}
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
