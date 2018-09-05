package com.vaadin.tests.widgetset.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.metadata.Invoker;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.SubPartAware;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.TestWidgetComponent;

@Connect(TestWidgetComponent.class)
public class TestWidgetConnector extends AbstractComponentConnector {
    public static class SubPartAwareSimplePanel extends SimplePanel
            implements SubPartAware {

        public SubPartAwareSimplePanel() {
            getElement().getStyle().setPosition(Position.ABSOLUTE);
        }

        @Override
        public Element getSubPartElement(String subPart) {
            Widget target = getWidget();
            if (target instanceof SubPartAware) {
                return ((SubPartAware) target).getSubPartElement(subPart);
            } else {
                return null;
            }
        }

        @Override
        public String getSubPartName(Element subElement) {
            Widget target = getWidget();
            if (target instanceof SubPartAware) {
                return ((SubPartAware) target).getSubPartName(subElement);

            } else {
                return null;
            }
        }

    }

    public static class TestWidgetState extends AbstractComponentState {
        public String widgetClass;
    }

    private final TestWidgetRegistry registry = GWT
            .create(TestWidgetRegistry.class);

    public abstract static class TestWidgetRegistry {
        private Map<String, Invoker> creators = new HashMap<>();

        // Called by generated sub class
        protected void register(String widgetClass, Invoker creator) {
            creators.put(widgetClass, creator);
        }

        public Widget createWidget(String widgetClass) {
            Invoker invoker = creators.get(widgetClass);
            if (invoker == null) {
                return new Label("Widget not found: " + widgetClass);
            } else {
                return (Widget) invoker.invoke(null);
            }
        }
    }

    @OnStateChange("widgetClass")
    private void updateWidgetClass() {
        getWidget().setWidget(registry.createWidget(getState().widgetClass));
    }

    @Override
    public TestWidgetState getState() {
        return (TestWidgetState) super.getState();
    }

    @Override
    public SubPartAwareSimplePanel getWidget() {
        return (SubPartAwareSimplePanel) super.getWidget();
    }
}
