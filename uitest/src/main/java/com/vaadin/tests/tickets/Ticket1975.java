package com.vaadin.tests.tickets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket1975 extends LegacyApplication {

    private CustomLayout cl1;
    private CustomLayout cl2;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);
        setTheme("tests-tickets");
        GridLayout layout = new GridLayout(1, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        String s = "<b>Blah</b><input type=\"text\" value='Lorem\" ipsum'/>";
        try {
            cl1 = new CustomLayout(new ByteArrayInputStream(s.getBytes()));
            layout.addComponent(cl1);

            layout.addComponent(new Button("Disable/Enable",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            boolean e = cl1.isEnabled();

                            cl1.setEnabled(!e);
                            cl2.setEnabled(!e);
                        }

                    }));
            File baseDir = VaadinService.getCurrent().getBaseDirectory()
                    .getAbsoluteFile();
            File f = new File(baseDir + "/VAADIN/themes/" + getTheme()
                    + "/layouts/Ticket1975.html");

            cl2 = new CustomLayout(new FileInputStream(f));
            layout.addComponent(cl2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
