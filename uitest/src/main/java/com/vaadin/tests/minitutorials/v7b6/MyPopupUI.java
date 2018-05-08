package com.vaadin.tests.minitutorials.v7b6;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class MyPopupUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("This is MyPopupUI where parameter foo="
                + request.getParameter("foo") + " and fragment is set to "
                + getPage().getUriFragment()));
    }

}
