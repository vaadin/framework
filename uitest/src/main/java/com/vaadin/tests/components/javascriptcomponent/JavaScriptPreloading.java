package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.AbstractJavaScriptComponent;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class JavaScriptPreloading extends AbstractTestUI {

    public static class JsLabelState extends JavaScriptComponentState {
        public String xhtml;
    }

    @JavaScript("js_label.js")
    @JavaScript("wholly_different.js")
    public class JsLabel extends AbstractJavaScriptComponent {

        public JsLabel(final String xhtml) {
            getState().xhtml = xhtml;
        }

        @Override
        protected JsLabelState getState() {
            return (JsLabelState) super.getState();
        }
    }

    private final Log log = new Log(5);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);

        final JsLabel c = new JsLabel("Hello World!");
        c.setId("js-component");
        addComponent(c);
    }

    @Override
    protected String getTestDescription() {
        return "Loading javascript component with multiple sourcefiles should not break IE11";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13956);
    }

}
