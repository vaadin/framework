package com.vaadin.tests.server.component.fieldgroup;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class CaseInsensitiveBinding {

    @Test
    public void caseInsensitivityAndUnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("LastName", new ObjectProperty<String>("Sparrow"));

        class MyForm extends FormLayout {
            TextField lastName = new TextField("Last name");

            public MyForm() {

                // Should bind to the LastName property
                addComponent(lastName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Sparrow".equals(form.lastName.getValue()));
    }

    @Test
    public void UnderscoreRemoval() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name", new ObjectProperty<String>("Jack"));

        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // Should bind to the first_name property
                addComponent(firstName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Jack".equals(form.firstName.getValue()));
    }

    @Test
    public void perfectMatchPriority() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("first_name", new ObjectProperty<String>(
                "Not this"));
        item.addItemProperty("firstName", new ObjectProperty<String>("This"));

        class MyForm extends FormLayout {
            TextField firstName = new TextField("First name");

            public MyForm() {
                // should bind to the firstName property, not first_name
                // property
                addComponent(firstName);
            }
        }

        MyForm form = new MyForm();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("This".equals(form.firstName.getValue()));
    }

}
