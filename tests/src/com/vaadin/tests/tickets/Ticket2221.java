package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2221 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        layout.setSizeFull();
        layout.addComponent(new Invoice());
    }

    public class Invoice extends CustomComponent {

        Layout main = new OrderedLayout();

        private TextField tf;

        private Panel outerPanel;

        private TextField tf2;

        public Invoice() {
            setSizeFull();

            setCompositionRoot(main);
            main.setSizeFull();
            Button b = new Button("Switch textfield/panel",
                    new ClickListener() {

                        public void buttonClick(ClickEvent event) {
                            Component visible = tf;

                            if (tf.isVisible()) {
                                visible = outerPanel;
                            }

                            outerPanel.setVisible(false);
                            tf.setVisible(false);

                            visible.setVisible(true);
                        }

                    });
            main.addComponent(b);

            tf = new TextField("TextField");
            tf.setHeight("1000px");
            tf.setWidth("1000px");

            outerPanel = new Panel();
            outerPanel.setCaption("A RichTextArea");
            outerPanel.setVisible(false);
            outerPanel.setHeight("1000px");
            outerPanel.setWidth("1000px");

            outerPanel.getLayout().setSizeFull();
            Panel innerPanel = new Panel("Inner panel");
            innerPanel.setSizeFull();
            outerPanel.addComponent(innerPanel);

            tf2 = new TextField("A 2000x2000 textfield");
            tf2.setWidth("2000px");
            tf2.setHeight("2000px");

            innerPanel.addComponent(tf2);
            main.addComponent(outerPanel);
            main.addComponent(tf);
        }

    }

}
