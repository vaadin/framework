package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket2240 extends LegacyApplication {

    public static final String txt = "<p>There are two main types of windows: application-level windows, and "
            + "\"sub windows\".</p><p>A sub window is rendered as a \"inline\" popup window"
            + " within the (native) browser window to which it was added. You can create"
            + " a sub window by creating a new Window and adding it to a application-level window, for instance"
            + " your main window. </p><p> In contrast, you create a application-level window by"
            + " creating a new Window and adding it to the Application. Application-level"
            + " windows are not shown by default - you need to open a browser window for"
            + " the url representing the window. You can think of the application-level"
            + " windows as separate views into your application - and a way to create a"
            + " \"native\" browser window.</p><p>Depending on your needs, it's also"
            + " possible to create a new window instance (with it's own internal state)"
            + " for each new (native) browser window, or you can share the same instance"
            + " (and state) between several browser windows (the latter is most useful"
            + " for read-only views).</p><br/><p>This is the end.</p>";

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        layout.setHeight(null);
        layout.setStyleName("borders");
        // layout.setSizeFull();
        final Label l = new Label(txt);
        l.setContentMode(ContentMode.HTML);
        // l.setWidth("100%");

        TextField tf = new TextField("This is a textField");
        tf.setWidth("100%");

        layout.addComponent(tf);
        layout.addComponent(l);
    }
}
