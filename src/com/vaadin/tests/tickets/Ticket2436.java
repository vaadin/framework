package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Window;

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
