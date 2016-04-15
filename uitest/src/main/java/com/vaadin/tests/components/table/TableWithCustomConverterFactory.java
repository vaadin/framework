/*
 * Copyright 2012 Vaadin Ltd.
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
package com.vaadin.tests.components.table;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

public class TableWithCustomConverterFactory extends AbstractTestUI {

    public static class MyIntegerConverter implements
            Converter<String, Integer> {

        @Override
        public Integer convertToModel(String value,
                Class<? extends Integer> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String convertToPresentation(Integer value,
                Class<? extends String> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            return "Integer: " + value;
        }

        @Override
        public Class<Integer> getModelType() {
            return Integer.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }

    }

    public static class MyConverterFactory extends DefaultConverterFactory {
        @Override
        protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
            if (Integer.class.isAssignableFrom(sourceType)) {
                return new MyIntegerConverter();
            } else {
                return super.createStringConverter(sourceType);
            }
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        getSession().setConverterFactory(new MyConverterFactory());
        Table t = new Table();
        t.addContainerProperty("String column", String.class, "");
        t.addContainerProperty("Integer column", Integer.class, "");
        t.addItem(new Object[] { "Second column is 1", 1 }, "item1");
        t.addItem(new Object[] { "Second column is 4589", 4589 }, "item2");

        addComponent(t);
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
