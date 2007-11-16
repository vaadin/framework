package com.itmill.toolkit.tests.magi;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;

/** This example demonstrates the use of inline classes for event listeners. */
public class TheButtons3 {
	Button thebutton; /* This component is stored as a member variable. */ 
	
	/** Creates two buttons in given container. */
	public TheButtons3(AbstractComponentContainer container) {
		thebutton = new Button ("Do not push this button");
		thebutton.addListener(new Button.ClickListener() {
			/* Define the method in the local class to handle the click. */
			public void buttonClick(ClickEvent event) {
				thebutton.setCaption ("Do not push this button again");
			}
		});
		container.addComponent(thebutton);
	
		/* Have the second button as a local variable in the constructor. 
		 * Only "final" local variables can be accessed from an anonymous class. */
		final Button secondbutton = new Button ("I am a button too");
		secondbutton.addListener(new Button.ClickListener() {
			/* Define the method in the local class to handle the click. */
			public void buttonClick(ClickEvent event) {
				secondbutton.setCaption ("I am not a number!");			
			}
		});
		container.addComponent (secondbutton);
	}
}
