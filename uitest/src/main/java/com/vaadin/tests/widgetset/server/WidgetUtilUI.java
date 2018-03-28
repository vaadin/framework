package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.WidgetUtilTestComponentState;
import com.vaadin.ui.AbstractComponent;

@Widgetset(TestingWidgetSet.NAME)
public class WidgetUtilUI extends AbstractReindeerTestUI {

    public static class WidgetUtilTestComponent extends AbstractComponent {

        public WidgetUtilTestComponent(boolean inline) {
            getState().inline = inline;
        }

        @Override
        protected WidgetUtilTestComponentState getState() {
            return (WidgetUtilTestComponentState) super.getState();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new WidgetUtilTestComponent(
                request.getParameter("inline") != null));
    }
}
