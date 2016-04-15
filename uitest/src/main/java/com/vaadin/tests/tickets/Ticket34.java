package com.vaadin.tests.tickets;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket34 extends LegacyApplication {

    private Map<String, Component> views = new HashMap<String, Component>();
    private VerticalLayout mainLayout;
    private Component currentView;

    @Override
    public void init() {

        buildViews(new String[] { "main", "view2", "view3" });

        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        final LegacyWindow mainWin = new LegacyWindow(
                "Test app for URI fragment management/reading", mainLayout);
        setMainWindow(mainWin);

        mainWin.getPage().addListener(new Page.UriFragmentChangedListener() {

            @Override
            public void uriFragmentChanged(UriFragmentChangedEvent event) {
                getMainWindow().showNotification(
                        "Fragment now: " + event.getUriFragment());
                // try to change to view mapped by fragment string
                setView(event.getUriFragment());
            }
        });

        setView("main");

    }

    private void setView(String string) {
        Component component = views.get(string);
        if (component == null) {
            getMainWindow().showNotification(
                    "View called " + string + " not found!");
        } else if (component != currentView) {
            if (currentView != null) {
                mainLayout.replaceComponent(currentView, component);
            } else {
                mainLayout.addComponent(component);
            }
            // give all extra space for view
            mainLayout.setExpandRatio(component, 1);
            currentView = component;
        }
    }

    private void buildViews(String[] strings) {
        for (String string : strings) {
            VerticalLayout pl = new VerticalLayout();
            pl.setMargin(true);
            Panel p = new Panel(string, pl);
            p.setSizeFull();
            pl.setSpacing(true);
            pl.addComponent(new Label("This is a simple test case for "
                    + "UriFragmentReader that can be used for"
                    + " adding linking, back/forward button "
                    + "and history support for ajax application. "));
            StringBuffer sb = new StringBuffer();
            sb.append("Available views : ");
            for (String key : strings) {
                sb.append(key);
                sb.append(" ");
            }
            sb.append("Application will change to them from uri "
                    + "fragment or server initiated via textfield below.");
            pl.addComponent(new Label(sb.toString()));

            final TextField tf = new TextField(
                    "Type view name (will change to that "
                            + "view and change the uri fragment)");
            pl.addComponent(tf);
            Button b = new Button("Go!");
            pl.addComponent(b);
            b.addListener(new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    String viewName = tf.getValue().toString();
                    // fragmentChangedListener will change the view if possible
                    event.getButton().getUI().getPage()
                            .setUriFragment(viewName);
                }
            });

            views.put(string, p);
        }
    }

}
