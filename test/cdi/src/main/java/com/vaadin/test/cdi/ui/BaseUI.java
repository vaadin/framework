package com.vaadin.test.cdi.ui;

import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.vaadin.cdi.AfterViewChange;
import com.vaadin.cdi.CDINavigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public abstract class BaseUI extends UI {

    public static final String NAVIGATION_TEXT = "Navigated to: %s";

    @Inject
    /* This should be automatic */
    private CDINavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        Page.getCurrent().getStyles()
                .add(".vertical-wrap-flex { "
                        + "display: flex; flex-wrap: wrap; flex-direction: column;"
                        + "}");

        VerticalLayout content = new VerticalLayout();
        Panel container = new Panel();
        HorizontalLayout naviBar = new HorizontalLayout();

        content.addComponents(naviBar);
        content.addComponentsAndExpand(container);

        container.setSizeFull();
        content.setSizeFull();

        setContent(content);

        // Create navigation bar links
        Button firstLink = new Button("Default",
                e -> getNavigator().navigateTo(""));
        firstLink.addStyleName(ValoTheme.BUTTON_LINK);
        naviBar.addComponent(firstLink);

        Stream.of("param", "param/foo", "name/foo", "name/bar", "new",
                "persisting", "persisting/foo")
                .map(n -> new Button(n, e -> getNavigator().navigateTo(n)))
                .forEach(b -> {
                    b.addStyleName(ValoTheme.BUTTON_LINK);
                    naviBar.addComponent(b);
                });

        // This should be automatic
        navigator.init(this, container);
        setNavigator(navigator);
    }

    protected void notifyNavigationToNonDefault(
            @Observes @AfterViewChange ViewChangeEvent event) {
        if (!event.getViewName().isEmpty()) {
            Notification.show(
                    String.format(NAVIGATION_TEXT, event.getViewName()),
                    Type.TRAY_NOTIFICATION);
        }
    }
}
