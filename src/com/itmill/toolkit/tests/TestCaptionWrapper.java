package com.itmill.toolkit.tests;

import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ErrorMessage;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.UserError;

import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Component.Listener;

public class TestCaptionWrapper extends CustomComponent implements Listener {

	OrderedLayout main = new OrderedLayout();

	final String eventListenerString = "Component.Listener feedback: ";
	Label eventListenerFeedback = new Label(eventListenerString
			+ " <no events occured>");
	int count = 0;

	public TestCaptionWrapper() {
		setCompositionRoot(main);
	}

	public void attach() {
		super.attach();
		createNewView();
	}

	public void createNewView() {
		main.removeAllComponents();

		main
				.addComponent(new Label(
						"Each Layout and their contained components should "
								+ "have icon, caption, description, user error defined. "
								+ "Eeach layout should contain similar components."));

		main.addComponent(eventListenerFeedback);

		main.addComponent(new Label("OrderedLayout"));
		test(main);
		populateLayout(main);

		Panel panel = new Panel("Panel");
		test(panel);
		populateLayout(panel);

		TabSheet tabsheet = new TabSheet();
		test(tabsheet);
		OrderedLayout tab1 = new OrderedLayout();
		tab1.addComponent(new Label("try tab2"));
		OrderedLayout tab2 = new OrderedLayout();
		test(tab2);
		populateLayout(tab2);
		tabsheet.addTab(tab1, "TabSheet tab1", new ClassResource("m.gif", this
				.getApplication()));
		tabsheet.addTab(tab2, "TabSheet tab2", new ClassResource("m.gif", this
				.getApplication()));

		ExpandLayout expandLayout = new ExpandLayout();
		test(expandLayout);
		populateLayout(expandLayout);

		GridLayout gridLayout = new GridLayout();
		test(gridLayout);
		populateLayout(gridLayout);

		Window window = new Window("TEST: Window");
		test(window);
		populateLayout(window);

	}

	void populateLayout(Layout layout) {

		Button button = new Button("Button " + count++);
		test(layout, button);
		button.addListener(this);

		DateField df = new DateField("DateField " + count++);
		test(layout, df);

		CheckBox cb = new CheckBox("Checkbox " + count++);
		test(layout, cb);

		Embedded emb = new Embedded("Embedded " + count++);
		test(layout, emb);

		Panel panel = new Panel("Panel " + count++);
		test(layout, panel);

		Label label = new Label("Label " + count++);
		test(layout, label);

		Link link = new Link("Link " + count++, new ExternalResource(
				"www.itmill.com"));
		test(layout, link);

		NativeSelect nativeSelect = new NativeSelect("NativeSelect " + count++);
		test(layout, nativeSelect);

		OptionGroup optionGroup = new OptionGroup("OptionGroup " + count++);
		test(layout, optionGroup);

		ProgressIndicator pi = new ProgressIndicator();
		test(layout, pi);

		RichTextArea rta = new RichTextArea();
		test(layout, rta);

		Select select = new Select("Select " + count++);
		test(layout, select);

		Slider slider = new Slider("Slider " + count++);
		test(layout, slider);

		Table table = new Table("Table " + count++);
		test(layout, table);

		TextField tf = new TextField("Textfield " + count++);
		test(layout, tf);

		Tree tree = new Tree("Tree " + count++);
		test(layout, tree);

		TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect "
				+ count++);
		test(layout, twinColSelect);

		Upload upload = new Upload("Upload (non-functional)", null);
		test(layout, upload);

		// Custom components
		layout.addComponent(new Label("<B>Below are few custom components</B>",
				Label.CONTENT_XHTML));
		TestForUpload tfu = new TestForUpload();
		layout.addComponent(tfu);

	}

	/**
	 * Stresses component by configuring it
	 * 
	 * @param c
	 */
	void test(AbstractComponent c) {
		ClassResource res = new ClassResource("m.gif", this.getApplication());
		ErrorMessage errorMsg = new UserError("User error " + c);

		if ((c.getCaption() == null) || (c.getCaption().length() <= 0))
			c.setCaption("Caption " + c);
		c.setDescription("Description " + c);
		c.setComponentError(errorMsg);
		c.setIcon(res);
	}

	/**
	 * Stresses component by configuring it in a given layout
	 * 
	 * @param c
	 */
	void test(Layout layout, AbstractComponent c) {
		test(c);
		layout.addComponent(c);
	}

	public void componentEvent(Event event) {
		String feedback = eventListenerString + " source=" + event.getSource()
				+ ", toString()=" + event.toString();
		System.out.println("eventListenerFeedback: " + feedback);
		eventListenerFeedback.setValue(feedback);
	}

}
