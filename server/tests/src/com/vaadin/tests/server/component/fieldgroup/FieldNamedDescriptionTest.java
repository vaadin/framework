package com.vaadin.tests.server.component.fieldgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class FieldNamedDescriptionTest {

    @Test
    public void bindReadOnlyPropertyToFieldGroup() {
        // Create an item
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("name", new ObjectProperty<String>("Zaphod"));
        item.addItemProperty("description", new ObjectProperty<String>(
                "This is a description"));

        // Define a form as a class that extends some layout
        class MyForm extends FormLayout {
            // Member that will bind to the "name" property
            TextField name = new TextField("Name");

            // This member will not bind to the desctiptionProperty as the name
            // description conflicts with something in the binding process
            @PropertyId("description")
            TextField description = new TextField("Description");

            public MyForm() {

                // Add the fields
                addComponent(name);
                addComponent(description);
            }
        }

        // Create one
        MyForm form = new MyForm();

        // Now create a binder that can also creates the fields
        // using the default field factory
        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue(form.description.getValue().equals("This is a description"));
    }

}
