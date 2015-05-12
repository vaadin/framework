package com.vaadin.tests.components;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

public class LayoutAttachListenerInfo extends TestBase {

    private VerticalLayout content = new VerticalLayout();

    @Override
    protected void setup() {

        OptionGroup layouts = new OptionGroup("Layouts", Arrays.asList(
                "AbsoluteLayout", "OrderedLayout", "GridLayout"));
        layouts.select("AbsoluteLayout");
        layouts.setImmediate(true);
        layouts.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue().equals("AbsoluteLayout")) {
                    testAbsoluteLayout();
                } else if (event.getProperty().getValue()
                        .equals("OrderedLayout")) {
                    testOrderedLayout();
                } else if (event.getProperty().getValue().equals("GridLayout")) {
                    testGridLayout();
                }
            }
        });

        addComponent(layouts);
        addComponent(content);

        testAbsoluteLayout();
    }

    @Override
    protected String getDescription() {
        return "When pressing the attach button a Label with the value \"X\" "
                + "should get added to the selected layout and a notification of the position"
                + " of the component should be visible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6368;
    }

    private void testAbsoluteLayout() {
        content.removeAllComponents();

        final AbsoluteLayout a = new AbsoluteLayout();
        a.setWidth("300px");
        a.setHeight("300px");
        a.addComponentAttachListener(new ComponentAttachListener() {
            @Override
            public void componentAttachedToContainer(ComponentAttachEvent event) {
                AbsoluteLayout layout = (AbsoluteLayout) event.getContainer();
                AbsoluteLayout.ComponentPosition position = layout
                        .getPosition(event.getAttachedComponent());

                getMainWindow().showNotification(
                        "Attached to " + position.getCSSString(),
                        Notification.TYPE_ERROR_MESSAGE);
            }
        });
        content.addComponent(a);

        content.addComponent(new Button("Attach label to layout",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        a.addComponent(new Label("X"), "top:50px;left:50px");
                    }
                }));
    }

    private void testOrderedLayout() {
        content.removeAllComponents();

        final VerticalLayout v = new VerticalLayout();
        v.setWidth("300px");
        v.setHeight("300px");
        v.addComponentAttachListener(new ComponentAttachListener() {
            @Override
            public void componentAttachedToContainer(ComponentAttachEvent event) {
                VerticalLayout layout = (VerticalLayout) event.getContainer();
                getMainWindow().showNotification(
                        "Attached to index "
                                + layout.getComponentIndex(event
                                        .getAttachedComponent()),
                        Notification.TYPE_ERROR_MESSAGE);
            }
        });
        content.addComponent(v);

        content.addComponent(new Button("Attach label to layout",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        v.addComponent(new Label("X"));

                    }
                }));
    }

    private void testGridLayout() {
        content.removeAllComponents();

        final GridLayout g = new GridLayout(4, 4);
        g.setWidth("300px");
        g.setHeight("300px");
        g.setHideEmptyRowsAndColumns(true);
        g.addComponentAttachListener(new ComponentAttachListener() {
            @Override
            public void componentAttachedToContainer(ComponentAttachEvent event) {
                GridLayout layout = (GridLayout) event.getContainer();
                GridLayout.Area area = layout.getComponentArea(event
                        .getAttachedComponent());
                getMainWindow().showNotification(
                        "Attached to " + area.getColumn1() + ","
                                + area.getRow1(),
                        Notification.TYPE_ERROR_MESSAGE);
            }
        });

        content.addComponent(g);

        content.addComponent(new Button("Attach label to layout",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        g.addComponent(new Label("X"), 2, 3);
                    }
                }));
    }
}
