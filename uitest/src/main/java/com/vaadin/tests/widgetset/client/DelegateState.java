package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

public class DelegateState extends AbstractComponentState {
    @DelegateToWidget
    public String value1;

    @DelegateToWidget("setValue2")
    public int renamedValue2;

    private Boolean value3;

    private double renamedValue4;

    @DelegateToWidget
    public void setValue3(Boolean value3) {
        this.value3 = value3;
    }

    public Boolean getValue3() {
        return value3;
    }

    @DelegateToWidget("setValue4")
    public void setRenamedValue4(double renamedValue4) {
        this.renamedValue4 = renamedValue4;
    }

    public double getRenamedValue4() {
        return renamedValue4;
    }
}
