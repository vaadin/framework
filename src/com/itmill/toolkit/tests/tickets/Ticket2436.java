package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.PopupView;
import com.itmill.toolkit.ui.Window;

public class Ticket2436 extends Application {

    public void init() {
        final Window main = new Window();
        setMainWindow(main);

        final Button remover = new Button("Remove PopupView");
        final PopupView pv = new PopupView(new PopupView.Content() {
            public String getMinimizedValueAsHTML() {
                return "PopupView";
            }

            public Component getPopupComponent() {
                return remover;
            }
        });

        remover.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                main.removeComponent(pv);
            }
        });

        main.addComponent(pv);
    }

}
