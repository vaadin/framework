/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import java.util.EnumSet;

import com.vaadin.data.Item;
import com.vaadin.data.fieldbinder.FieldGroup.BindException;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class DefaultFormBuilderFieldFactory implements FieldGroupFieldFactory {

    public static final Object CAPTION_PROPERTY_ID = "Caption";

    public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
        if (Enum.class.isAssignableFrom(type)) {
            return createEnumField(type, fieldType);
        } else if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)) {
            return createBooleanField(fieldType);
        }
        if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return fieldType.cast(createAbstractTextField(fieldType
                    .asSubclass(AbstractTextField.class)));
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
        if (AbstractSelect.class.isAssignableFrom(fieldType)) {
            AbstractSelect s = createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
            populateWithEnumData(s, (Class<? extends Enum>) type);
            return (T) s;
        }

        return null;
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

    protected <T extends Field> T createBooleanField(Class<T> fieldType) {
        if (fieldType.isAssignableFrom(CheckBox.class)) {
            CheckBox cb = new CheckBox(null);
            cb.setImmediate(true);
            return (T) cb;
        } else if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return (T) createAbstractTextField((Class<? extends AbstractTextField>) fieldType);
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
            throw new BindException("Could not create a field of type "
                    + fieldType, e);
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
