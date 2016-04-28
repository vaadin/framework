package com.vaadin.tests.tickets;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket736 extends LegacyApplication {

    Address address = new Address();

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow("Test app for #736");
        setMainWindow(mainWin);

        setTheme("runo");

        // Create form for editing address
        final Form f = new Form();
        f.setItemDataSource(new BeanItem<Address>(address, new String[] {
                "name", "street", "zip", "city", "state", "country" }));
        f.setCaption("Office address");
        f.setIcon(new ThemeResource("../runo/icons/16/document.png"));
        f.setDescription("Jep jpe, this is form description.");
        mainWin.addComponent(f);

        // Select to use buffered mode for editing to enable commit and discard
        f.setBuffered(true);
        Button commit = new Button("Commit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                f.commit();
            }
        });
        Button discard = new Button("Discard", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                f.discard();
            }

        });
        HorizontalLayout ol = new HorizontalLayout();
        ol.setHeight("3em");
        ol.addComponent(commit);
        ol.setComponentAlignment(commit, Alignment.TOP_RIGHT);
        ol.addComponent(discard);
        f.setFooter(ol);

        // Add some validators for the form
        f.getField("zip").addValidator(
                new IntegerValidator("'{0}' is not a number"));
        ((AbstractComponent) f.getField("zip")).setDescription("Jepjep");
        ((AbstractComponent) f.getField("zip")).setIcon(new ThemeResource(
                "../runo/icons/16/folder.png"));
        f.getField("state").addValidator(new IsValidState());
        f.getField("name").setRequired(true);
        f.getField("street").setRequired(true);
        f.getField("city").setRequired(true);
        f.getField("zip").setRequired(true);

        // Debug form properties
        final VerticalLayout formPropertiesLayout = new VerticalLayout();
        formPropertiesLayout.setMargin(true);
        final Panel formProperties = new Panel("Form properties",
                formPropertiesLayout);
        formProperties.setWidth("200px");
        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "writeThrough", "invalidCommitted",
                "validationVisible", "immediate" };
        for (int i = 0; i < visibleProps.length; i++) {
            CheckBox b = new CheckBox(visibleProps[i],
                    new MethodProperty<Boolean>(f, visibleProps[i]));
            b.setImmediate(true);
            formPropertiesLayout.addComponent(b);
        }
        mainWin.addComponent(formProperties);

        // Debug the internal state of the address-object
        mainWin.addComponent(new Button("Show state of the address object",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        mainWin.showNotification(address.toString());
                    }
                }));
    }

    /** Address pojo. */
    public static class Address {
        String name = "";
        String street = "";
        String zip = "";
        String city = "";
        String state = "";
        String country = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        @Override
        public String toString() {
            return name + "; " + street + "; " + city + " " + zip
                    + (state != null ? " " + state : "") + " " + country;
        }

    }

    /** Simple state validator */
    static class IsValidState implements Validator {

        @Override
        public void validate(Object value) throws InvalidValueException {
            // Empty and null are accepted values
            if (value == null || "".equals("" + value)) {
                return;
            }

            // Otherwise state must be two capital letter combo
            if (value.toString().length() != 2
                    || !value.toString().equals(("" + value).toUpperCase())) {
                throw new InvalidValueException(
                        "State must be either two capital letter abreviation or left empty");
            }
        }
    }
}
