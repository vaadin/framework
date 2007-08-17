package com.itmill.toolkit.demo;

import java.util.Date;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CalendarField;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

/**
 * The classic "hello, world!" example for IT Mill Toolkit. The class simply
 * implements the abstract {@link com.itmill.toolkit.Application#init() init()}
 * method in which it creates a Window and adds a Label to it.
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class HelloWorld extends com.itmill.toolkit.Application {

	/**
	 * The initialization method that is the only requirement for inheriting the
	 * com.itmill.toolkit.service.Application class. It will be automatically
	 * called by the framework when a user accesses the application.
	 */
	OrderedLayout l;
	public void init() {

		setTheme("demo");
		/*
		 * - Create new window for the application - Give the window a visible
		 * title - Set the window to be the main window of the application
		 */
		Window main = new Window("Hello window");
		setMainWindow(main);

		main.addComponent(new DateField());
		main.addComponent(new CalendarField());
		
		/*
		 * - Create a label with the classic text - Add the label to the main
		 * window
		 */
		main.addComponent(new Label("Hello4 World!"));
		
		l = new OrderedLayout();
		main.addComponent(l);
		l.addComponent(new Button("foo",this,"foo"));
		l.addComponent(new Button("asd",this,"asd"));

		/*
		 * And that's it! The framework will display the main window and its
		 * contents when the application is accessed with the terminal.
		 */
	}
	
	public void foo() {
	    long s = new Date().getTime();
	    System.out.println("> foo: " + s);
	    try {
		Thread.currentThread().sleep(5000);
	    } catch (Exception e) {
		
	    }
	    System.out.println("< foo: " + s);	
	}
	public void asd() {
	    long s = new Date().getTime();
	    System.out.println("> asd: " + s);
	    try {
		Thread.currentThread().sleep(5000);
	    } catch (Exception e) {
		
	    }
	    System.out.println("< asd: " + s);	
	}
}
