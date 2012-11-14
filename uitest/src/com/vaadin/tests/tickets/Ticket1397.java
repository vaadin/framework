package com.vaadin.tests.tickets;

import java.util.Date;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket1397 extends LegacyApplication {

    LegacyWindow main;

    @Override
    public void init() {
        setTheme("runo");
        main = new LegacyWindow("PopupView test");
        setMainWindow(main);
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel panel = new Panel("PopupTest", panelLayout);

        // First test component
        final ObjectProperty<String> prop = new ObjectProperty<String>(
                "fooTextField");

        PopupView.Content content = new PopupView.Content() {
            @Override
            public String getMinimizedValueAsHTML() {
                return String.valueOf(prop.getValue());
            }

            @Override
            public Component getPopupComponent() {
                return new TextField("Edit foo", prop);
            }
        };

        PopupView pe = new PopupView(content);
        pe.setDescription("Click to edit");
        panelLayout.addComponent(pe);

        // Second test component
        PopupView pe2 = new PopupView("fooLabel", new Label("Foooooooooo..."));
        pe2.setDescription("Click to view");
        panelLayout.addComponent(pe2);

        // Third test component
        final ObjectProperty<StringBuffer> prop2 = new ObjectProperty<StringBuffer>(
                new StringBuffer("Text for button"));

        class myButton extends Button {
            public myButton() {
                super("Reverse the property");
                this.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        StringBuffer getContents = prop2.getValue();
                        getContents.reverse();

                    }
                });
            }
        }

        VerticalLayout panel2Layout = new VerticalLayout();
        panel2Layout.setMargin(true);
        final Panel panel2 = new Panel("Editor with a button", panel2Layout);
        panel2Layout.addComponent(new myButton());
        PopupView.Content content2 = new PopupView.Content() {
            @Override
            public String getMinimizedValueAsHTML() {
                return String.valueOf(prop2.getValue());
            }

            @Override
            public Component getPopupComponent() {
                return panel2;
            }
        };

        PopupView p3 = new PopupView(content2);
        panelLayout.addComponent(p3);

        // Fourth test component
        VerticalLayout panel3Layout = new VerticalLayout();
        panel3Layout.setMargin(true);
        final Panel panel3 = new Panel("Editor popup for a property",
                panel3Layout);
        TextField tf2 = new TextField("TextField for editing a property");
        final ObjectProperty<String> op = new ObjectProperty<String>(
                "This is property text.");
        tf2.setPropertyDataSource(op);
        panel3Layout.addComponent(tf2);
        PopupView.Content content3 = new PopupView.Content() {

            @Override
            public String getMinimizedValueAsHTML() {
                return String.valueOf(op.getValue());
            }

            @Override
            public Component getPopupComponent() {
                return panel3;
            }

        };
        PopupView p4 = new PopupView(content3);
        panelLayout.addComponent(p4);

        // Fifth test component
        Table table = new Table("Table for testing purposes");
        for (int i = 0; i < 5; i++) {
            table.addContainerProperty("" + (i + 1), String.class, "");
        }
        table.addContainerProperty("" + 6, PopupView.class, null);
        table.addContainerProperty("" + 7, PopupView.class, null);
        table.setPageLength(20);
        for (int i = 0; i < 1000; i++) {

            final InlineDateField df = new InlineDateField("", new Date());
            PopupView pp = new PopupView(new PopupView.Content() {
                @Override
                public String getMinimizedValueAsHTML() {
                    return String.valueOf(df.getValue());
                }

                @Override
                public Component getPopupComponent() {
                    return df;
                }
            });
            final int lineNum = i;
            PopupView pp2 = new PopupView(new PopupView.Content() {

                TextField tf = new TextField("Editor for line " + lineNum,

                "Try to edit the contents for this textfield on line "
                        + lineNum
                        + " and see how the overview-version changes.");

                @Override
                public String getMinimizedValueAsHTML() {
                    return "" + String.valueOf(tf.getValue()).length()
                            + " characters of info";
                }

                @Override
                public Component getPopupComponent() {
                    return tf;
                }

            });
            table.addItem(new Object[] { "1 " + i, "2 " + i, "3 " + i,
                    "4 " + i, "5 " + i, pp, pp2 }, new Integer(i));
        }

        main.addComponent(table);
        main.addComponent(panel);
    }
}
