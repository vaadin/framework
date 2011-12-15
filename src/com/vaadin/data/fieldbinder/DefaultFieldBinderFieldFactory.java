package com.vaadin.data.fieldbinder;

import java.util.EnumSet;

import com.vaadin.data.Item;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class DefaultFieldBinderFieldFactory implements FieldBinderFieldFactory {

    public static final Object CAPTION_PROPERTY_ID = "Caption";

    @SuppressWarnings("unchecked")
    public Field createField(Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return createEnumField((Class<? extends Enum<?>>) type);
        }
        if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)) {
            return createBooleanField();
        }
        return createDefaultField(type);
    }

    private Field createBooleanField() {
        CheckBox cb = new CheckBox(null);
        cb.setImmediate(true);
        return cb;
    }

    private Field createDefaultField(Class<?> type) {
        return new TextField();
    }

    public Field createEnumField(Class<? extends Enum> enumClass) {
        ComboBox select = new ComboBox(null);
        select.setImmediate(true);
        select.setNullSelectionAllowed(false);
        select.addContainerProperty(CAPTION_PROPERTY_ID, String.class, "");
        select.setItemCaptionPropertyId(CAPTION_PROPERTY_ID);
        @SuppressWarnings("unchecked")
        EnumSet<?> enumSet = EnumSet.allOf(enumClass);
        for (Object r : enumSet) {
            Item newItem = select.addItem(r);
            newItem.getItemProperty(CAPTION_PROPERTY_ID).setValue(r.toString());
        }
        return select;
    }

}
