package com.vaadin.data;

import com.vaadin.tests.data.bean.BeanToValidate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnbindTest
        extends BinderTestBase<Binder<BeanToValidate>, BeanToValidate> {
    @Before
    public void setUp() {
        binder = new BeanValidationBinder<>(BeanToValidate.class);
        item = new BeanToValidate();
        item.setFirstname("Johannes");
        item.setAge(32);
    }

    @Test
    public void binding_unbind_shouldBeRemovedFromBindings() {
        Binder.Binding<BeanToValidate, String> firstnameBinding = binder
                .bind(nameField, "firstname");
        Assert.assertEquals(1, binder.getBindings().size());
        firstnameBinding.unbind();
        Assert.assertTrue(binder.getBindings().isEmpty());
        Assert.assertNull(firstnameBinding.getField());
    }

    @Test
    public void binding_unbindDuringReadBean_shouldBeRemovedFromBindings() {
        Binder.Binding<BeanToValidate, String> firstnameBinding = binder
                .bind(nameField, "firstname");
        Binder.Binding<BeanToValidate, String> ageBinding = binder
                .bind(ageField, "age");
        Assert.assertEquals(2, binder.getBindings().size());
        nameField.addValueChangeListener(event -> {
            if (event.getValue().length() > 0)
                ageBinding.unbind();
        });
        binder.readBean(item);
        Assert.assertEquals(1, binder.getBindings().size());
        Assert.assertNull(ageBinding.getField());
    }

    @Test
    public void binding_unbindTwice_shouldBeRemovedFromBindings() {
        Binder.Binding<BeanToValidate, String> firstnameBinding = binder
                .bind(nameField, "firstname");
        Assert.assertEquals(1, binder.getBindings().size());
        firstnameBinding.unbind();
        firstnameBinding.unbind();
        Assert.assertTrue(binder.getBindings().isEmpty());
        Assert.assertNull(firstnameBinding.getField());
    }
}
