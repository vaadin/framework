/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data;

import javax.validation.Validation;

import org.junit.Assert;
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
        Assert.assertNotNull(Validation.class);

        Binder<Bean> binder = new Binder<>(Bean.class);

        TextField field = new TextField();
        binder.forField(field).bind("property");

        Bean bean = new Bean();
        binder.setBean(bean);

        field.setValue("foo");
        Assert.assertEquals(field.getValue(), bean.getProperty());

    }
}
