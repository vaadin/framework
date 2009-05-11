package com.vaadin.demo;

import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class HelloWorld extends com.vaadin.Application {

    /**
     * Init is invoked on application load (when a user accesses the application
     * for the first time).
     */
    @Override
    public void init() {

        // Main window is the primary browser window
        final Window main = new Window("Hello window");
        setMainWindow(main);

        // "Hello world" text is added to window as a Label component
        main.addComponent(new Label("Hello World!"));
    }
}
