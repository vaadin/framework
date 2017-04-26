package com.vaadin.test.dependencyrewrite;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DependencyDynamicUI extends UI {

    @JavaScript("http://jquery.com/jquery-1.2.3.js")
    public static class MyJqueryLabel extends Label {
        public MyJqueryLabel() {
            super("MyJqueryLabel");
        }
    }

    @JavaScript("vaadin://jquery-33.3.3.js")
    public static class MyJqueryLabel2 extends Label {
        public MyJqueryLabel2() {
            super("MyJqueryLabel2");
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new MyJqueryLabel());
        layout.addComponent(new MyJqueryLabel2());

        setContent(layout);
    }

}
