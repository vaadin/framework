package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.AbsoluteLayout;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;

public class TestAbsoluteLayout extends TestBase {

    @Override
    protected String getDescription() {
        return "This is absolute layout tester.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        AbsoluteLayout layout = new AbsoluteLayout();
        setTheme("tests-tickets");
        layout.setStyleName("cyan");

        layout.addComponent(new Label("Hello World"));

        Button button = new Button("Centered button,z-index:10;");
        button.setSizeFull();
        layout.addComponent(button,
                "top:40%;bottom:40%;right:20%;left:20%;z-index:10;");

        Label label = new Label(
                "Exotic positioned label. Fullsize, top:100px; left:2cm; right: 3.5in; bottom:12.12mm ");
        label.setStyleName("yellow");
        label.setSizeFull();
        layout.addComponent(label,
                "top:100px; left:2cm; right: 3.5in; bottom:12.12mm");

        label = new Label("fullize, bottom:80%;left:80%;");
        label.setStyleName("green");
        label.setSizeFull();
        layout.addComponent(label, "bottom:80%;left:80%;");

        label = new Label("bottomright");
        label.setSizeUndefined();
        label.setStyleName("green");
        layout.addComponent(label, "bottom:0px; right:0px;");

        getLayout().setSizeFull();
        getLayout().addComponent(layout);

    }
}
