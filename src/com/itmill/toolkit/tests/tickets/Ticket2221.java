package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2221 extends Application {

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
