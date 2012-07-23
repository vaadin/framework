package com.vaadin.tests.components.form;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;

public class FormWithPropertyFormatterConnected extends TestBase {
    @Override
    protected void setup() {
        Form form2 = new Form();
        form2.setFormFieldFactory(new FormFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                AbstractField f = (AbstractField) DefaultFieldFactory.get()
                        .createField(item, propertyId, uiContext);
                if (propertyId.equals("age")) {
                    f.setPropertyDataSource(new PropertyFormatter() {

                        @Override
                        public Object parse(String formattedValue)
                                throws Exception {
                            String str = formattedValue.replaceAll("[^0-9.]",
                                    "");
                            if (formattedValue.toLowerCase().contains("months")) {
                                return Double.parseDouble(str) / 12;
                            }
                            return Double.parseDouble(str);
                        }

                        @Override
                        public String format(Object value) {
                            Double dValue = (Double) value;
                            if (dValue < 1) {
                                return ((int) (dValue * 12)) + " months";
                            }
                            return dValue + " years";
                        }
                    });
                    f.setImmediate(true);
                }
                return f;
            }
        });
        form2.setItemDataSource(createItem());

        addComponent(form2);
        addComponent(new Button("B"));
    }

    private Item createItem() {
        return new BeanItem<Person>(new Person(0.5));
    }

    public class Person {
        public Person(double age) {
            super();
            this.age = age;
        }

        public double getAge() {
            return age;
        }

        public void setAge(double age) {
            this.age = age;
        }

        private double age;
    }

    @Override
    protected String getDescription() {
        return "It should be possible to inject PropertyFormatter and similar classses to fields in form. The test app hooks formatter that displays age in years or months and also accepts value in both (years by default, months if mentioned in the field)";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
