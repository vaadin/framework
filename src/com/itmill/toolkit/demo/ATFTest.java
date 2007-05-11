package com.itmill.toolkit.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.itmill.toolkit.demo.features.FeatureUtil;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.*;

public class ATFTest extends com.itmill.toolkit.Application implements
		Button.ClickListener {

	// Seed with fixed number to ensure predeterministic AUT behaviour
	private Random rand;

	// How many "noise" components are added to AUT
	private static int COMPONENT_NUMBER = 10;

	private static int COMPONENT_MAX_GROUPED_NUMBER = 5;

	private OrderedLayout mainLayout = new OrderedLayout();

	private Layout testingLayout;

	private TextField randomSeedValue = new TextField("Seed for random");

	private Button seedShuffle = new Button("Shuffle with seed", this,
			"seedShuffle");

	private Button randomShuffle = new Button("Seed randomly and shuffle",
			this, "randomShuffle");

	private Label display = null;

	private double stored = 0.0;

	private double current = 0.0;

	private String operation = "C";

	private long captionCounter = 0;

	private ArrayList components;

	private long eventCounter = 0;

	private Label statusLabel = new Label();

	// Store button object => real value map
	// needed because button captions are randomized
	private HashMap buttonValues;

	public void init() {
		addWindow(new Window("ATFTest", create()));
		setTheme("corporate");
		setUser(new Long(System.currentTimeMillis()).toString());
	}

	/**
	 * Create application UI components and it's main layout. Does not attach
	 * layout to any specific application.
	 * 
	 * @return Layout that can be added to any application
	 */
	public Layout create() {

		statusLabel.setUIID("Label_status");

		// Setup contains restart button and deterministic component shuffler
		// Test requirement: test cases must be reproducable (use seed)
		mainLayout.addComponent(new Label(
				"<H3>ATFTest with randomized Calculator functionality</H3>"
						+ "Buttons with X captions contain calculator number, "
						+ "minus, add, multiply, divisor or clear "
						+ "button functionalities.<br />Layouts, \"noise\" "
						+ "components and component placing is randomized "
						+ "after each application restart.<br />"
						+ "Test cases should exercise calculator functions "
						+ "through X buttons and ensure that Result label "
						+ "contains correct value.", Label.CONTENT_XHTML));
		OrderedLayout setupLayout = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);
		Panel statusPanel = new Panel("Status");
		statusPanel.setWidth(200);
		setupLayout.addComponent(statusPanel);
		statusPanel.addComponent(statusLabel);
		setupLayout.addComponent(randomSeedValue);
		setupLayout.addComponent(seedShuffle);
		setupLayout.addComponent(randomShuffle);
		setupLayout.addComponent(new Button("restart", this, "close"));
		mainLayout.addComponent(setupLayout);

		randomSeedValue.setUIID("randomSeedValue");
		seedShuffle.setUIID("seedShuffle");
		randomShuffle.setUIID("randomShuffle");

		// Test requirement: layout changes or non testable component changes
		// (additions, removals) must not brake existing tests
		seedShuffle();

		return mainLayout;
	}

	// initialize random with given seed and shuffle
	// ensures deterministic application behaviour
	// helps to rerun failed tests again
	public void seedShuffle() {
		if (testingLayout != null) {
			testingLayout.removeAllComponents();
			mainLayout.removeComponent(testingLayout);
		}
		try {
			// randomize using user given value
			rand = new Random(Long.parseLong((String) randomSeedValue
					.getValue()));
		} catch (Exception e) {
			randomize();
		}
		testingLayout = new GridLayout(3, 5);
		mainLayout.addComponent(testingLayout);
		createComponents();
		addComponents(testingLayout);
		eventCounter = 0;

		statusLabel.setValue("#" + eventCounter + ": button <none>"
				+ ", value " + Double.toString(current));
	}

	// initialize random with random seed and shuffle
	// creates new application behaviour
	public void randomShuffle() {
		randomize();
		seedShuffle();
	}

	private void randomize() {
		long newSeed = System.currentTimeMillis();
		rand = new Random(newSeed);
		randomSeedValue.setValue(String.valueOf(newSeed));
	}

	private void createComponents() {

		//
		// Create components that have UUID defined
		//
		components = new ArrayList();

		// create label
		Label userLabel = new Label("user");
		userLabel.setValue(this.getUser());
		userLabel.setUIID("Label_user");
		components.add(userLabel);

		// create label
		display = new Label(Double.toString(current));
		display.setCaption("Result");
		display.setUIID("Label_result");
		components.add(display);

		// create calculator buttonsStatus:
		String[][] calcValues = {
				{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-",
						"*", "/", "=", "C" },
				{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "plus",
						"minus", "multiple", "divisor", "equals", "clear" } };
		// String[] randomizedCaptions = { "a", "b", "c", "1", "8", "3"};
		String[] randomizedCaptions = { "X" };
		buttonValues = new HashMap();
		for (int i = 0; i < calcValues[0].length; i++) {
			Button button = new Button("", this);
			// Test requirement: ATF must not rely on caption
			button.setCaption(randomizedCaptions[rand
					.nextInt(randomizedCaptions.length)]);
			// button.setCaption(calcValues[1][i]);
			// Test requirement: ATF may use UIIDs
			button.setUIID("Button_" + calcValues[1][i]);
			components.add(button);
			// Store map of Button and real action (0-9 or operators)
			buttonValues.put(button, calcValues[0][i]);
		}

		//
		// Create random "noise" components
		//
		for (int i = 0; i < COMPONENT_NUMBER; i++) {
			components.add(getRandomComponent(""));
		}
	}

	/**
	 * Get component that has UUID defined. May be used for testing AUT.
	 * 
	 * @return
	 */
	private Component getComponent() {
		if (components.size() > 0) {
			// components found, return any
			int i = rand.nextInt(components.size());
			Component c = (Component) components.get(i);
			components.remove(i);
			return c;
		} else {
			// no components left
			return null;
		}
	}

	private void addComponents(Layout layout) {
		while (components.size() > 0) {
			// Get random container
			ComponentContainer container = getRandomComponentContainer(""
					+ captionCounter++);
			layout.addComponent(container);
			// Get random amount of components for above container
			int groupsize = rand.nextInt(COMPONENT_MAX_GROUPED_NUMBER) + 1;
			for (int j = 0; j < groupsize; j++) {
				Component c = getComponent();
				if (c != null) {
					if (container instanceof TabSheet) {
						ComponentContainer tab = (ComponentContainer) ((TabSheet) container)
								.getSelectedTab();
						tab.addComponent(c);
					} else if (container instanceof GridLayout) {
						GridLayout gl = (GridLayout) container;
						if (j == 0) {
							int x = rand.nextInt(gl.getWidth());
							int y = rand.nextInt(gl.getHeight());
							gl.removeComponent(x, y);
							gl.addComponent(c, x, y);
						} else {
							gl.addComponent(c);
						}
					} else {
						container.addComponent(c);
					}
				}
			}
		}
	}

	public void buttonClick(Button.ClickEvent event) {
		String value = (String) buttonValues.get(event.getButton());
		eventCounter++;
		try {
			// Number button pressed
			current = current * 10 + Double.parseDouble(value);
			display.setValue(Double.toString(current));
			statusLabel.setValue("#" + eventCounter + ": button " + value
					+ ", value " + Double.toString(current));
			System.out.println("#" + eventCounter + ": button " + value
					+ ", value " + Double.toString(current));
		} catch (java.lang.NumberFormatException e) {
			// Operation button pressed
			if (operation.equals("+"))
				stored += current;
			if (operation.equals("-"))
				stored -= current;
			if (operation.equals("*"))
				stored *= current;
			if (operation.equals("/"))
				stored /= current;
			if (operation.equals("C"))
				stored = current;
			if (value.equals("C"))
				stored = 0.0;
			operation = value;
			current = 0.0;
			display.setValue(Double.toString(stored));
			statusLabel.setValue("#" + eventCounter + ": button " + value
					+ ", value " + Double.toString(stored));
			System.out.println("#" + eventCounter + ": button " + value
					+ ", value " + Double.toString(stored));
		}
	}

	/**
	 * Get random component container
	 * 
	 * @param caption
	 * @return
	 */
	private ComponentContainer getRandomComponentContainer(String caption) {
		ComponentContainer result = null;
		int randint = rand.nextInt(5);
		switch (randint) {
		case 0:
			result = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
			((OrderedLayout) result).setCaption("OrderedLayout_horizontal_"
					+ caption);
			break;
		case 1:
			result = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
			((OrderedLayout) result).setCaption("OrderedLayout_vertical_"
					+ caption);
			break;
		case 2:
			GridLayout gl;
			if (rand.nextInt(1) > 0)
				gl = new GridLayout();
			else
				gl = new GridLayout(rand.nextInt(3) + 1, rand.nextInt(3) + 1);
			gl.setCaption("GridLayout_" + caption);
			gl.setDescription(gl.getCaption());
			for (int x = 0; x < gl.getWidth(); x++)
				for (int y = 0; y < gl.getHeight(); y++)
					gl.addComponent(getExamplePicture("x=" + x + ", y=" + y),
							x, y);
			result = gl;
			break;
		case 3:
			result = new Panel();
			((Panel) result).setCaption("Panel_" + caption);
			break;
		case 4:
			TabSheet ts = new TabSheet();
			ts.setCaption("TabSheet_" + caption);
			// randomly select one of the tabs
			int selectedTab = rand.nextInt(3);
			ArrayList tabs = new ArrayList();
			for (int i = 0; i < 3; i++) {
				String tabCaption = "tab" + i;
				if (selectedTab == i)
					tabCaption = "tabX";
				tabs.add(new OrderedLayout());
				ts.addTab((ComponentContainer) tabs.get(tabs.size() - 1),
						tabCaption, null);
			}
			ts.setSelectedTab((ComponentContainer) tabs.get(selectedTab));
			result = (ComponentContainer) ts;
			break;
		}

		return result;
	}

	/**
	 * Get random component. Used to provide "noise" for AUT.
	 * 
	 * @param caption
	 * @return
	 */
	private AbstractComponent getRandomComponent(String caption) {
		AbstractComponent result = null;
		int randint = rand.nextInt(7); // calendar disabled
		switch (randint) {
		case 0:
			// Label
			result = new Label("Label component " + caption);
			break;
		case 1:
			// Button
			result = new Button("Button component " + caption);
			break;
		case 2:
			// TextField
			result = new TextField("TextField component " + caption);
			break;
		case 3:
			// Select
			result = new Select("Select " + caption);
			result.setCaption("Select component " + caption);
			((Select) result).addItem("First item");
			((Select) result).addItem("Second item");
			((Select) result).addItem("Third item");
			break;
		case 4:
			// Link
			result = new Link("", new ExternalResource("http://www.itmill.com"));
			result.setCaption("Link component " + caption);
			break;
		case 5:
			// Embedded
			result = getExamplePicture(caption);
			break;
		case 6:
			// Datefield
			result = new DateField();
			((DateField) result).setValue(new java.util.Date());
			((DateField) result).setResolution(DateField.RESOLUTION_DAY);
			result.setCaption("DateField component " + caption);
			break;
		case 7:
			// Calendar
			result = new DateField();
			((DateField) result).setStyle("calendar");
			((DateField) result).setValue(new java.util.Date());
			((DateField) result).setResolution(DateField.RESOLUTION_DAY);
			result.setCaption("Calendar component " + caption);
			break;
		}

		return result;
	}

	private AbstractComponent getExamplePicture(String caption) {
		ClassResource cr = new ClassResource("icon_demo.png", this);
		Embedded em = new Embedded("Embedded " + caption, cr);
		return em;
	}

	/**
	 * Add demo components to given layout. Used to provide "noise" for AUT.
	 * 
	 * @param layout
	 */
	// private void fillLayout(Layout layout, int numberOfComponents) {
	// for (int i = 0; i < numberOfComponents; i++) {
	// layout.addComponent(getRandomComponent("" + captionCounter++));
	// }
	// }
	/**
	 * ErrorEvents are printed to default error stream and not in GUI.
	 */
	public void terminalError(
			com.itmill.toolkit.terminal.Terminal.ErrorEvent event) {
		Throwable e = event.getThrowable();
		FeatureUtil.debug(getUser().toString(), "terminalError: "
				+ e.toString());
		e.printStackTrace();
	}
}
