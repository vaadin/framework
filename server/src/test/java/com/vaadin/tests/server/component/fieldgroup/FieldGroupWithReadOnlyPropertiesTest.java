package com.vaadin.tests.server.component.fieldgroup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.data.bean.BeanWithReadOnlyField;
import com.vaadin.ui.TextField;

public class FieldGroupWithReadOnlyPropertiesTest {

    private TextField readOnlyField = new TextField();
    private TextField writableField = new TextField();

    @Test
    public void bindReadOnlyPropertyToFieldGroup() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(
                bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);

        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);

        assertTrue(readOnlyField.isReadOnly());
        assertFalse(writableField.isReadOnly());
    }

    @Test
    public void fieldGroupSetReadOnlyTest() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(
                bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);

        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);

        fieldGroup.setReadOnly(true);
        assertTrue(readOnlyField.isReadOnly());
        assertTrue(writableField.isReadOnly());

        fieldGroup.setReadOnly(false);
        assertTrue(readOnlyField.isReadOnly());
        assertFalse(writableField.isReadOnly());
    }

}
