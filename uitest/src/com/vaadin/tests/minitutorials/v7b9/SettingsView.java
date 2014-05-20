package com.vaadin.tests.minitutorials.v7b9;

import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class SettingsView extends Panel implements View {

    public static String NAME = "settings";

    Navigator navigator;
    DateField date;
    Button apply;
    Button cancel;

    String pendingViewAndParameters = null;

    public SettingsView(final Navigator navigator) {
        this.navigator = navigator;
        Layout layout = new VerticalLayout();

        date = new InlineDateField("Birth date");
        date.setImmediate(true);
        layout.addComponent(date);
        // pretend we have a datasource:
        date.setPropertyDataSource(new ObjectProperty<Date>(new Date()));
        date.setBuffered(true);
        // show buttons when date is changed
        date.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                hideOrShowButtons();
                pendingViewAndParameters = null;
            }
        });

        // commit the TextField changes when "Save" is clicked
        apply = new Button("Apply", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date.commit();
                hideOrShowButtons();
                processPendingView();
            }
        });
        layout.addComponent(apply);

        // Discard the TextField changes when "Cancel" is clicked
        cancel = new Button("Cancel", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                date.discard();
                hideOrShowButtons();
                processPendingView();
            }
        });
        cancel.setStyleName(Reindeer.BUTTON_LINK);
        layout.addComponent(cancel);

        // attach a listener so that we'll get asked isViewChangeAllowed?
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                if (event.getOldView() == SettingsView.this
                        && date.isModified()) {

                    // save the View where the user intended to go
                    pendingViewAndParameters = event.getViewName();
                    if (event.getParameters() != null) {
                        pendingViewAndParameters += "/";
                        pendingViewAndParameters += event.getParameters();
                    }

                    // Prompt the user to save or cancel if the name is changed
                    Notification.show("Please apply or cancel your changes",
                            Type.WARNING_MESSAGE);

                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                pendingViewAndParameters = null;
            }
        });

        setContent(layout);

    }

    // Hide or show buttons depending on whether date is modified or not
    private void hideOrShowButtons() {
        apply.setVisible(date.isModified());
        cancel.setVisible(date.isModified());
    }

    // if there is a pending view change, do it now
    private void processPendingView() {
        if (pendingViewAndParameters != null) {
            navigator.navigateTo(pendingViewAndParameters);
            pendingViewAndParameters = null;
        }
    }

    public void navigateTo(String fragmentParameters) {
        hideOrShowButtons();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
