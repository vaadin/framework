package com.itmill.toolkit.demo;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class HelloWorld extends com.itmill.toolkit.Application {

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