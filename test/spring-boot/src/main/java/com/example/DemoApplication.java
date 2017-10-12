package com.example;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@SpringUI
class RootPathUI extends AbstractSpringUI {

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Label label = new Label("RootPathUI");
        label.setId("rootpath");
        navigationBar.addComponent(label);
    }
}

@PushStateNavigation
@SpringUI(path = SubPathUI.SUBPATH)
class SubPathUI extends AbstractSpringUI {

    static final String SUBPATH = "subpath";

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);

        Label label = new Label("SubPathUI");
        label.setId(SUBPATH);
        navigationBar.addComponent(label);
    }
}

@UIScope
@SpringViewDisplay
class ViewDisplayPanel extends Panel implements ViewDisplay {

    @PostConstruct
    void init() {
        setSizeFull();
    }

    @Override
    public void showView(View view) {
        setContent((Component) view);
    }

}

abstract class AbstractSpringUI extends UI {

    @Autowired
    private ViewDisplayPanel springViewDisplay;
    protected CssLayout navigationBar;

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        navigationBar = new CssLayout();
        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        navigationBar.addComponent(
                createNavigationButton("Default View", DefaultView.VIEW_NAME));
        navigationBar.addComponent(createNavigationButton("UI Scoped View",
                UIScopedView.VIEW_NAME));
        navigationBar.addComponent(createNavigationButton("View Scoped View",
                ViewScopedView.VIEW_NAME));
        root.addComponent(navigationBar);

        root.addComponent(springViewDisplay);
        root.setExpandRatio(springViewDisplay, 1.0f);

    }

    private Button createNavigationButton(String caption,
            final String viewName) {
        Button button = new Button(caption);
        button.setId(viewName + "-button");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(
                event -> getUI().getNavigator().navigateTo(viewName));
        return button;
    }

    @Override
    public String getUiRootPath() {
        // FIXME: Should be handled by Spring plug-in
        return super.getUiRootPath() + "/"
                + getClass().getAnnotation(SpringUI.class).path();
    }
}

@SpringView(name = DefaultView.VIEW_NAME)
class DefaultView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";

    @Autowired
    private ThankYouService service;

    @PostConstruct
    void init() {
        setId("default-view");
        Button button = new Button("Click Me!",
                e -> Notification.show(service.getText()));
        addComponent(button);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}

@SpringView(name = ViewScopedView.VIEW_NAME)
class ViewScopedView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "view-scoped";

    @Autowired
    ViewGreeter service;

    @PostConstruct
    void init() {
        setId(VIEW_NAME);
        setMargin(true);
        setSpacing(true);
        addComponents(new Label("This is a view scoped view"),
                new Label(service.sayHello()));

    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}

@UIScope
@SpringView(name = UIScopedView.VIEW_NAME)
class UIScopedView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "ui-scoped";

    @Autowired
    ViewGreeter service;

    @PostConstruct
    void init() {
        setId(VIEW_NAME);
        setMargin(true);
        setSpacing(true);
        addComponents(new Label("This is a UI scoped view."),
                new Label(service.sayHello()));
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }
}