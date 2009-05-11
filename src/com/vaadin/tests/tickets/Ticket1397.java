package com.vaadin.tests.tickets;

import java.util.Date;

import com.vaadin.Application;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket1397 extends Application {

    Window main;

    @Override
    public void init() {
        setTheme("default");
        main = new Window("PopupView test");
        setMainWindow(main);
        Panel panel = new Panel("PopupTest");

        // First test component
        final ObjectProperty prop = new ObjectProperty("fooTextField");

        PopupView.Content content = new PopupView.Content() {
            public String getMinimizedValueAsHTML() {
                return prop.toString();
            }

            public Component getPopupComponent() {
                return new TextField("Edit foo", prop);
            }
        };

        PopupView pe = new PopupView(content);
        pe.setDescription("Click to edit");
        panel.addComponent(pe);

        // Second test component
        PopupView pe2 = new PopupView("fooLabel", new Label("Foooooooooo..."));
        pe2.setDescription("Click to view");
        panel.addComponent(pe2);

        // Third test component
        final ObjectProperty prop2 = new ObjectProperty(new StringBuffer(
                "Text for button"));

        class myButton extends Button {
            public myButton() {
                super("Reverse the property");
                this.addListener(new Button.ClickListener() {
                    public void buttonClick(Button.ClickEvent event) {
                        StringBuffer getContents = (StringBuffer) prop2
                                .getValue();
                        getContents.reverse();

                    }
                });
            }
        }

        final Panel panel2 = new Panel("Editor with a button");
        panel2.addComponent(new myButton());
        PopupView.Content content2 = new PopupView.Content() {
            public String getMinimizedValueAsHTML() {
                return prop2.toString();
            }

            public Component getPopupComponent() {
                return panel2;
            }
        };

        PopupView p3 = new PopupView(content2);
        panel.addComponent(p3);

        // Fourth test component
        final Panel panel3 = new Panel("Editor popup for a property");
        TextField tf2 = new TextField("TextField for editing a property");
        final ObjectProperty op = new ObjectProperty("This is property text.");
        tf2.setPropertyDataSource(op);
        panel3.addComponent(tf2);
        PopupView.Content content3 = new PopupView.Content() {

            public String getMinimizedValueAsHTML() {
                return op.toString();
            }

            public Component getPopupComponent() {
                return panel3;
            }

        };
        PopupView p4 = new PopupView(content3);
        panel.addComponent(p4);

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
                public String getMinimizedValueAsHTML() {
                    return df.toString();
                }

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

                public String getMinimizedValueAsHTML() {
                    return "" + tf.toString().length() + " characters of info";
                }

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
