/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.data.fieldgroup;

import java.util.Date;
import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.FieldGroup.BindException;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.InlineDateField;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PopupDateField;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

/**
 * This class contains a basic implementation for {@link FieldGroupFieldFactory}
 * .The class is singleton, use {@link #get()} method to get reference to the
 * instance.
 *
 * @author Vaadin Ltd
 * @deprecated As of 8.0, no direct replacement available. {@link Binder#forMemberField(HasValue)} and
 * {@link Binder#bindInstanceFields(Object)} should be used instead.
 */
@Deprecated
public class DefaultFieldGroupFieldFactory implements FieldGroupFieldFactory {

    private static final DefaultFieldGroupFieldFactory INSTANCE = new DefaultFieldGroupFieldFactory();

    public static final Object CAPTION_PROPERTY_ID = "Caption";

    protected DefaultFieldGroupFieldFactory() {
    }

    /**
     * Gets the singleton instance.
     *
     * @since 7.4
     *
     * @return the singleton instance
     */
    public static DefaultFieldGroupFieldFactory get() {
        return INSTANCE;
    }

    @Override
    public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
        if (Enum.class.isAssignableFrom(type)) {
            return createEnumField(type, fieldType);
        } else if (Date.class.isAssignableFrom(type)) {
            return createDateField(type, fieldType);
        } else if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)) {
            return createBooleanField(fieldType);
        }
        if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return fieldType.cast(createAbstractTextField(
                    fieldType.asSubclass(AbstractTextField.class)));
        } else if (fieldType == RichTextArea.class) {
            return fieldType.cast(createRichTextArea());
        }
        return createDefaultField(type, fieldType);
    }

    protected RichTextArea createRichTextArea() {
        RichTextArea rta = new RichTextArea();
        rta.setImmediate(true);

        return rta;
    }

    private <T extends Field> T createEnumField(Class<?> type,
            Class<T> fieldType) {
        // Determine first if we should (or can) create a select for the enum
        Class<AbstractSelect> selectClass = null;
        if (AbstractSelect.class.isAssignableFrom(fieldType)) {
            selectClass = (Class<AbstractSelect>) fieldType;
        } else if (anySelect(fieldType)) {
            selectClass = AbstractSelect.class;
        }

        if (selectClass != null) {
            AbstractSelect s = createCompatibleSelect(selectClass);
            populateWithEnumData(s, (Class<? extends Enum>) type);
            return (T) s;
        } else if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return (T) createAbstractTextField(
                    (Class<? extends AbstractTextField>) fieldType);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Field> T createDateField(Class<?> type,
            Class<T> fieldType) {
        AbstractField<?> field;

        if (InlineDateField.class.isAssignableFrom(fieldType)) {
            field = new InlineDateField();
        } else if (anyField(fieldType)
                || DateField.class.isAssignableFrom(fieldType)) {
            field = new PopupDateField();
        } else if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            field = createAbstractTextField(
                    (Class<? extends AbstractTextField>) fieldType);
        } else {
            return null;
        }

        field.setImmediate(true);
        return (T) field;
    }

    protected AbstractSelect createCompatibleSelect(
            Class<? extends AbstractSelect> fieldType) {
        AbstractSelect select;
        if (fieldType.isAssignableFrom(ListSelect.class)) {
            select = new ListSelect();
            select.setMultiSelect(false);
        } else if (fieldType.isAssignableFrom(NativeSelect.class)) {
            select = new NativeSelect();
        } else if (fieldType.isAssignableFrom(OptionGroup.class)) {
            select = new OptionGroup();
            select.setMultiSelect(false);
        } else if (fieldType.isAssignableFrom(Table.class)) {
            Table t = new Table();
            t.setSelectable(true);
            select = t;
        } else {
            select = new ComboBox(null);
        }
        select.setImmediate(true);
        select.setNullSelectionAllowed(false);

        return select;
    }

    /**
     * @since 7.4
     * @param fieldType
     *            the type of the field
     * @return true if any AbstractField can be assigned to the field
     */
    protected boolean anyField(Class<?> fieldType) {
        return fieldType == Field.class || fieldType == AbstractField.class;
    }

    /**
     * @since 7.4
     * @param fieldType
     *            the type of the field
     * @return true if any AbstractSelect can be assigned to the field
     */
    protected boolean anySelect(Class<? extends Field> fieldType) {
        return anyField(fieldType) || fieldType == AbstractSelect.class;
    }

    protected <T extends Field> T createBooleanField(Class<T> fieldType) {
        if (fieldType.isAssignableFrom(CheckBox.class)) {
            CheckBox cb = new CheckBox(null);
            cb.setImmediate(true);
            return (T) cb;
        } else if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return (T) createAbstractTextField(
                    (Class<? extends AbstractTextField>) fieldType);
        }

        return null;
    }

    protected <T extends AbstractTextField> T createAbstractTextField(
            Class<T> fieldType) {
        if (fieldType == AbstractTextField.class) {
            fieldType = (Class<T>) TextField.class;
        }
        try {
            T field = fieldType.newInstance();
            field.setImmediate(true);
            return field;
        } catch (Exception e) {
            throw new BindException(
                    "Could not create a field of type " + fieldType, e);
        }
    }

    /**
     * Fallback when no specific field has been created. Typically returns a
     * TextField.
     *
     * @param <T>
     *            The type of field to create
     * @param type
     *            The type of data that should be edited
     * @param fieldType
     *            The type of field to create
     * @return A field capable of editing the data or null if no field could be
     *         created
     */
    protected <T extends Field> T createDefaultField(Class<?> type,
            Class<T> fieldType) {
        if (fieldType.isAssignableFrom(TextField.class)) {
            return fieldType.cast(createAbstractTextField(TextField.class));
        }
        return null;
    }

    /**
     * Populates the given select with all the enums in the given {@link Enum}
     * class. Uses {@link Enum}.toString() for caption.
     *
     * @param select
     *            The select to populate
     * @param enumClass
     *            The Enum class to use
     */
    protected void populateWithEnumData(AbstractSelect select,
            Class<? extends Enum> enumClass) {
        select.removeAllItems();
        for (Object p : select.getContainerPropertyIds()) {
            select.removeContainerProperty(p);
        }
        select.addContainerProperty(CAPTION_PROPERTY_ID, String.class, "");
        select.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
        @SuppressWarnings("unchecked")
        EnumSet<?> enumSet = EnumSet.allOf(enumClass);
        for (Object r : enumSet) {
            Item newItem = select.addItem(r);
            newItem.getItemProperty(CAPTION_PROPERTY_ID).setValue(r.toString());
        }
    }
}
