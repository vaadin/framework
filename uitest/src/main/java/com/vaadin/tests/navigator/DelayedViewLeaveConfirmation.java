package com.vaadin.tests.navigator;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewLeaveAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class DelayedViewLeaveConfirmation extends AbstractTestUI {

    public static class OtherView extends VerticalLayout implements View {
        public OtherView() {
            addComponent(new Label("Just another view"));
        }

        @Override
        public void enter(ViewChangeEvent event) {

        }
    }

    public static class MainView extends VerticalLayout implements View {
        private Label saved;
        private TextField input;

        public MainView() {
            saved = new Label("Initial");
            saved.setCaption("Saved value");
            input = new TextField("Enter a value");
            input.setId("input");
            Button navigateAway = new Button("Navigate to the other view",
                    event -> getUI().getNavigator().navigateTo("other"));
            Button logout = new Button("Simulate logout", event -> getUI()
                    .getNavigator().runAfterLeaveConfirmation(() -> {
                        removeAllComponents();
                        addComponent(new Label("You have been logged out"));
                        getUI().getPage().setUriFragment("", false);
                    }));
            navigateAway.setId("navigateAway");
            logout.setId("logout");
            addComponents(saved, input, navigateAway, logout);
        }

        @Override
        public void enter(ViewChangeEvent event) {
            input.setValue(saved.getValue());
        }

        @Override
        public void beforeLeave(ViewBeforeLeaveEvent event) {
            boolean hasChanges = !(saved.getValue().equals(input.getValue()));
            if (hasChanges) {
                getUI().addWindow(new ConfirmationWindow(event::navigate));
            } else {
                event.navigate();
            }
        }

    }

    public static class ConfirmationWindow extends Window {
        public ConfirmationWindow(ViewLeaveAction action) {
            super();
            VerticalLayout layout = new VerticalLayout();
            layout.addComponent(new Label(
                    "You have unsaved changes. Are you sure you want to leave?"));
            Button leave = new Button("YES, LEAVE!", event -> {
                close();
                action.run();
            });
            leave.setId("leave");
            Button stay = new Button("NO, STAY!", event -> close());
            stay.setId("stay");
            layout.addComponents(new HorizontalLayout(leave, stay));
            setContent(layout);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        setNavigator(new Navigator(this, this));
        getNavigator().addView("main", MainView.class);
        getNavigator().addView("other", OtherView.class);
    }

}
