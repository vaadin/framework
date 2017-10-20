package com.vaadin.test.cdi.views;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GreetingView extends VerticalLayout implements View {

    public static final String CALL_COUNT_FORMAT = "Current call count: %d";

    @Inject
    private GreetingService service;

    CssLayout greetingLog = new CssLayout();
    Label callCount = new Label("");

    public GreetingView() {
        addComponentsAndExpand(greetingLog);
        addComponent(callCount);
        setComponentAlignment(callCount, Alignment.BOTTOM_LEFT);
        greetingLog.setSizeFull();
        greetingLog.addStyleName("vertical-wrap-flex");
        greetingLog.setId("log");
        callCount.setId("callCount");
        setSizeFull();

        setExpandRatio(greetingLog, 1.0f);

        setId(getClass().getAnnotation(CDIView.class).value());
    }

    private void updateCallCount() {
        callCount.setValue(
                String.format(CALL_COUNT_FORMAT, service.getCallCount()));
    }

    private void greet(String name) {
        greetingLog.addComponent(new Label(service.getGreeting(name)));
        updateCallCount();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        greet(event.getParameters());
    }
}