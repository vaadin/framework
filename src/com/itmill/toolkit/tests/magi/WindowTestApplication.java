package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.terminal.*;

public class WindowTestApplication extends Application {
    Window anotherpage = null;

    public void init() {
        final Window main = new Window ("Window Test Application");
        setMainWindow(main);
        setTheme("tests-magi");
        
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
                main.open(new ExternalResource(mywindow.getURL()), "_new");
            }      
        }));        
        
        /* Add a link to the second window. */
        Link link = new Link("Click to open second window",
                             new ExternalResource(mywindow.getURL()));
        link.setTargetName("_new");
        //link.setTargetHeight(300);
        //link.setTargetWidth(300);
        //link.setTargetBorder(Link.TARGET_BORDER_DEFAULT);
        main.addComponent(link);
        
        /* Add the link manually inside a Label. */
        main.addComponent(new Label("Second window: <a href='"
                                    + mywindow.getURL() + "' target='_new'>click to open</a>",
                          Label.CONTENT_XHTML));
        main.addComponent(new Label("The second window can be accessed through URL: "
                                    + mywindow.getURL()));

        /* Add link to the yet another window that does not yet exist. */
        main.addComponent(new Label("Yet another window: <a href='"
                                    + getURL() + "anotherpage/' target='_new'>click to open</a>",
                          Label.CONTENT_XHTML));
        main.addComponent(new Label("The yet another window can be accessed through URL: "
                                    + getURL()+"anotherpage/"));
    }
    
    public Window getWindow(String name) {
        if (name.equals("anotherpage")) {
            if (anotherpage == null) {
                anotherpage = new Window("Yet Another Page");
                anotherpage.addComponent(new Label("This is a yet another window."));
            }
            return anotherpage;
        }
        return super.getWindow(name);
    }
    
}
