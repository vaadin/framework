package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.ComponentContainer;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket677 extends Application {

    private static final Label info = new Label(
            "<li> keep debug window open to see variable changes"
                    + "<li> disable root panel w/ toggle button"
                    + "<li> toggle one of the subpanels"
                    + "<li> we attempt to focus the subpanels first textfield"
                    + "<li> focusing should fail (try tabbing as well) [worked previousy]"
                    + "<li> no variable changes should be sent from disabled fields [changed sent previously]"
                    + "<li> try further toggling and tabbing around",
            Label.CONTENT_RAW);

    Panel root = new Panel("Enabled");
    Panel one = new Panel("Enabled");
    Panel two = new Panel("Enabled");
    Form form;
    Table table;

    public void init() {
        Window main = new Window();
        setMainWindow(main);

        main.addComponent(info);

        OrderedLayout l = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        main.addComponent(l);

        l.addComponent(new Button("Toggle root panel",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        toggle(root);
                    }
                }));
        l.addComponent(new Button("Toggle panel one",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        toggle(one);
                    }
                }));
        l.addComponent(new Button("Toggle panel two",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        toggle(two);
                    }
                }));
        l.addComponent(new Button("Toggle form", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                toggle(form);
            }
        }));
        l.addComponent(new Button("Toggle table", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                toggle(table);
            }
        }));

        root.setLayout(new GridLayout(2, 3));
        main.addComponent(root);

        TextField tf = new TextField("Enabled");
        tf.setImmediate(true);
        root.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        root.addComponent(tf);

        root.addComponent(one);
        tf = new TextField("Enabled");
        tf.setImmediate(true);
        one.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        one.addComponent(tf);

        root.addComponent(two);
        tf = new TextField("Enabled");
        tf.setImmediate(true);
        two.addComponent(tf);
        tf = new TextField("Disabled");
        tf.setImmediate(true);
        tf.setEnabled(false);
        two.addComponent(tf);

        form = new Form();
        form.setCaption("Enabled");
        form.setFieldFactory(new BaseFieldFactory() {

            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                Field f = super.createField(item, propertyId, uiContext);
                f.setEnabled(!"disabled".equals(propertyId));
                return f;
            }

        });
        form.setItemDataSource(new BeanItem(new MyBean()));
        root.addComponent(form);

        table = new Table("Enabled");
        table.addContainerProperty("Text", String.class, null);
        for (int i = 0; i < 5; i++) {
            Item item = table.addItem("Item" + i);
            Property p = item.getItemProperty("Text");
            p.setValue(i > 1 ? "enabled" : "disabled");
        }

        table.setFieldFactory(new BaseFieldFactory() {

            public Field createField(Container container, Object itemId,
                    Object propertyId, Component uiContext) {
                Field f = super.createField(container, itemId, propertyId,
                        uiContext);
                Item item = container.getItem(itemId);
                Property p = item.getItemProperty(propertyId);
                if ("disabled".equals(p.getValue())) {
                    f.setEnabled(false);
                }
                return f;
            }

        });
        table.setEditable(true);
        root.addComponent(table);

    }

    private void toggle(Component c) {
        boolean enable = "Disabled".equals(c.getCaption());
        c.setEnabled(enable);
        c.setCaption((enable ? "Enabled" : "Disabled"));
        if (c instanceof ComponentContainer) {
            TextField tf = (TextField) ((ComponentContainer) c)
                    .getComponentIterator().next();
            tf.focus();
        }
    }

    class MyBean {
        boolean on = false;
        int number = 1;
        String rw = "read/write";
        String r = "read";
        String disabled = "disabled";

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getRw() {
            return rw;
        }

        public void setRw(String rw) {
            this.rw = rw;
        }

        public String getDisabled() {
            return disabled;
        }

        public void setDisabled(String disabled) {
            this.disabled = disabled;
        }

        public String getR() {
            return r;
        }

    }
}
