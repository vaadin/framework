package com.enably.tk.demo;

import com.enably.tk.ui.*;

/** <p>An example application implementing a simple web-based calculator.
 * using the MillStone UI library. The application opens up a window and
 * places the needed UI components (display label, buttons etc.) on it, and
 * registers a button click listener for them.</p>
 * 
 * <p>When any of the buttons are pressed the application finds out which
 * button was pressed, figures what that button does, and updates the user
 * interface accordingly.</p>
 *
 * @see com.enably.tk.Application
 * @see com.enably.tk.ui.Button.ClickListener
 */
public class Calc
	extends com.enably.tk.Application
	implements Button.ClickListener {

	/** The label used as the display */
	private Label display = null;

	/** Last completed result */
	private double stored = 0.0;

	/** The number being currently edited. */
	private double current = 0.0;

	/** Last activated operation. */
	private String operation = "C";

	/** Button captions. */
		private static String[] captions = // Captions for the buttons
	{"7","8","9","/","4","5","6","*","1","2","3","-","0","=","C","+" };

	/** <p>Initializes the application. This is the only method a MillStone
	 * application is required to implement. It's called by the framework
	 * and it should perform whatever initialization tasks the application
	 * needs to perform.</p>
	 * 
	 * <p>In this case we create the main window, the display, the grid to
	 * hold the buttons, and the buttons themselves.</p>
	 */
	public void init() {

		/*
		 * Create a new {@link com.enably.tk.ui.GridLayout GridLayout}
		 * to hold the UI components needed by the calculator.
		 */
		GridLayout layout = new GridLayout(4, 5);

		//Create a new label component for displaying the result
		display = new Label(Double.toString(current));
		display.setCaption("Result");

		// Place the label to the top of the previously created grid.
		layout.addComponent(display, 0, 0, 3, 0);

		// Create the buttons and place them in the grid
		for (int i = 0; i < captions.length; i++) {
			layout.addComponent(new Button(captions[i], this));
		}

		/*
		 * Create the main window with a caption and add it to the
		 * application.
		 */
		addWindow(new Window("MillStone calculator", layout));

	}

	/** <p>The button listener method called any time a button is pressed.
	 * This method catches all button presses, figures out what the user
	 * wanted the application to do, and updates the UI accordingly.</p>
	 * 
	 * <p>The button click event passed to this method contains information
	 * about which button was pressed. If it was a number, the currently
	 * edited number is updated. If it was something else, the requested
	 * operation is performed. In either case the display label is updated
	 * to include the outcome of the button click.</p>
	 * 
	 * @param event the button click event specifying which button was
	 * pressed
	 */
	public void buttonClick(Button.ClickEvent event) {

		try {
			// Number button pressed
			current =
				current * 10
					+ Double.parseDouble(event.getButton().getCaption());
			display.setValue(Double.toString(current));
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
			if (event.getButton().getCaption().equals("C"))
				stored = 0.0;
			operation = event.getButton().getCaption();
			current = 0.0;
			display.setValue(Double.toString(stored));
		}
	}
}
