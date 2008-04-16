package com.itmill.toolkit.demo.reservation.simple;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * This is a stripped down version of Reservr example. Idea is to create simple,
 * but actually usable portal gadget.
 * 
 */
public class SimpleReserver extends Application {

    private SampleDB db = new SampleDB();

    private StdView stdView = new StdView(this);

    private AdminView adminView = new AdminView(this);

    private Button toggleMode = new Button("Switch mode");

    private boolean isAdminView = false;

    public void init() {
        final Window w = new Window("Simple Reserver");
        w.setTheme("simplereserver");
        setMainWindow(w);
        w.addComponent(toggleMode);
        w.addComponent(stdView);
        toggleMode.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                OrderedLayout main = (OrderedLayout) w.getLayout();
                isAdminView = !isAdminView;
                if (isAdminView) {
                    main.replaceComponent(stdView, adminView);
                } else {
                    main.replaceComponent(adminView, stdView);
                    stdView.refreshData();
                }

            }
        });
    }

    public SampleDB getDb() {
        return db;
    }

    public Object getUser() {
        // TODO expand for Portal support
        Object user = super.getUser();
        if (user == null) {
            return "Demo User";
        } else {
            return user;
        }
    }

}
