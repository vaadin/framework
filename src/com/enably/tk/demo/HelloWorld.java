package com.enably.tk.demo;

import com.enably.tk.ui.*;

/** The classic "hello, world!" example for the MillStone framework. The
 * class simply implements the abstract 
 * {@link com.enably.tk.Application#init() init()} method
 * in which it creates a Window and adds a Label to it.
 *
 * @author IT Mill Ltd.
 * @see com.enably.tk.Application
 * @see com.enably.tk.ui.Window
 * @see com.enably.tk.ui.Label
 */
public class HelloWorld extends com.enably.tk.Application {

	/** The initialization method that is the only requirement for
	 * inheriting the com.enably.tk.service.Application class. It will
	 * be automatically called by the framework when a user accesses the
	 * application.
	 */
    public void init() {
     
        /*
         * - Create new window for the application
         * - Give the window a visible title
         * - Set the window to be the main window of the application
         */
        Window main = new Window("Hello window");
        setMainWindow(main);
        
        /*
         * - Create a label with the classic text
         * - Add the label to the main window
         */
        main.addComponent(new Label("Hello World!"));
        
   		/*
		 * And that's it! The framework will display the main window and its
		 * contents when the application is accessed with the terminal.
		 */
    }            
}
