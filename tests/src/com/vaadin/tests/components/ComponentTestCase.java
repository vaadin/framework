package com.vaadin.tests.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout.SpacingHandler;

public abstract class ComponentTestCase<T extends AbstractComponent> extends
        TestBase {

    private List<T> testComponents = new ArrayList<T>();

    @Override
    protected void setup() {
        ((SpacingHandler) getLayout()).setSpacing(true);
        addComponent(createActionLayout());
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    protected abstract List<Component> createActions();

    private Component createActionLayout() {
        HorizontalLayout actionLayout = new HorizontalLayout();
        actionLayout.setSpacing(true);
        actionLayout.setMargin(true);
        for (Component c : createActions()) {
            actionLayout.addComponent(c);
        }
        addComponent(actionLayout);
        return actionLayout;
    }

    protected void addTestComponent(T c) {
        testComponents.add(c);
        addComponent(c);
    }

    protected List<T> getTestComponents() {
        return testComponents;
    }

    protected void setErrorIndicators(boolean on) {
        for (T c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            if (on) {
                c.setComponentError(new UserError("It failed!"));
            } else {
                c.setComponentError(null);

            }
        }

    }

    protected void setRequired(boolean on) {

        for (T c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            if (c instanceof AbstractField) {
                ((AbstractField) c).setRequired(on);
            }

        }

    }

    protected void setEnabled(boolean on) {
        for (T c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            c.setEnabled(on);
        }

    }

    protected void setReadOnly(boolean on) {
        for (T c : getTestComponents()) {
            if (c == null) {
                continue;
            }

            c.setReadOnly(on);
        }

    }

}
