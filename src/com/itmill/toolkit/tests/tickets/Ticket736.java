package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket736 extends Application {

    Address address = new Address();

    public void init() {

        final Window mainWin = new Window("Test app for #736");
        setMainWindow(mainWin);

        // Create form for editing address
        final Form f = new Form();
        f.setItemDataSource(new BeanItem(address, new String[] { "name",
                "street", "zip", "city", "state", "country" }));
        f.setCaption("Office address");
        mainWin.addComponent(f);

        // Select to use buffered mode for editing to enable commit and discard
        f.setWriteThrough(false);
        f.setReadThrough(false);
        Button commit = new Button("Commit", f, "commit");
        Button discard = new Button("Discard", f, "discard");
        OrderedLayout ol = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.addComponent(commit);
        ol.addComponent(discard);
        mainWin.addComponent(ol);

        // Add some validators for the form
        f.getField("zip").addValidator(new IsInteger());
        f.getField("state").addValidator(new IsValidState());
        f.getField("name").addValidator(new IsNotEmpty());
        f.getField("street").addValidator(new IsNotEmpty());
        f.getField("city").addValidator(new IsNotEmpty());

        // Debug form properties
        final Panel formProperties = new Panel("Form properties");
        formProperties.setWidth(200);
        final String[] visibleProps = { "required", "invalidAllowed",
                "readOnly", "readThrough", "writeThrough", "invalidCommitted",
                "validationVisible", "immediate" };
        for (int i = 0; i < visibleProps.length; i++) {
            Button b = new Button(visibleProps[i], new MethodProperty(f,
                    visibleProps[i]));
            b.setImmediate(true);
            formProperties.addComponent(b);
        }
        mainWin.addComponent(formProperties);

        // Debug the internal state of the address-object
        mainWin.addComponent(new Button("Show state of the address object",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        mainWin.showNotification(address.toString());
                    }
                }));

        final AddressValidator av = new AddressValidator();
        mainWin.addComponent(new Button("Add addressvalidator",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        f.addValidator(av);
                    }
                }));
        mainWin.addComponent(new Button("Remove addressvalidator",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        f.removeValidator(av);
                    }
                }));

    }

    /** Address pojo. */
    public class Address {
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

        public String toString() {
            return name + "; " + street + "; " + city + " " + zip
                    + (state != null ? " " + state : "") + " " + country;
        }

    }

    /** Simple validator for checking if the validated value is an integer */
    class IsInteger implements Validator {

        public boolean isValid(Object value) {
            try {
                Integer.parseInt("" + value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException("'" + value
                        + "' is not a number");
            }
        }
    }

    class AddressValidator implements Validator {

        public boolean isValid(Object value) {
            if (!(value instanceof Address)) {
                return false;
            }
            Address a = (Address) value;
            if (a.getCity() == null || ("" + a.getCity()).length() < 1) {
                return false;
            }
            if (a.getStreet() == null || ("" + a.getStreet()).length() < 1) {
                return false;
            }
            if (a.getZip() == null || ("" + a.getZip()).length() < 5) {
                return false;
            }
            return true;
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException(
                        "Address should at least have street, zip and city set");
            }
        }
    }

    /** Simple state validator */
    class IsValidState implements Validator {

        public boolean isValid(Object value) {
            // Empty and null are accepted values
            if (value == null || "".equals("" + value)) {
                return true;
            }

            // Otherwise state must be two capital letter combo
            if (value.toString().length() != 2) {
                return false;
            }
            return value.toString().equals(("" + value).toUpperCase());
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException(
                        "State must be either two capital letter abreviation or left empty");
            }
        }
    }

    /** Simple non-empty validator */
    class IsNotEmpty implements Validator {

        public boolean isValid(Object value) {

            if (value == null || "".equals("" + value)) {
                return false;
            }
            return true;
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException("Must not be empty");
            }
        }
    }
}
