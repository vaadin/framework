package com.vaadin.tests.components.checkbox;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.CheckBox;

@PreserveOnRefresh
public class CheckBoxRevertValueChange extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final CheckBox alwaysUnchecked = new CheckBox("You may not check me");
        alwaysUnchecked.addValueChangeListener(event -> {
            if (alwaysUnchecked.getValue()) {
                log("I said no checking!");
                alwaysUnchecked.setValue(false);
            }
        });
        final CheckBox alwaysChecked = new CheckBox("You may not uncheck me");
        alwaysChecked.setValue(true);
        alwaysChecked.addValueChangeListener(event -> {
            if (!alwaysChecked.getValue()) {
                log("I said no unchecking!");
                alwaysChecked.setValue(true);
            }
        });

        addComponent(alwaysUnchecked);
        addComponent(alwaysChecked);
    }

    @Override
    protected String getTestDescription() {
        return "Ensure checking of a checkbox can be reverted on the server side without making the client go out of sync";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11028;
    }

}
