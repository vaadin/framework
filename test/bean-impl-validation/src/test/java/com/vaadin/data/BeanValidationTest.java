package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.validation.Validation;

import org.junit.Test;

import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class BeanValidationTest {

    public static class Bean {

        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

    }

    @Test
    public void binderWorksWithoutBeanValidationImpl() {
        // Just to make sure that it's available at the compilation time and in
        // runtime
        assertNotNull(Validation.class);

        Binder<Bean> binder = new Binder<>(Bean.class);

        TextField field = new TextField();
        binder.forField(field).bind("property");

        Bean bean = new Bean();
        binder.setBean(bean);

        field.setValue("foo");
        assertEquals(field.getValue(), bean.getProperty());

    }
}
