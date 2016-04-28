package com.vaadin.tests.minitutorials.v7b9;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MainViewEarlierExample extends Panel implements View {

    public static final String NAME = "";

    public MainViewEarlierExample() {

        VerticalLayout layout = new VerticalLayout();

        Link lnk = new Link("Count",
                new ExternalResource("#!" + CountView.NAME));
        layout.addComponent(lnk);

        lnk = new Link("Message: Hello", new ExternalResource("#!"
                + MessageView.NAME + "/Hello"));
        layout.addComponent(lnk);

        lnk = new Link("Message: Bye", new ExternalResource("#!"
                + MessageView.NAME + "/Bye/Goodbye"));
        layout.addComponent(lnk);

        lnk = new Link("Private message: Secret", new ExternalResource("#!"
                + SecretView.NAME + "/Secret"));
        layout.addComponent(lnk);

        lnk = new Link("Private message: Topsecret", new ExternalResource("#!"
                + SecretView.NAME + "/Topsecret"));
        layout.addComponent(lnk);

        // login/logout toggle so we can test this
        Button logInOut = new Button("Toggle login",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Object user = ((NavigationtestUI) UI.getCurrent())
                                .getLoggedInUser();
                        ((NavigationtestUI) UI.getCurrent())
                                .setLoggedInUser(user == null ? "Smee" : null);
                    }
                });
        layout.addComponent(logInOut);
        setContent(layout);
    }

    @Override
    public void enter(ViewChangeEvent event) {

    }

}
