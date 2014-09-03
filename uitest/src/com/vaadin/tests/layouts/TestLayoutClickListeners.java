package com.vaadin.tests.layouts;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TestLayoutClickListeners extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout layoutsLayout = new HorizontalLayout();
        layoutsLayout.setSpacing(true);
        addComponent(layoutsLayout);

        layoutsLayout.addComponent(createClickableGridLayout());
        layoutsLayout.addComponent(createClickableVerticalLayout());
        layoutsLayout.addComponent(createClickableAbsoluteLayout());
        layoutsLayout.addComponent(createClickableCSSLayout());
    }

    private Component createClickableAbsoluteLayout() {
        final AbsoluteLayout al = new AbsoluteLayout();
        al.setCaption("AbsoluteLayout");
        al.setStyleName("borders");
        al.setWidth("300px");
        al.setHeight("500px");
        al.addComponent(new TextField("This is its caption",
                "This is a textfield"), "top: 60px; left: 0px; width: 100px;");
        al.addComponent(new TextField("Another textfield caption",
                "This is another textfield"),
                "top: 120px; left: 20px; width: 100px;");

        al.addComponent(new Button("A button with its own click listener",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        log("Button " + event.getButton().getCaption()
                                + " was clicked");

                    }
                }));
        al.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                logLayoutClick("AbsoluteLayout", event);
            }
        });

        return al;

    }

    private Component createClickableCSSLayout() {
        final CssLayout cl = new CssLayout();
        cl.setCaption("CSSLayout");
        cl.setStyleName("borders");
        cl.setWidth("300px");
        cl.setHeight("500px");
        cl.addComponent(new TextField("This is its caption",
                "This is a textfield"));
        cl.addComponent(new TextField("Another textfield caption",
                "This is another textfield"));

        cl.addComponent(new Button("A button with its own click listener",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(
                            com.vaadin.ui.Button.ClickEvent event) {
                        log("Button " + event.getButton().getCaption()
                                + " was clicked");

                    }
                }));
        cl.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                logLayoutClick("CSSLayout", event);
            }
        });

        return cl;

    }

    private Layout createClickableGridLayout() {

        GridLayout gl = new GridLayout(4, 4);
        gl.setHeight("400px");
        gl.setWidth("564px");
        gl.setStyleName("borders");
        gl.setSpacing(true);
        gl.setHideEmptyRowsAndColumns(true);
        addContent(gl, 4);
        TextArea largeTextarea = new TextArea("Large textarea");
        largeTextarea.setWidth("100%");
        largeTextarea.setHeight("99%");
        gl.addComponent(largeTextarea, 0, 3, 3, 3);

        gl.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                logLayoutClick("GridLayout", event);
            }
        });
        gl.setRowExpandRatio(3, 1);
        return wrap(gl, "GridLayout");
    }

    protected void logLayoutClick(String layout, LayoutClickEvent event) {
        String target = "&lt;none>";
        Component component = event.getChildComponent();
        if (component != null) {
            target = component.getCaption();
            if (target == null && component instanceof Label) {
                target = ((Label) component).getValue().toString();
            }
        }
        String button = event.getButtonName();
        String type = "click";
        if (event.isDoubleClick()) {
            type = "double-click";
        }
        log(layout + ": " + button + " " + type + " on " + target);
    }

    private Layout createClickableVerticalLayout() {

        VerticalLayout gl = new VerticalLayout();
        addContent(gl, 5);

        gl.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                logLayoutClick("VerticalLayout", event);

            }
        });

        return wrap(gl, "Clickable VerticalLayout");
    }

    private void addContent(Layout gl, int nr) {
        for (int i = 1; i < nr; i++) {
            Label l = new Label("This is label " + i);
            l.setWidth(null);
            gl.addComponent(l);
        }
        for (int i = nr; i < nr * 2; i++) {
            gl.addComponent(new TextField("This is tf" + i, "this is tf " + i));
        }
    }

    private Layout wrap(Component c, String caption) {
        VerticalLayout vl = new VerticalLayout();
        Label l = new Label(caption);
        l.setWidth(null);
        vl.addComponent(l);
        vl.addComponent(c);

        return vl;
    }

    @Override
    protected String getTestDescription() {
        return "All layouts have click listeners attached and the events are shown in the event log at the top";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3541;
    }
}
