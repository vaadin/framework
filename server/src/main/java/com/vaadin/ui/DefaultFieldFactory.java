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

import java.text.Normalizer.Form;
import java.util.Date;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.legacy.ui.LegacyCheckBox;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyField;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.shared.util.SharedUtil;

/**
 * This class contains a basic implementation for {@link TableFieldFactory}. The
 * class is singleton, use {@link #get()} method to get reference to the
 * instance.
 *
 * <p>
 * There are also some static helper methods available for custom built field
 * factories.
 *
 */
public class DefaultFieldFactory implements TableFieldFactory {

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
    public LegacyField createField(Container container, Object itemId,
            Object propertyId, Component uiContext) {
        Property containerProperty = container.getContainerProperty(itemId,
                propertyId);
        Class<?> type = containerProperty.getType();
        LegacyField<?> field = createFieldByPropertyType(type);
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
        return SharedUtil.propertyIdToHumanFriendly(propertyId);
    }

    /**
     * Creates fields based on the property type.
     * <p>
     * The default field type is {@link LegacyTextField}. Other field types generated
     * by this method:
     * <p>
     * <b>Boolean</b>: {@link CheckBox}.<br/>
     * <b>Date</b>: {@link LegacyDateField}(resolution: day).<br/>
     * <b>Item</b>: {@link Form}. <br/>
     * <b>default field type</b>: {@link LegacyTextField}.
     * <p>
     *
     * @param type
     *            the type of the property
     * @return the most suitable generic {@link LegacyField} for given type
     */
    public static LegacyField<?> createFieldByPropertyType(Class<?> type) {
        // Null typed properties can not be edited
        if (type == null) {
            return null;
        }

        // Date field
        if (Date.class.isAssignableFrom(type)) {
            final LegacyDateField df = new LegacyDateField();
            df.setResolution(LegacyDateField.RESOLUTION_DAY);
            return df;
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(type)) {
            return new LegacyCheckBox();
        }

        return new LegacyTextField();
    }

}
