/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo.featurebrowser;

import java.net.URL;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * @author marc
 * 
 */
public class WindowingExample extends CustomComponent {

    public static final String txt = "<p>There are two main types of windows: application-level windows, and"
            + "\"subwindows\". </p><p> A subwindow is rendered as a \"inline\" popup window"
            + " within the (native) browser window to which it was added. You can create"
            + " a subwindow by creating a new Window and adding it to a application-level window, for instance"
            + " your main window. </p><p> In contrast, you create a application-level window by"
            + " creating a new Window and adding it to the Application. Application-level"
            + " windows are not shown by default - you need to open a browser window for"
            + " the url representing the window. You can think of the application-level"
            + " windows as separate views into your application - and a way to create a"
            + " \"native\" browser window. </p><p> Depending on your needs, it's also"
            + " possible to create a new window instance (with it's own internal state)"
            + " for each new (native) browser window, or you can share the same instance"
            + " (and state) between several browser windows (the latter is most useful"
            + " for read-only views).</p>";

    private URL windowUrl = null;

    public WindowingExample() {
        final OrderedLayout main = new OrderedLayout();
        main.setMargin(true);
        setCompositionRoot(main);

        final Label l = new Label(txt);
        l.setContentMode(Label.CONTENT_XHTML);
        main.addComponent(l);

        Button b = new Button("Create a new subwindow",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        final Window w = new Window("Subwindow");
                        final Label l = new Label(txt);
                        l.setContentMode(Label.CONTENT_XHTML);
                        w.addComponent(l);
                        getApplication().getMainWindow().addWindow(w);
                    }
                });
        b.setStyleName(Button.STYLE_LINK);
        main.addComponent(b);
        b = new Button("Create a new modal window", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                final Window w = new Window("Modal window");
                w.setModal(true);
                final Label l = new Label(txt);
                l.setContentMode(Label.CONTENT_XHTML);
                w.addComponent(l);
                getApplication().getMainWindow().addWindow(w);
            }
        });
        b.setStyleName(Button.STYLE_LINK);
        main.addComponent(b);
        b = new Button("Open a application-level window, with shared state",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        if (windowUrl == null) {
                            final Window w = new Window("Subwindow");
                            final Label l = new Label(txt);
                            l.setContentMode(Label.CONTENT_XHTML);
                            w.addComponent(l);
                            getApplication().addWindow(w);
                            windowUrl = w.getURL();
                        }
                        getApplication().getMainWindow().open(
                                new ExternalResource(windowUrl), "_new");
                    }
                });
        b.setStyleName(Button.STYLE_LINK);
        main.addComponent(b);
        b = new Button(
                "Create a new application-level window, with it's own state",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        final Window w = new Window("Subwindow");
                        getApplication().addWindow(w);
                        final Label l = new Label(
                                "Each opened window has its own"
                                        + " name, and is accessed trough its own uri.");
                        l.setCaption("Window " + w.getName());
                        w.addComponent(l);
                        getApplication().getMainWindow().open(
                                new ExternalResource(w.getURL()), "_new");
                    }
                });
        b.setStyleName(Button.STYLE_LINK);
        main.addComponent(b);

    }

}
