package com.vaadin.tests.components.ui;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

@JavaScript("uiDependency.js")
@StyleSheet({ "theme://uiDependency1.css", "theme://uiDependency2.css" })
@StyleSheet("theme://uiDependency3.css")
@Theme("tests-valo")
public class UiDependenciesInHtml extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label statusBox = new Label("Status box");
        statusBox.setId("statusBox");
        addComponent(statusBox);

        getPage().getJavaScript().execute("window.reportUiDependencyStatus();");
    }
}
