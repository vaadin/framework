package com.vaadin.tests.layouts.layouttester;

import com.vaadin.ui.Label;

public class UndefWideLabel extends Label {

    public UndefWideLabel(String value) {
        super(value);
        setWidth(null);
    }

}
