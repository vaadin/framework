package com.vaadin.data.fieldgroup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Property;
import com.vaadin.data.Property.Transactional;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.TransactionalPropertyWrapper;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class FieldGroupTests {

    private FieldGroup sut;
    private Field field;

    @Before
    public void setup() {
        sut = new FieldGroup();
        field = mock(Field.class);
    }

    @Test
    public void fieldIsBound() {
        sut.bind(field, "foobar");

        assertThat(sut.getField("foobar"), is(field));
    }

    @Test(expected = FieldGroup.BindException.class)
    public void cannotBindToAlreadyBoundProperty() {
        sut.bind(field, "foobar");
        sut.bind(mock(Field.class), "foobar");
    }

    @Test(expected = FieldGroup.BindException.class)
    public void cannotBindNullField() {
        sut.bind(null, "foobar");
    }

    public void canUnbindWithoutItem() {
        sut.bind(field, "foobar");

        sut.unbind(field);
        assertThat(sut.getField("foobar"), is(nullValue()));
    }

    @Test
    public void wrapInTransactionalProperty_provideCustomImpl_customTransactionalWrapperIsUsed() {
        Bean bean = new Bean();
        FieldGroup group = new FieldGroup() {
            @Override
            protected <T> Transactional<T> wrapInTransactionalProperty(
                    Property<T> itemProperty) {
                return new TransactionalPropertyImpl(itemProperty);
            }
        };
        group.setItemDataSource(new BeanItem<Bean>(bean));
        TextField field = new TextField();
        group.bind(field, "name");

        Property propertyDataSource = field.getPropertyDataSource();
        Assert.assertTrue("Custom implementation of transactional property "
                + "has not been used",
                propertyDataSource instanceof TransactionalPropertyImpl);
    }

    public static class TransactionalPropertyImpl<T> extends
            TransactionalPropertyWrapper<T> {

        public TransactionalPropertyImpl(Property<T> wrappedProperty) {
            super(wrappedProperty);
        }

    }

    public static class Bean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
