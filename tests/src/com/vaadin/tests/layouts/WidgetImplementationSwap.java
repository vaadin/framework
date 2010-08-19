package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class WidgetImplementationSwap extends TestBase {

    protected void setup() {
        setTheme("tests-tickets");

        {
            final AbstractLayout layout = new AbsoluteLayout();
            layout.setCaption(layout.getClass().getSimpleName());
            layout.setStyleName("borders");
            layout.setWidth("500px");
            layout.setHeight("50px");
            addComponent(layout);
            final TextField tf = new TextField();
            layout.addComponent(tf);
            Button b = new Button("-> TextArea", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (tf.getRows() == 0) {
                        tf.setRows(3);
                        event.getButton().setCaption("Move");
                    } else {
                        layout.setCaption(layout.getClass().getSimpleName()
                                + " done");
                        event.getButton().setCaption("done");
                    }

                }
            });
            addComponent(b);
        }
        {
            final AbstractLayout layout = new VerticalLayout();
            layout.setCaption(layout.getClass().getSimpleName());
            layout.setStyleName("borders");
            layout.setWidth("500px");
            layout.setHeight("50px");
            addComponent(layout);
            final TextField tf = new TextField();
            layout.addComponent(tf);
            Button b = new Button("-> TextArea", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (tf.getRows() == 0) {
                        tf.setRows(3);
                        event.getButton().setCaption("Move");
                    } else {
                        layout.setCaption(layout.getClass().getSimpleName()
                                + " done");
                        event.getButton().setCaption("done");
                    }

                }
            });
            addComponent(b);
        }

        {
            final AbstractLayout layout = new HorizontalLayout();
            layout.setCaption(layout.getClass().getSimpleName());
            layout.setStyleName("borders");
            layout.setWidth("500px");
            layout.setHeight("50px");
            addComponent(layout);
            final TextField tf = new TextField();
            layout.addComponent(tf);
            Button b = new Button("-> TextArea", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (tf.getRows() == 0) {
                        tf.setRows(3);
                        event.getButton().setCaption("Move");
                    } else {
                        layout.setCaption(layout.getClass().getSimpleName()
                                + " done");
                        event.getButton().setCaption("done");
                    }

                }
            });
            addComponent(b);
        }

        {
            final AbstractLayout layout = new GridLayout();
            layout.setCaption(layout.getClass().getSimpleName());
            layout.setStyleName("borders");
            layout.setWidth("500px");
            layout.setHeight("50px");
            addComponent(layout);
            final TextField tf = new TextField();
            layout.addComponent(tf);
            Button b = new Button("-> TextArea", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (tf.getRows() == 0) {
                        tf.setRows(3);
                        event.getButton().setCaption("Move");
                    } else {
                        layout.setCaption(layout.getClass().getSimpleName()
                                + " done");
                        event.getButton().setCaption("done");
                    }

                }
            });
            addComponent(b);
        }

        {
            final AbstractLayout layout = new CssLayout();
            layout.setCaption(layout.getClass().getSimpleName());
            layout.setStyleName("borders");
            layout.setWidth("500px");
            layout.setHeight("50px");
            addComponent(layout);
            final TextField tf = new TextField();
            layout.addComponent(tf);
            Button b = new Button("-> TextArea", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (tf.getRows() == 0) {
                        tf.setRows(3);
                        event.getButton().setCaption("Move");
                    } else {
                        layout.setCaption(layout.getClass().getSimpleName()
                                + " done");
                        event.getButton().setCaption("done");
                    }

                }
            });
            addComponent(b);
        }

    }

    protected String getDescription() {
        return "First click turns TextField into a TextArea (on the client); second click modifies the layout - widget should still be a TextArea.";
    }

    protected Integer getTicketNumber() {
        return 5457;
    }

}
