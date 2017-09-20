package com.vaadin.test.dependencyrewrite;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.test.dependencyrewrite.DependencyDynamicUI.MyJqueryLabel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@JavaScript("http://jquery.com/jquery-12.2.3.js")
@JavaScript("vaadin://jquery-323.3.3.js")
public class DependencyInitialUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new MyJqueryLabel());

        setContent(layout);
    }

}
