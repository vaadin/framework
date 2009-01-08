package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket2426 extends Application {

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);

        final String content = "<select/>";

        w.addComponent(new Label("CONTENT_DEFAULT: " + content,
                Label.CONTENT_DEFAULT));
        w.addComponent(new Label("CONTENT_PREFORMATTED: " + content,
                Label.CONTENT_PREFORMATTED));
        w.addComponent(new Label("CONTENT_RAW: " + content, Label.CONTENT_RAW));
        w
                .addComponent(new Label("CONTENT_TEXT: " + content,
                        Label.CONTENT_TEXT));
        w.addComponent(new Label("CONTENT_XML: " + content, Label.CONTENT_XML));
        w.addComponent(new Label("CONTENT_XHTML: " + content,
                Label.CONTENT_XHTML));

    }

}
