package com.itmill.toolkit.tests.tickets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket2297 extends Ticket2292 {

    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);
        URL url = getURL();
        main
                .addComponent(new Label(
                        "Icon is built by servlet with a slow method, so it will show the bug (components not firing requestLayout)."));

        try {
            CustomLayout cl = new CustomLayout(
                    new ByteArrayInputStream(
                            ("This is an empty CustomLayout with as slow image. <img src=\""
                                    + url.toString() + "/icon.png\" />")
                                    .getBytes()));
            main.addComponent(cl);

            cl = new CustomLayout(
                    new ByteArrayInputStream(
                            ("This is an empty CustomLayout with as slow image. <img src=\""
                                    + url.toString() + "/icon.png\" />")
                                    .getBytes()));
            main.addComponent(cl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}