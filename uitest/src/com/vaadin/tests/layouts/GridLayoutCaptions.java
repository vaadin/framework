package com.vaadin.tests.layouts;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutCaptions extends TestBase {

    class CustomForm extends Form {
        private com.vaadin.ui.GridLayout layout;

        private VerticalLayout wrapper = new VerticalLayout();
        private CssLayout wrapper2 = new CssLayout();

        private FormFieldFactory fff = new FormFieldFactory() {

            @Override
            public Field<?> createField(Item item, Object propertyId,
                    Component uiContext) {

                if (propertyId.equals(DataPOJO.Fields.name.name())) {
                    Field<?> f = DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                    f.setCaption("This is a long caption for the name field");
                    return f;

                } else if (propertyId.equals(DataPOJO.Fields.hp.name())) {
                    Field<?> f = DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                    f.setCaption("This is a long caption for the HP field, but it has a VL as a wrapper");

                    return f;

                } else if (propertyId.equals(DataPOJO.Fields.place.name())) {
                    Field<?> f = DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                    f.setCaption("This is a long caption for the Place field, but it has a CSSLo as a wrapper");

                    return f;

                } else if (propertyId.equals(DataPOJO.Fields.price.name())) {
                    Field<?> f = DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                    f.setCaption("With size undefined the caption behaves like this...");
                    f.setSizeFull();

                    return f;

                } else {
                    return DefaultFieldFactory.get().createField(item,
                            propertyId, uiContext);
                }
            }
        };

        public CustomForm() {
            super();
            layout = new GridLayout(3, 3);
            layout.addComponent(wrapper, 1, 0);
            layout.addComponent(wrapper2, 2, 0);
            layout.setSpacing(true);

            setLayout(layout);
            setFormFieldFactory(fff);

            Label l = new Label("A label with caption");
            l.setCaption("A really long caption that is clipped");

            layout.addComponent(l, 0, 2);

            Label l2 = new Label("A wrapped label with caption");
            l2.setCaption("A really long caption that is not clipped");

            VerticalLayout vl = new VerticalLayout();
            vl.addComponent(l2);

            layout.addComponent(vl, 1, 2);

        }

        public void createErrors() {
            Validator.InvalidValueException ive = new Validator.InvalidValueException(
                    "Ipsum lipsum laarum lop... ");

            for (Object propIDs : getItemDataSource().getItemPropertyIds()) {
                ((TextField) getField(propIDs))
                        .setComponentError(AbstractErrorMessage
                                .getErrorMessageForException(ive));

            }

        }

        public void clearErrors() {
            for (Object propIDs : getItemDataSource().getItemPropertyIds()) {
                ((TextField) getField(propIDs)).setComponentError(null);

            }
        }

        @Override
        protected void attachField(Object propertyId, Field field) {

            if (propertyId.equals(DataPOJO.Fields.name.name())) {
                layout.addComponent(field, 0, 0);

            } else if (propertyId.equals(DataPOJO.Fields.hp.name())) {
                wrapper.removeAllComponents();
                wrapper.addComponent(field);
            } else if (propertyId.equals(DataPOJO.Fields.place.name())) {
                wrapper2.removeAllComponents();
                wrapper2.addComponent(field);
            } else if (propertyId.equals(DataPOJO.Fields.price.name())) {
                layout.addComponent(field, 0, 1);
            }

        }
    }

    public static class DataPOJO {

        public enum Fields {
            name, price, hp, place;
        }

        private String name;
        private int price;
        private String hp;
        private String place;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getHp() {
            return hp;
        }

        public void setHp(String hp) {
            this.hp = hp;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

    }

    @Override
    protected void setup() {
        LegacyWindow mainWindow = getMainWindow();

        Label label = new Label("Hello Vaadin user");
        mainWindow.addComponent(label);

        DataPOJO forDemo = new DataPOJO();

        BeanItem<DataPOJO> bi = new BeanItem<DataPOJO>(forDemo);

        final CustomForm aFormWithGl = new CustomForm();

        aFormWithGl.setItemDataSource(bi);

        mainWindow.addComponent(aFormWithGl);

        Button b = new Button("Give me an error!", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                aFormWithGl.createErrors();

            }
        });
        mainWindow.addComponent(b);

        Button b2 = new Button("Get rid of an error!",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        aFormWithGl.clearErrors();

                    }
                });
        mainWindow.addComponent(b2);

    }

    @Override
    protected String getDescription() {
        return "Captions in Gridlayout behaves differently than in other layouts";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5424;
    }

}
