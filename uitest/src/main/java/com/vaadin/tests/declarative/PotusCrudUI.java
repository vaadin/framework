package com.vaadin.tests.declarative;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class PotusCrudUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(new PotusCrud());
    }
}
