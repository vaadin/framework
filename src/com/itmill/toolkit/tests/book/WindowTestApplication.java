package com.itmill.toolkit.tests.book;

import java.util.HashMap;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class WindowTestApplication extends Application {
    Window anotherpage = null;
    HashMap windows = new HashMap();

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

        /* Add a button to open a new window. */
        main.addComponent(new Button("Click to open new window",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // Open the window.
                        main.open(new ExternalResource(mywindow.getURL()),
                                "_new");
                    }
                }));

        /* Add a link to the second window. */
        Link link = new Link("Click to open second window",
                new ExternalResource(mywindow.getURL()));
        link.setTargetName("_new");
        main.addComponent(link);

        // Add the link manually inside a Label.
        main.addComponent(new Label("Second window: <a href='"
                + mywindow.getURL() + "' target='_new'>click to open</a>",
                Label.CONTENT_XHTML));
        main.addComponent(new Label(
                "The second window can be accessed through URL: "
                        + mywindow.getURL()));

        // Add links to windows that do not yet exist, but are created
        // dynamically when the URL is called.
        main.addComponent(new Label("URLs to open item windows:"));
        final String[] items = new String[] { "mercury", "venus", "earth",
                "mars", "jupiter", "saturn", "uranus", "neptune" };

        for (int inx = 0; inx < items.length; inx++) {
            String item = items[inx];

            // We do not create window objects here, but just links to the
            // windows
            String windowUrl = getURL() + "planet-" + item;
            main.addComponent(new Label("A window about '" + item
                    + "': <a href='" + windowUrl + "' target='_new'>"
                    + windowUrl + "</a>", Label.CONTENT_XHTML));
        }
    }

    @Override
    public Window getWindow(String name) {
        if (name.startsWith("planet-")) {
            String planetName = name.substring("planet-".length());
            if (!windows.containsKey(planetName)) {
                // Create the window object on the fly.
                Window newWindow = new Window("Yet Another Page");
                newWindow.addComponent(new Label(
                        "This window contains details about " + planetName
                                + "."));
                windows.put(planetName, newWindow);

                // We must add the window to the application, it is not done
                // automatically
                addWindow(newWindow);
            }
            return (Window) windows.get(planetName);
        }

        return super.getWindow(name);
    }
}
