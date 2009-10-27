package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Window;

public class Ticket2022 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        CustomLayout l;

        // WebApplicationContext wac = ((WebApplicationContext) getContext());
        // File f = new File(wac.getBaseDirectory().getAbsoluteFile()
        // + "/VAADIN/themes/" + getTheme() + "/layouts/Ticket2022.html");

        l = new CustomLayout("Ticket2022");
        // try {
        // l = new CustomLayout(new FileInputStream(f));
        w.setLayout(l);
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
}
