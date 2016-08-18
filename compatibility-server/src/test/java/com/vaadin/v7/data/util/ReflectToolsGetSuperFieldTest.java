package com.vaadin.v7.data.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.PropertyId;
import com.vaadin.v7.ui.TextField;

public class ReflectToolsGetSuperFieldTest {

    @Test
    public void getFieldFromSuperClass() {
        class MyClass {
            @PropertyId("testProperty")
            TextField test = new TextField("This is a test");
        }
        class MySubClass extends MyClass {
            // no fields here
        }

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty("testProperty",
                new ObjectProperty<String>("Value of testProperty"));

        MySubClass form = new MySubClass();

        FieldGroup binder = new FieldGroup(item);
        binder.bindMemberFields(form);

        assertTrue("Value of testProperty".equals(form.test.getValue()));
    }

}
