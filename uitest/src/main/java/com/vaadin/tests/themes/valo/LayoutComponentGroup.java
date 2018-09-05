package com.vaadin.tests.themes.valo;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class LayoutComponentGroup extends AbstractTestUI {

    private VerticalLayout container;

    @Override
    protected void setup(VaadinRequest request) {

        addComponent(new Label(
                "Test contains alternating rows with Button and Upload"));
        container = new VerticalLayout();
        container.setSizeUndefined();
        container.setSpacing(false);
        container.setId("container");

        createLayout("button", new Button("Upload"));
        createLayout("upload", new Upload());
        createLayout("button", new Button("Before"), new Button("Upload"));
        createLayout("upload", new Button("Before"), new Upload());
        createLayout("button", new Button("Before"), new Button("Upload"),
                new Button("After"));
        createLayout("upload", new Button("Before"), new Upload(),
                new Button("After"));

        addComponent(container);

    }

    private void createLayout(String info, Component... components) {
        CssLayout mainMenuLayout = new CssLayout();
        mainMenuLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        for (Component c : components) {
            mainMenuLayout.addComponent(c);
        }
        container.addComponent(mainMenuLayout);

    }

}
