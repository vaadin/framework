package com.vaadin.tests.components;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ItemClickEvent.ItemClickNotifier;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;

public class ClickListeners extends AbstractTestUI {

    private class TestClickListener implements ClickListener,
            ItemClickListener, LayoutClickListener {
        @Override
        public void click(ClickEvent event) {
            logClick(event);
        }

        @Override
        public void itemClick(ItemClickEvent event) {
            logClick(event);
        }

        @Override
        public void layoutClick(LayoutClickEvent event) {
            logClick(event);
        }
    }

    private Log log = new Log(8);
    private GridLayout layout = new GridLayout(3, 2);
    private TestClickListener clickListener = new TestClickListener();

    @Override
    protected void setup(VaadinRequest request) {

        layout.setSpacing(true);

        addClickListener(clickListener);

        Panel p = new Panel();
        addTestComponent(p);

        Table t = new Table();
        t.setSelectable(true);
        t.addContainerProperty("foo", String.class, "foo");
        t.addItem();
        addTestComponent(t);

        Image i = new Image(null, new ExternalResource("notfound.jpg"));
        addTestComponent(i);

        HorizontalLayout hl = new HorizontalLayout();
        addTestComponent(hl);

        CssLayout cl = new CssLayout();
        // cl.addComponent(new Label("CSS"));
        addTestComponent(cl);

        AbsoluteLayout al = new AbsoluteLayout();
        addTestComponent(al);

        addComponent(log);
        addComponent(layout);
    }

    private void addTestComponent(Component c) {
        c.setWidth("100px");
        c.setHeight("100px");

        if (c instanceof LayoutClickNotifier) {
            ((LayoutClickNotifier) c).addLayoutClickListener(clickListener);
        } else if (c instanceof ItemClickNotifier) {
            ((ItemClickNotifier) c).addItemClickListener(clickListener);
        } else if (c instanceof Image) {
            ((Image) c).addClickListener(clickListener);
        } else if (c instanceof Panel) {
            ((Panel) c).addClickListener(clickListener);
        } else {
            log.log(c.getClass() + " not click notifier!");
        }

        Panel p = new Panel();
        p.setCaption(c.getClass().getSimpleName());
        p.addComponent(c);
        layout.addComponent(p);
    }

    private void logClick(ClickEvent e) {
        String caption = e.getComponent() == this ? "UI" : e.getComponent()
                .getParent().getParent().getCaption();
        log.log(caption + ": BUTTON=" + e.getButtonName() + " DBL="
                + e.isDoubleClick() + " ALT=" + e.isAltKey() + " CTRL="
                + e.isCtrlKey() + " SHIFT=" + e.isShiftKey() + " META="
                + e.isMetaKey());
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return "Test click listeners of various components";
    }
}
