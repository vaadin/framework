package com.vaadin.data.fieldgroup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Field;

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
}
