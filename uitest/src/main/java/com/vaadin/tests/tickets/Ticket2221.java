package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2221 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        layout.setSizeFull();
        layout.addComponent(new Invoice());
    }

    public class Invoice extends CustomComponent {

        Layout main = new VerticalLayout();

        private TextField tf;

        private Panel outerPanel;

        private TextField tf2;

        public Invoice() {
            setSizeFull();

            setCompositionRoot(main);
            main.setSizeFull();
            Button b = new Button("Switch textfield/panel",
                    new ClickListener() {

                        @Override
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

            VerticalLayout outerLayout = new VerticalLayout();
            outerLayout.setMargin(true);
            outerPanel = new Panel(outerLayout);
            outerPanel.setCaption("A RichTextArea");
            outerPanel.setVisible(false);
            outerPanel.setHeight("1000px");
            outerPanel.setWidth("1000px");

            outerLayout.setSizeFull();
            VerticalLayout innerLayout = new VerticalLayout();
            innerLayout.setMargin(true);
            Panel innerPanel = new Panel("Inner panel", innerLayout);
            innerPanel.setSizeFull();
            outerLayout.addComponent(innerPanel);

            tf2 = new TextField("A 2000x2000 textfield");
            tf2.setWidth("2000px");
            tf2.setHeight("2000px");

            innerLayout.addComponent(tf2);
            main.addComponent(outerPanel);
            main.addComponent(tf);
        }

    }

}
