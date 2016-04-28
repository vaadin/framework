package com.vaadin.tests.components.customfield;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class BooleanFieldExample extends TestBase {

    /**
     * Data model class with two boolean fields.
     */
    public static class TwoBooleans {
        private boolean normal;
        private boolean custom;

        public void setNormal(boolean normal) {
            this.normal = normal;
        }

        public boolean isNormal() {
            return normal;
        }

        public void setCustom(boolean custom) {
            this.custom = custom;
        }

        public boolean isCustom() {
            return custom;
        }
    }

    @Override
    protected void setup() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        final Form form = new Form();
        form.setFooter(null);
        form.setFormFieldFactory(new DefaultFieldFactory() {
            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                if ("custom".equals(propertyId)) {
                    return new BooleanField();
                }
                return super.createField(item, propertyId, uiContext);
            }
        });
        final TwoBooleans data = new TwoBooleans();
        form.setItemDataSource(new BeanItem<TwoBooleans>(data));

        layout.addComponent(form);

        Button submit = new Button("Submit", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                form.commit();
                Notification.show("The custom boolean field value is "
                        + data.isCustom() + ".\n"
                        + "The checkbox (default boolean field) value is "
                        + data.isNormal() + ".");
            }
        });
        layout.addComponent(submit);

        addComponent(layout);
    }

    @Override
    protected String getDescription() {
        return "A customized field (a two-state button) for editing a boolean value.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
