package com.vaadin.tests.tickets;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;

public class Ticket34 extends Application {

    private Map<String, Component> views = new HashMap<String, Component>();
    private VerticalLayout mainLayout;
    private Component currentView;
    private UriFragmentUtility reader;

    @Override
    public void init() {

        buildViews(new String[] { "main", "view2", "view3" });

        reader = new UriFragmentUtility();
        reader.addListener(new UriFragmentUtility.FragmentChangedListener() {

            public void fragmentChanged(FragmentChangedEvent event) {
                getMainWindow().showNotification(
                        "Fragment now: "
                                + event.getUriFragmentUtility().getFragment());
                // try to change to view mapped by fragment string
                setView(event.getUriFragmentUtility().getFragment());
            }
        });

        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        final Window mainWin = new Window(
                "Test app for URI fragment management/reading", mainLayout);
        setMainWindow(mainWin);

        // UriFragmentReader is 0px size by default, so it will not render
        // anything on screen
        mainLayout.addComponent(reader);

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
            ((VerticalLayout) p.getLayout()).setSpacing(true);
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
                    reader.setFragment(viewName);
                }
            });

            views.put(string, p);
        }
    }

}
