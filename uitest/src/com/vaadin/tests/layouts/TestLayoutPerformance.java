package com.vaadin.tests.layouts;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TestLayoutPerformance extends TestBase {
    private NativeSelect ns;
    private int i;
    private NativeSelect ns2;
    private VerticalLayout testarea = new VerticalLayout();

    @Override
    protected String getDescription() {
        return "Test app to test simple rendering to various layouts.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected void setup() {
        Label label = new Label("<h1>CssLayout performance test.</h1>",
                ContentMode.HTML);
        getLayout().addComponent(label);

        label = new Label(
                "<em>Hint</em>. Use debug dialog to measure rendering times TODO: extend with size settings (to both layout and content).",
                ContentMode.HTML);
        getLayout().addComponent(label);

        ns = new NativeSelect("Select component to test");
        ns.addItem(CssLayout.class);
        ns.addItem(GridLayout.class);
        ns.addItem(VerticalLayout.class);
        ns.setNullSelectionAllowed(false);
        ns.setValue(CssLayout.class);

        ns2 = new NativeSelect("Select component to render inside layout.");
        ns2.addItem(Label.class);
        ns2.addItem(Button.class);
        ns2.setNullSelectionAllowed(false);
        ns2.setValue(Label.class);

        final TextField n = new TextField("Number of components");

        n.setValue("1000");

        final CheckBox cb = new CheckBox("Generate captions", false);

        Button b = new Button("Render component");

        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                int components = Integer.parseInt(n.getValue());
                Layout layout = getCurrentLayout();
                for (int i = 0; i < components; i++) {
                    Component component = newTestComponent();
                    if (cb.getValue()) {
                        component.setCaption("caption " + i);
                    }
                    layout.addComponent(component);
                }

                testarea.removeAllComponents();
                testarea.addComponent(layout);
            }

        });

        getLayout().addComponent(ns);
        getLayout().addComponent(ns2);
        getLayout().addComponent(n);
        getLayout().addComponent(cb);
        getLayout().addComponent(b);
        getLayout().addComponent(testarea);

    }

    private Layout getCurrentLayout() {
        Class<?> value = (Class<?>) ns.getValue();
        if (value == GridLayout.class) {
            return new GridLayout(10, 1);
        }

        try {
            return (Layout) value.newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private Component newTestComponent() {
        Class<?> componentClass = (Class<?>) ns2.getValue();
        AbstractComponent newInstance = null;
        try {
            newInstance = (AbstractComponent) componentClass.newInstance();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (componentClass == Label.class) {
            ((Label) newInstance).setValue("Test l " + (i++));
            ((Label) newInstance).setSizeUndefined();
        } else {
            newInstance.setCaption("Test l " + (i++));
        }
        return newInstance;
    }

}
