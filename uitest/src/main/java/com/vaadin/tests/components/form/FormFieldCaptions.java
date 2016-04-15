package com.vaadin.tests.components.form;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;

public class FormFieldCaptions extends TestBase {

    @Override
    protected void setup() {
        // Method 1
        Form form1 = new Form();
        Item item1 = createItem();
        for (Object propertyId : item1.getItemPropertyIds()) {
            form1.addItemProperty(propertyId, item1.getItemProperty(propertyId));
        }

        // Method 2

        Form form2 = new Form();
        Item item2 = createItem();
        form2.setItemDataSource(item2);

        // Layout
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(form1);
        hl.addComponent(form2);

        addComponent(hl);
    }

    private Item createItem() {
        return new BeanItem<Person>(new Person("John", "Doe", 38));
    }

    public class Person {
        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName, int age) {
            super();
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        private int age;
    }

    @Override
    protected String getDescription() {
        return "The two forms generated using different methods should have the same captions for all fields";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3554;
    }

}
