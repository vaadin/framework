package com.itmill.toolkit.demo.sampler.features.form;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.data.validator.StringLengthValidator;
import com.itmill.toolkit.demo.sampler.ExampleUtil;
import com.itmill.toolkit.ui.BaseFieldFactory;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FormPojoExample extends VerticalLayout {

    // the 'POJO' we're editing
    Person person;

    public FormPojoExample() {

        person = new Person(); // a person POJO
        BeanItem personItem = new BeanItem(person); // item from POJO

        // create the Form
        final Form personForm = new Form();
        personForm.setWriteThrough(false); // we want explicit 'apply'
        personForm.setInvalidCommitted(false); // no invalid values in datamodel
        // FieldFactory for customizing the fields and adding validators
        personForm.setFieldFactory(new PersonFieldFactory());
        personForm.setItemDataSource(personItem); // bind to POJO via BeanItem
        // determines which properties are shown, and in which order:
        personForm.setVisibleItemProperties(Arrays.asList(new String[] {
                "firstName", "lastName", "countryCode", "password",
                "birthdate", "shoesize", "uuid" }));
        addComponent(personForm); // add to layout

        // the cancel / apply buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button discardChanges = new Button("Discard changes",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        personForm.discard();
                    }
                });
        discardChanges.setStyleName(Button.STYLE_LINK);
        buttons.addComponent(discardChanges);
        Button apply = new Button("Apply", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                personForm.commit();
            }
        });
        buttons.addComponent(apply);
        personForm.getLayout().addComponent(buttons);

        // button for showing the internal state of the POJO
        Button showPojoState = new Button("Show POJO internal state",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        showPojoState();
                    }
                });
        addComponent(showPojoState);
        // showPojoState();
    }

    private void showPojoState() {
        Window.Notification n = new Window.Notification("POJO state",
                Window.Notification.TYPE_TRAY_NOTIFICATION);
        n.setPosition(Window.Notification.POSITION_CENTERED);
        n.setDescription("First name: " + person.getFirstName()
                + "<br/>Last name: " + person.getLastName() + "<br/>Country: "
                + person.getCountryCode() + "<br/>Birthdate: "
                + person.getBirthdate() + "<br/>Shoe size: "
                + +person.getShoesize() + "<br/>Password: "
                + person.getPassword() + "<br/>UUID: " + person.getUuid());
        getWindow().showNotification(n);
    }

    private class PersonFieldFactory extends BaseFieldFactory {

        final ComboBox countries = new ComboBox("Country");

        public PersonFieldFactory() {
            countries.setWidth("30em");
            countries.setContainerDataSource(ExampleUtil.getISO3166Container());
            countries
                    .setItemCaptionPropertyId(ExampleUtil.iso3166_PROPERTY_NAME);
            countries.setItemIconPropertyId(ExampleUtil.iso3166_PROPERTY_FLAG);
            countries.setFilteringMode(ComboBox.FILTERINGMODE_STARTSWITH);
        }

        @Override
        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            if ("countryCode".equals(propertyId)) {
                // filtering ComboBox w/ country names
                return countries;
            }
            Field f = super.createField(item, propertyId, uiContext);
            if ("firstName".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setRequired(true);
                tf.setWidth("15em");
                tf.addValidator(new StringLengthValidator(
                        "First Name must be 3-25 characters", 3, 25, false));
            } else if ("lastName".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setRequired(true);
                tf.setWidth("20em");
                tf.addValidator(new StringLengthValidator(
                        "Last Name must be 3-50 characters", 3, 50, false));
            } else if ("password".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setSecret(true);
                tf.setRequired(true);
                tf.setWidth("10em");
                tf.addValidator(new StringLengthValidator(
                        "Password must be 6-20 characters", 6, 20, false));
            } else if ("shoesize".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setWidth("2em");
            } else if ("uuid".equals(propertyId)) {
                TextField tf = (TextField) f;
                tf.setWidth("20em");
            }

            return f;
        }
    }

    public class Person {
        private String firstName = "";
        private String lastName = "";
        private Date birthdate;
        private int shoesize = 42;
        private String password = "";
        private UUID uuid;
        private String countryCode = "";

        public Person() {
            uuid = UUID.randomUUID();
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

        public Date getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
        }

        public int getShoesize() {
            return shoesize;
        }

        public void setShoesize(int shoesize) {
            this.shoesize = shoesize;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

    }
}
