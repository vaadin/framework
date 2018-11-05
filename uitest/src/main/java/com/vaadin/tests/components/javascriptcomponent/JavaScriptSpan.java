package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractJavaScriptComponent;

public class JavaScriptSpan extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Span("Hello World"));
    }

    @JavaScript("JavaScriptSpanComponent.js")
    public static class Span extends AbstractJavaScriptComponent {
        public Span(String text) {
            this.getState().text = text;
        }

        @Override
        protected SpanState getState() {
            return (SpanState) super.getState();
        }
    }

    public static class SpanState extends JavaScriptComponentState {
        public String text;
    }
}
