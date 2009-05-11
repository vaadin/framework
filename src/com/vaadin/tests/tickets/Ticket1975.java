package com.vaadin.tests.tickets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket1975 extends Application {

    private CustomLayout cl1;
    private CustomLayout cl2;

    @Override
    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        setTheme("tests-tickets");
        GridLayout layout = new GridLayout(1, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        String s = "<b>Blah</b><input type=\"text\" value='Lorem\" ipsum'/>";
        try {
            cl1 = new CustomLayout(new ByteArrayInputStream(s.getBytes()));
            layout.addComponent(cl1);
            WebApplicationContext wc = ((WebApplicationContext) getContext());

            layout.addComponent(new Button("Disable/Enable",
                    new ClickListener() {

                        public void buttonClick(ClickEvent event) {
                            boolean e = cl1.isEnabled();

                            cl1.setEnabled(!e);
                            cl2.setEnabled(!e);
                        }

                    }));
            File f = new File(wc.getBaseDirectory().getAbsoluteFile()
                    + "/ITMILL/themes/" + getTheme()
                    + "/layouts/Ticket1975.html");

            cl2 = new CustomLayout(new FileInputStream(f));
            layout.addComponent(cl2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
