package com.itmill.toolkit.demo;

import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.MenuBar;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class HelloWorld extends com.itmill.toolkit.Application {

    /**
     * 
     */
    private static final long serialVersionUID = -2582480664174220379L;

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

        MenuBar menu = new MenuBar();
        MenuBar.MenuItem file = menu.addItem("File", null);
        file.addItem("New...", null);
        file.addItem("Open...", null);
        file.addItem("Save", null);
        file.addItem("Save as...", null);
        MenuBar.MenuItem export = file.addItem("Export", null);
        export.addItem("As image...", null);
        export.addItem("As movie...", null);
        menu.addItem("Edit", null);
        menu.addItem("View", null);

        main.addComponent(menu);

        ProgressIndicator p = new ProgressIndicator(0.6f);
        main.addComponent(p);
        p.setPollingInterval(1200000);

        ((VerticalLayout) main.getLayout()).setSpacing(true);

        Window w = new Window("Hidden window");
        w.setStyleName("hidden");
        w.addComponent(new Label("Test label"));
        w.setModal(true);

        main.addWindow(w);

    }
}
