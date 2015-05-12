package com.vaadin.tests.navigator;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class NavigatorListenerModifiesListeners extends AbstractTestUI {

    private Navigator navigator;

    protected static final String LABEL_MAINVIEW_ID = "LABEL_MAINVIEW_ID";
    protected static final String LABEL_ANOTHERVIEW_ID = "LABEL_ANOTHERVIEW_ID";

    // NOP view change listener
    private class MyViewChangeListener implements ViewChangeListener {
        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            navigator.removeViewChangeListener(listener1);
            navigator.addViewChangeListener(listener2);
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            navigator.removeViewChangeListener(listener2);
            navigator.addViewChangeListener(listener1);
        }
    }

    private MyViewChangeListener listener1 = new MyViewChangeListener();
    private MyViewChangeListener listener2 = new MyViewChangeListener();

    @Override
    protected void setup(VaadinRequest request) {
        navigator = new Navigator(this, this);
        navigator.addView(MainView.NAME, new MainView());
        navigator.addView(AnotherView.NAME, new AnotherView());
        navigator.addViewChangeListener(listener1);
        navigator.navigateTo(MainView.NAME);
    }

    class MainView extends VerticalLayout implements View {

        public static final String NAME = "mainview";

        public MainView() {
            Label label = new Label("MainView content");
            label.setId(LABEL_MAINVIEW_ID);
            addComponent(label);

            Button buttonNavToAnotherView = new Button(
                    "Navigate to another view", new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            navigator.navigateTo(AnotherView.NAME);
                        }
                    });
            addComponent(buttonNavToAnotherView);
        }

        @Override
        public void enter(ViewChangeEvent event) {
        }

    }

    class AnotherView extends VerticalLayout implements View {

        public static final String NAME = "another";

        public AnotherView() {
            Label label = new Label("AnotherView content");
            label.setId(LABEL_ANOTHERVIEW_ID);
            addComponent(label);
        }

        @Override
        public void enter(ViewChangeEvent event) {
        }
    }

    @Override
    protected String getTestDescription() {
        return "Adding and removing view change listeners from view change listeners should not cause a ConcurrentModificationException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17477;
    }
}