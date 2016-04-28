package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.LegacyWindow;

public class Ticket2022 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        CustomLayout l;

        // WebApplicationContext wac = ((WebApplicationContext) getContext());
        // File f = new File(wac.getBaseDirectory().getAbsoluteFile()
        // + "/VAADIN/themes/" + getTheme() + "/layouts/Ticket2022.html");

        l = new CustomLayout("Ticket2022");
        // try {
        // l = new CustomLayout(new FileInputStream(f));
        w.setContent(l);
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }
}
