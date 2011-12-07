package com.vaadin.tests.tickets;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.FragmentChangedEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket34 extends Application.LegacyApplication {

    private Map<String, Component> views = new HashMap<String, Component>();
    private VerticalLayout mainLayout;
    private Component currentView;

    @Override
    public void init() {

        buildViews(new String[] { "main", "view2", "view3" });

        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        final Root mainWin = new Root(
                "Test app for URI fragment management/reading", mainLayout);
        setMainWindow(mainWin);

        mainWin.addListener(new Root.FragmentChangedListener() {

            public void fragmentChanged(FragmentChangedEvent event) {
                getMainWindow().showNotification(
                        "Fragment now: " + event.getFragment());
                // try to change to view mapped by fragment string
                setView(event.getFragment());
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
            Panel p = new Panel(string);
            p.setSizeFull();
            ((VerticalLayout) p.getContent()).setSpacing(true);
            p.addComponent(new Label("This is a simple test case for "
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
            p.addComponent(new Label(sb.toString()));

            final TextField tf = new TextField(
                    "Type view name (will change to that "
                            + "view and change the uri fragment)");
            p.addComponent(tf);
            Button b = new Button("Go!");
            p.addComponent(b);
            b.addListener(new Button.ClickListener() {

                public void buttonClick(ClickEvent event) {
                    String viewName = tf.getValue().toString();
                    // fragmentChangedListener will change the view if possible
                    event.getButton().getRoot().setFragment(viewName);
                }
            });

            views.put(string, p);
        }
    }

}
