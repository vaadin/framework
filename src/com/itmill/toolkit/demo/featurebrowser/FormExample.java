package com.itmill.toolkit.demo.featurebrowser;

import java.util.Vector;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.FieldFactory;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Button.ClickEvent;

/**
 * This example demonstrates the most important features of the Form component:
 * binding Form to a JavaBean so that form fields are automatically generated
 * from the bean properties, creation of fields with proper types for each bean
 * properly using a FieldFactory, buffering (commit/discard), and validation.
 * 
 * The Form is used with a FormLayout, which automatically lays the components
 * out in a format typical for forms.
 */
public class FormExample extends CustomComponent {
    /** Contact information data model. */
    public class Contact {
        String name = "";
        String address = "";
        int postalCode = 0;
        String city;
    }

    /** Bean wrapper for the data model. */
    public class ContactBean extends Contact {
        public ContactBean() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setPostalCode(String postalCode) {
            try {
                if (postalCode != null)
                    this.postalCode = Integer.parseInt(postalCode);
                else
                    this.postalCode = 0;
            } catch (NumberFormatException e) {
                this.postalCode = 0;
            }
        }

        public String getPostalCode() {
            if (postalCode > 0)
                return String.valueOf(postalCode);
            else
                return "";
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    /**
     * Factory to create the proper type of field for each property type. We
     * need to implement just one of the factory methods.
     */
    class MyFieldFactory implements FieldFactory {

        public Field createField(Class type, Component uiContext) {
            return null;
        }

        public Field createField(Property property, Component uiContext) {
            return null;
        }

        public Field createField(Item item, Object propertyId,
                Component uiContext) {
            String pid = (String) propertyId;

            if (pid.equals("name"))
                return new TextField("Name");

            if (pid.equals("address"))
                return new TextField("Street Address");

            if (pid.equals("postalCode")) {
                TextField field = new TextField("Postal Code");
                field.setColumns(5);
                Validator postalCodeValidator = new Validator() {

                    public boolean isValid(Object value) {
                        if (value == null || !(value instanceof String)) {
                            return false;
                        }

                        return ((String) value).matches("[0-9]{5}");
                    }

                    public void validate(Object value)
                            throws InvalidValueException {
                        if (!isValid(value)) {
                            throw new InvalidValueException(
                                    "Postal code must be a number 10000-99999.");
                        }
                    }
                };
                field.addValidator(postalCodeValidator);
                return field;
            }

            if (pid.equals("city")) {
                Select select = new Select("City");
                final String cities[] = new String[] { "Amsterdam", "Berlin",
                        "Helsinki", "Hong Kong", "London", "Luxemburg",
                        "New York", "Oslo", "Paris", "Rome", "Stockholm",
                        "Tokyo", "Turku" };
                for (int i = 0; i < cities.length; i++)
                    select.addItem(cities[i]);
                return select;
            }
            return null;
        }

        public Field createField(Container container, Object itemId,
                Object propertyId, Component uiContext) {
            return null;
        }
    }

    /**
     * Displays the contents of the bean in a table.
     * 
     * This is not as clean as it should be. Currently, components can not be
     * bound to a BeanItem so that the when the data in the BeanItem changes,
     * the displayed value would be automatically refreshed. We therefore do the
     * refreshing manually.
     **/
    public class ContactDisplay extends CustomComponent {
        ContactBean contact;
        Label name;
        Label address;
        Label postalCode;
        Label city;

        public ContactDisplay(ContactBean contact) {
            this.contact = contact;

            // Use a Form merely as a layout component. The CSS will add
            // a border to the form.
            Form layout = new Form();
            setCompositionRoot(layout);
            layout.setCaption("Data Model State");
            layout
                    .setDescription("Below is the state of the actual stored data. "
                            + "It is updated only when the form is committed successfully. "
                            + "Discarding the form input reverts the form to this state.");
            layout.setWidth("400px");

            // Manually create read-only components for each of the fields.
            name = new Label(contact.getName());
            name.setCaption("Name:");
            address = new Label(contact.getAddress());
            address.setCaption("Address:");
            postalCode = new Label(contact.getPostalCode());
            postalCode.setCaption("Postal Code:");
            city = new Label(contact.getCity());
            city.setCaption("City:");

            layout.getLayout().addComponent(name);
            layout.getLayout().addComponent(address);
            layout.getLayout().addComponent(postalCode);
            layout.getLayout().addComponent(city);
        }

        /**
         * The Label components are not bound to the bean properties, so we have
         * to refresh the components manually.
         */
        public void refresh() {
            name.setValue(contact.getName());
            address.setValue(contact.getAddress());
            postalCode.setValue(contact.getPostalCode());
            city.setValue(contact.getCity());
        }
    }

    public FormExample() {
        // The root layout of the custom component.
        OrderedLayout root = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        root.addStyleName("formroot");
        setCompositionRoot(root);

        // Create a form. It will use FormLayout as its layout by default.
        final Form form = new Form();
        root.addComponent(form);
        form.setWidth("400px");

        // The caption appears within the border of the form box. The form box
        // is enabled in the CSS styles with "border: 1px solid".
        form.setCaption("Contact Information");

        // Set description that will appear on top of the form.
        form
                .setDescription("Please enter valid name and address. Fields marked with * are required. "
                        + "If you try to commit with invalid values, a form error message is displayed. "
                        + "(Address is required but failing to give it a value does not display an error.)");

        // Use custom field factory to create the fields in the form.
        form.setFieldFactory(new MyFieldFactory());

        // Create the custom bean.
        ContactBean bean = new ContactBean();

        // Create a bean item that is bound to the bean.
        BeanItem item = new BeanItem(bean);

        // Bind the bean item as the data source for the form.
        form.setItemDataSource(item);

        // Set the order of the items in the form.
        Vector order = new Vector();
        order.add("name");
        order.add("address");
        order.add("postalCode");
        order.add("city");
        form.setVisibleItemProperties(order);

        // Set required fields. The required error is displayed in
        // the error indication are of the Form if a required
        // field is empty. If it is not set, no error is displayed
        // about an empty required field.
        form.getField("name").setRequired(true);
        form.getField("name").setRequiredError("Name is missing");
        form.getField("address").setRequired(true); // No error message

        // Set the form to act immediately on user input. This is
        // necessary for the validation of the fields to occur immediately when
        // the input focus changes and not just on commit.
        form.setImmediate(true);

        // Set buffering so that commit() must be called for the form
        // before input is written to the data. (Form input is not written
        // immediately through to the underlying object.)
        form.setWriteThrough(false);

        // If the state of the bound data source changes, the changes are shown
        // immediately in the form, so there is no buffering. (This is the
        // default.)
        form.setReadThrough(true);

        // Have a read-only component to display the actual current state
        // of the bean (POJO).
        final ContactDisplay display = new ContactDisplay(bean);
        root.addComponent(display);

        // Add Commit and Discard controls to the form.
        ExpandLayout footer = new ExpandLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        // The Commit button calls form.commit().
        Button commit = new Button("Commit", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                form.commit();
                display.refresh();
            }
        });

        // The Discard button calls form.discard().
        Button discard = new Button("Discard", form, "discard");
        footer.addComponent(commit);
        footer.setComponentAlignment(commit, ExpandLayout.ALIGNMENT_RIGHT,
                ExpandLayout.ALIGNMENT_TOP);
        footer.setHeight("25px"); // Has to be set explicitly for ExpandLayout.
        footer.addComponent(discard);
        form.setFooter(footer);
    }
}
