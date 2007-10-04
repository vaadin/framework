package com.itmill.toolkit.tests;

import java.util.Random;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.*;

/**
 * This example demonstrates layouts. Layouts are populated with sample Toolkit
 * UI components.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class RandomLayoutStress extends com.itmill.toolkit.Application {

	private Random seededRandom = new Random(1);

	/**
	 * Initialize Application. Demo components are added to main window.
	 */
	public void init() {
		Window mainWindow = new Window("Layout demo");
		setMainWindow(mainWindow);

		// set the application to use Corporate -theme
		setTheme("corporate");

		// Create horizontal ordered layout
		Panel panelA = new Panel("Panel containing horizontal ordered layout");
		OrderedLayout layoutA = new OrderedLayout(
				OrderedLayout.ORIENTATION_HORIZONTAL);
		// Add 4 random components
		fillLayout(layoutA, 50);
		// Add layout to panel
		panelA.addComponent(layoutA);

		// Create vertical ordered layout
		Panel panelB = new Panel("Panel containing vertical ordered layout");
		OrderedLayout layoutB = new OrderedLayout(
				OrderedLayout.ORIENTATION_VERTICAL);
		// Add 4 random components
		fillLayout(layoutB, 50);
		// Add layout to panel
		panelB.addComponent(layoutB);

		// Create grid layout
		Panel panelG = new Panel("Panel containing grid layout (20 x 20)");
		GridLayout layoutG = new GridLayout(20, 20);
		// Add 12 random components
		fillLayout(layoutG, 200);
		// Add layout to panel
		panelG.addComponent(layoutG);

		// Create TabSheet
		TabSheet tabsheet = new TabSheet();
		tabsheet
				.setCaption("Tabsheet, above layouts are added to this component");
		tabsheet.addTab(layoutA, "Horizontal ordered layout", null);
		tabsheet.addTab(layoutB, "Vertical ordered layout", null);
		tabsheet.addTab(layoutG, "Grid layout (4 x 2)", null);

		// Create custom layout
		Panel panelC = new Panel("Custom layout with style exampleStyle");
		CustomLayout layoutC = new CustomLayout("exampleStyle");
		// Add 4 random components
		fillLayout(layoutC, 50);
		// Add layout to panel
		panelC.addComponent(layoutC);

		// Add demo panels (layouts) to main window
		mainWindow.addComponent(panelA);
		mainWindow.addComponent(panelB);
		mainWindow.addComponent(panelG);
		mainWindow.addComponent(tabsheet);
		mainWindow.addComponent(panelC);
	}

	private AbstractComponent getRandomComponent(int caption) {
		AbstractComponent result = null;
		int randint = seededRandom.nextInt(7);
		switch (randint) {
		case 0:
			// Label
			result = new Label();
			result.setCaption("Label component " + caption);
			break;
		case 1:
			// Button
			result = new Button();
			result.setCaption("Button component " + caption);
			break;
		case 2:
			// TextField
			result = new TextField();
			result.setCaption("TextField component " + caption);
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
			// Link
			result = new Panel();
			result.setCaption("Panel component " + caption);
			((Panel) result)
					.addComponent(new Label(
							"Panel is a container for other components, by default it draws a frame around it's "
									+ "extremities and may have a caption to clarify the nature of the contained components' purpose."
									+ " Panel contains an layout where the actual contained components are added, "
									+ "this layout may be switched on the fly."));
			((Panel) result).setWidth(250);
			break;
		case 6:
			// Datefield
			result = new DateField();
			((DateField) result).setStyle("calendar");
			((DateField) result).setValue(new java.util.Date());
			result.setCaption("Calendar component " + caption);
			break;
		case 7:
			// Datefield
			result = new DateField();
			((DateField) result).setValue(new java.util.Date());
			result.setCaption("Calendar component " + caption);
			break;
		}

		return result;
	}

	/**
	 * Add demo components to given layout
	 * 
	 * @param layout
	 */
	private void fillLayout(Layout layout, int numberOfComponents) {
		for (int i = 0; i < numberOfComponents; i++) {
			layout.addComponent(getRandomComponent(i));
		}
	}

}
