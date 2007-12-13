/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import java.util.HashMap;
import java.util.Iterator;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * Embeds other demos in windows using an ExternalResource ("application in
 * application").
 * 
 * @author IT Mill Ltd.
 * @see com.itmill.toolkit.ui.Window
 */
public class WindowedDemos extends com.itmill.toolkit.Application {

    // keeps track of created windows
    private final HashMap windows = new HashMap();

    // mapping demo name to URL
    private static final HashMap servlets = new HashMap();
    static {
        servlets.put("Caching demo", "CachingDemo/");
        servlets.put("Calculator", "Calc/");
        servlets.put("Calendar demo", "CalendarDemo/");
        servlets.put("Select demo", "SelectDemo/");
        servlets.put("Table demo", "TableDemo/");
        servlets.put("Browser demo", "BrowserDemo/");
        servlets.put("Notification demo", "NotificationDemo/");
    }

    public void init() {

        // Create new window for the application and give the window a visible.
        final Window main = new Window("IT Mill Toolkit 5 Windowed Demos");
        // set as main window
        setMainWindow(main);

        // Create menu window.
        final Window menu = new Window("Select demo");
        menu.getSize().setWidth(200);
        menu.getSize().setHeight(400);
        main.addWindow(menu); // add to layout

        // Create a menu button for each demo
        for (final Iterator it = servlets.keySet().iterator(); it.hasNext();) {
            final String name = (String) it.next();
            final Button b = new Button(name, new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    show(event.getButton().getCaption());
                }

            });
            b.setStyleName("link");
            menu.addComponent(b);
        }

    }

    /**
     * Shows the specified demo in a separate window. Creates a new window if
     * the demo has not been shown already, re-uses old window otherwise.
     * 
     * @param demoName
     *                the name of the demo to be shown
     */
    private void show(String demoName) {
        Window w = (Window) windows.get(demoName);
        if (w == null) {
            w = new Window(demoName);
            w.getSize().setWidth(520);
            w.getSize().setHeight(500);
            w.setPositionX(202);
            windows.put(demoName, w);
            getMainWindow().addWindow(w);
        } else {
            w.setVisible(true);
        }
        w.open(new ExternalResource((String) servlets.get(demoName)));

    }

}
