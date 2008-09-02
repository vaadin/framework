package com.itmill.toolkit.tests.book;

import java.util.Vector;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.BeanItem;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Field;
import com.itmill.toolkit.ui.FieldFactory;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.TextField;

/**
 * This example demonstrates the most important features of the Form component:
 * binding Form to a JavaBean so that form fields are automatically generated
 * from the bean properties, creation of fields with proper types for each bean
 * properly using a FieldFactory, buffering (commit/discard), and validation.
 * 
 * The Form is used with a FormLayout, which automatically lays the components
 * out in a format typical for forms.
 */
public class FormExample2 extends CustomComponent {
    /** A simple JavaBean. */
    public class PersonBean {
        String name = "";
        String city = "";

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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
            else if (pid.equals("city")) {
                Select select = new Select("City");
                select.addItem("Berlin");
                select.addItem("Helsinki");
                select.addItem("London");
                select.addItem("New York");
                select.addItem("Turku");
                select.setNewItemsAllowed(true);
                return select;
            }
            return null;
        }

        public Field createField(Container container, Object itemId,
                Object propertyId, Component uiContext) {
            return null;
        }
    }

    public FormExample2() {
        // Create a form and use FormLayout as its layout.
        final Form form = new Form();

        // Set form caption and description texts
        form.setCaption("Contact Information");
        form
                .setDescription("Please specify name of the person and the city where the person lives in.");

        // Use the custom field factory to create the fields in the form.
        form.setFieldFactory(new MyFieldFactory());

        // Create the custom bean.
        PersonBean bean = new PersonBean();

        // Create a bean item that is bound to the bean.
        BeanItem item = new BeanItem(bean);

        // Bind the bean item as the data source for the form.
        form.setItemDataSource(item);

        // Set the order of the items in the form.
        Vector order = new Vector();
        order.add("name");
        order.add("city");
        form.setVisibleItemProperties(order);

        form.getField("name").setRequired(true);
        form.getField("name").setRequiredError("You must enter a name.");
        form.getField("city").setRequired(true);

        OrderedLayout root = new OrderedLayout();
        root.setWidth(300, OrderedLayout.UNITS_PIXELS);
        root.addComponent(form);
        this.setCompositionRoot(root);
    }
}
