/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui;

import java.util.Date;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * This class contains a basic implementation for both {@link FormFieldFactory}
 * and {@link TableFieldFactory}. The class is singleton, use {@link #get()}
 * method to get reference to the instance.
 * 
 * <p>
 * There are also some static helper methods available for custom built field
 * factories.
 * 
 */
public class DefaultFieldFactory implements FormFieldFactory, TableFieldFactory {

    private static final DefaultFieldFactory instance = new DefaultFieldFactory();

    /**
     * Singleton method to get an instance of DefaultFieldFactory.
     * 
     * @return an instance of DefaultFieldFactory
     */
    public static DefaultFieldFactory get() {
        return instance;
    }

    protected DefaultFieldFactory() {
    }

    @Override
    public Field<?> createField(Item item, Object propertyId,
            Component uiContext) {
        Class<?> type = item.getItemProperty(propertyId).getType();
        Field<?> field = createFieldByPropertyType(type);
        field.setCaption(createCaptionByPropertyId(propertyId));
        return field;
    }

    @Override
    public Field createField(Container container, Object itemId,
            Object propertyId, Component uiContext) {
        Property containerProperty = container.getContainerProperty(itemId,
                propertyId);
        Class<?> type = containerProperty.getType();
        Field<?> field = createFieldByPropertyType(type);
        field.setCaption(createCaptionByPropertyId(propertyId));
        return field;
    }

    /**
     * If name follows method naming conventions, convert the name to spaced
     * upper case text. For example, convert "firstName" to "First Name"
     * 
     * @param propertyId
     * @return the formatted caption string
     */
    public static String createCaptionByPropertyId(Object propertyId) {
        String name = propertyId.toString();
        if (name.length() > 0) {

            int dotLocation = name.lastIndexOf('.');
            if (dotLocation > 0 && dotLocation < name.length() - 1) {
                name = name.substring(dotLocation + 1);
            }
            if (name.indexOf(' ') < 0
                    && name.charAt(0) == Character.toLowerCase(name.charAt(0))
                    && name.charAt(0) != Character.toUpperCase(name.charAt(0))) {
                StringBuffer out = new StringBuffer();
                out.append(Character.toUpperCase(name.charAt(0)));
                int i = 1;

                while (i < name.length()) {
                    int j = i;
                    for (; j < name.length(); j++) {
                        char c = name.charAt(j);
                        if (Character.toLowerCase(c) != c
                                && Character.toUpperCase(c) == c) {
                            break;
                        }
                    }
                    if (j == name.length()) {
                        out.append(name.substring(i));
                    } else {
                        out.append(name.substring(i, j));
                        out.append(" " + name.charAt(j));
                    }
                    i = j + 1;
                }

                name = out.toString();
            }
        }
        return name;
    }

    /**
     * Creates fields based on the property type.
     * <p>
     * The default field type is {@link TextField}. Other field types generated
     * by this method:
     * <p>
     * <b>Boolean</b>: {@link CheckBox}.<br/>
     * <b>Date</b>: {@link DateField}(resolution: day).<br/>
     * <b>Item</b>: {@link Form}. <br/>
     * <b>default field type</b>: {@link TextField}.
     * <p>
     * 
     * @param type
     *            the type of the property
     * @return the most suitable generic {@link Field} for given type
     */
    public static Field<?> createFieldByPropertyType(Class<?> type) {
        // Null typed properties can not be edited
        if (type == null) {
            return null;
        }

        // Item field
        if (Item.class.isAssignableFrom(type)) {
            return new Form();
        }

        // Date field
        if (Date.class.isAssignableFrom(type)) {
            final DateField df = new DateField();
            df.setResolution(DateField.RESOLUTION_DAY);
            return df;
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(type)) {
            return new CheckBox();
        }

        return new TextField();
    }

}
