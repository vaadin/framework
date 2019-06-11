package com.vaadin.tests.components.ui;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
public class RefreshUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Navigator navigator = new Navigator(this, this);
        navigator.addView("", MyView.class);
        setNavigator(navigator);
    }

    public static class MyView extends VerticalLayout implements View {
        private static int instanceNumber = 0;

        public MyView() {
            instanceNumber++;
            addComponent(new Label("This is instance no " + instanceNumber));
        }
    }
}
