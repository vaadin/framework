package com.vaadin.tests.components;

import java.io.File;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractTestUI extends UI {

    @Override
    public void init(VaadinRequest request) {
        getPage().setTitle(getClass().getName());

        Label label = new Label(getTestDescription(), ContentMode.HTML);
        label.setWidth("100%");

        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setMargin(true);
        setContent(rootLayout);

        layout = new VerticalLayout();

        rootLayout.addComponent(label);
        rootLayout.addComponent(layout);
        ((VerticalLayout) getContent()).setExpandRatio(layout, 1);

        warnIfWidgetsetMaybeNotCompiled();

        setup(request);
    }

    protected void warnIfWidgetsetMaybeNotCompiled() {
        // Ignore if using debug mode
        if (getPage().getLocation().getQuery().matches(".*[&?]gwt\\.codesvr.*")) {
            return;
        }

        // Find out the widgetset of this UI based on @Widgetset annotation
        Class<?> currentType = getClass();
        String usedWidgetset = VaadinServlet.DEFAULT_WIDGETSET;
        while (currentType != Object.class) {
            Widgetset annotation = currentType.getAnnotation(Widgetset.class);
            if (annotation != null) {
                usedWidgetset = annotation.value();
                break;
            } else {
                currentType = currentType.getSuperclass();
            }
        }

        // Assuming the same folder structure as in git repo
        // Assuming project root is the working dir of this process
        File widgetsetsFolder = new File("WebContent/VAADIN/widgetsets");
        if (!widgetsetsFolder.isDirectory()) {
            return;
        }

        // Find the most newly compiled widgetset
        long newestWidgetsetTimestamp = -1;
        String newestWidgetsetName = null;
        File[] children = widgetsetsFolder.listFiles();
        for (File child : children) {
            if (!child.isDirectory() || child.getName().equals("WEB-INF")) {
                continue;
            }
            long lastModified = child.lastModified();
            if (lastModified > newestWidgetsetTimestamp) {
                newestWidgetsetTimestamp = lastModified;
                newestWidgetsetName = child.getName();
            }
        }

        // Compare to currently used widgetset, with a 30 minute grace period
        File currentWidgetsetFolder = new File(widgetsetsFolder, usedWidgetset);
        long currentWidgetsetTimestamp = currentWidgetsetFolder.lastModified();
        int halfHour = 30 * 60 * 1000;
        if (currentWidgetsetTimestamp + halfHour < newestWidgetsetTimestamp) {
            Notification
                    .show("The currently used widgetset ("
                            + usedWidgetset
                            + ") was compiled long before the most recently compiled one ("
                            + newestWidgetsetName
                            + "). Are you sure you have compiled the right widgetset?",
                            Type.WARNING_MESSAGE);
        }
    }

    private VerticalLayout layout;

    protected VerticalLayout getLayout() {
        return layout;
    }

    protected abstract void setup(VaadinRequest request);

    public void addComponent(Component c) {
        getLayout().addComponent(c);
    }

    public void removeComponent(Component c) {
        getLayout().removeComponent(c);
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        getLayout().replaceComponent(oldComponent, newComponent);
    }

    protected abstract String getTestDescription();

    protected abstract Integer getTicketNumber();

    protected WebBrowser getBrowser() {
        return getSession().getBrowser();
    }

}
