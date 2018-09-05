package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    public void binderWorksWithoutBeanValidationLib() {
        try {
            Class.forName("javax.validation.Validation");
            fail("Validation API should not be present");
        } catch (ClassNotFoundException ignored) {
        }

        Binder<Bean> binder = new Binder<>(Bean.class);

        TextField field = new TextField();
        binder.forField(field).bind("property");

        Bean bean = new Bean();
        binder.setBean(bean);

        field.setValue("foo");
        assertEquals(field.getValue(), bean.getProperty());

    }
}
