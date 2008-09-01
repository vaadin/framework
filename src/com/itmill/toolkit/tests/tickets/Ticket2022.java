package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2022 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        CustomLayout l;

        // WebApplicationContext wac = ((WebApplicationContext) getContext());
        // File f = new File(wac.getBaseDirectory().getAbsoluteFile()
        // + "/ITMILL/themes/" + getTheme() + "/layouts/Ticket2022.html");

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
