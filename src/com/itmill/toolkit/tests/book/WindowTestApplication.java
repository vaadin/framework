package com.itmill.toolkit.tests.book;

import java.util.HashMap;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;
import com.itmill.toolkit.ui.Window.CloseListener;

public class WindowTestApplication extends Application {
    Window anotherpage = null;

    // Storage for extra window objects - there could be many.
    HashMap windows = new HashMap();

    /* (non-Javadoc)
     * @see com.itmill.toolkit.Application#init()
     */
    @Override
    public void init() {
        final Window main = new Window("Window Test Application");
        setMainWindow(main);
        setTheme("tests-book");

        /* Create a new window. */
        final Window mywindow = new Window("Second Window");

        /* Manually set the name of the window. */
        mywindow.setName("mywindow");

        /* Add some content to the window. */
        mywindow.addComponent(new Label("This is a second window."));

        /* Add the window to the application. */
        addWindow(mywindow);

//        /* Add a button to open a new window. */
//        main.addComponent(new Button("Click to open new window",
//                new Button.ClickListener() {
//                    public void buttonClick(ClickEvent event) {
//                        // Open the window.
//                        main.open(new ExternalResource(mywindow.getURL()),
//                                "_new");
//                    }
//                }));
//
//        /* Add a link to the second window. */
//        Link link = new Link("Click to open second window",
//                new ExternalResource(mywindow.getURL()));
//        link.setTargetName("_new");
//        main.addComponent(link);
//
//        // Add the link manually inside a Label.
//        main.addComponent(new Label("Second window: <a href='"
//                + mywindow.getURL() + "' target='_new'>click to open</a>",
//                Label.CONTENT_XHTML));
//        main.addComponent(new Label(
//                "The second window can be accessed through URL: "
//                        + mywindow.getURL()));

        // Add links to windows that do not yet exist, but are created
        // dynamically when the URL is called.
        
        main.addComponent(new Label("Click a link to open a new window:"));
        
        // Have some IDs for the separate windows.
        final String[] items = new String[] { "mercury", "venus", "earth",
                "mars", "jupiter", "saturn", "uranus", "neptune" };

        // Create a list of links to each of the available window.
        for (int i = 0; i < items.length; i++) {
            // Create a URL for the window.
            String windowUrl = getURL() + "planet-" + items[i];
            
            // Create a link to the window URL.
            // Using the window ID for the target also opens it in a new
            // browser window (or tab).
            main.addComponent(new Link("Open window about " + items[i],
                                       new ExternalResource(windowUrl),
                                       items[i], -1, -1, Window.BORDER_DEFAULT));
        }
    }

    @Override
    /**
     * This method is called for every client request for this application.
     * It needs to return the correct window for the given identifier.
     **/
    public Window getWindow(String name) {
        // If a dynamically created window is requested, but it does
        // not exist yet, create it.
        if (name.startsWith("planet-") &&
              super.getWindow(name) == null) {
            System.out.println("New window "+name);

            String planetName = name.substring("planet-".length());

            // Create the window object.
            Window newWindow = new Window("Window about " + planetName);
            
            // We must set this explicitly or otherwise an automatically
            // generated name is used.
            newWindow.setName(name);

            // Put some content in it.
            newWindow.addComponent(new Label("This window contains details about " + planetName + "."));
            
            // Add it to the application as a regular application-level window.
            addWindow(newWindow);
            
            newWindow.addListener(new Window.CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    System.out.println(e.getWindow().getName() + " was closed");
                    getMainWindow().addComponent(
                        new Label("Window '" + e.getWindow().getName() +
                                  "' was closed."));
                }
            });

            return newWindow;
        }

        // Otherwise the Application object manages existing windows by their name.
        return super.getWindow(name);
    }   
}
