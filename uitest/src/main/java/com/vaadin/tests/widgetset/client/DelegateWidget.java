package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.HTML;

public class DelegateWidget extends HTML {
    private String value1;
    private int value2;
    private Boolean value3;
    private double value4;

    public void setValue1(String value1) {
        this.value1 = value1;
        updateText();
    }

    public void setValue2(int value2) {
        this.value2 = value2;
        updateText();
    }

    public void setValue3(Boolean value3) {
        this.value3 = value3;
        updateText();
    }

    public void setValue4(double value4) {
        this.value4 = value4;
        updateText();
    }

    private void updateText() {
        setHTML(value1 + "<br />" + value2 + "<br />" + value3 + "<br />"
                + value4 + "<br />");
    }
}
