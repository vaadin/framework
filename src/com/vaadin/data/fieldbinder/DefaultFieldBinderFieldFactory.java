/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.fieldbinder;

import java.util.EnumSet;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class DefaultFieldBinderFieldFactory implements FieldBinderFieldFactory {

    public static final Object CAPTION_PROPERTY_ID = "Caption";

    public Field createField(Class<?> type, Class<? extends Field> fieldType) {
        if (Enum.class.isAssignableFrom(type)) {
            return createEnumField(type, fieldType);
        } else if (Boolean.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type)) {
            return createBooleanField(fieldType);
        }
        return createDefaultField(type, fieldType);
    }

    private Field createEnumField(Class<?> type,
            Class<? extends Field> fieldType) {
        if (AbstractSelect.class.isAssignableFrom(fieldType)) {
            AbstractSelect s = createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
            populateWithEnumData(s, (Class<? extends Enum>) type);
            return s;
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

    protected Field createBooleanField(Class<? extends Field> fieldType) {
        if (fieldType.isAssignableFrom(CheckBox.class)) {
            CheckBox cb = new CheckBox(null);
            cb.setImmediate(true);
            return cb;
        } else if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return createAbstractTextField((Class<? extends AbstractTextField>) fieldType);
        }

        return null;
    }

    protected AbstractTextField createAbstractTextField(
            Class<? extends AbstractTextField> fieldType) {
        if (fieldType.isAssignableFrom(PasswordField.class)) {
            PasswordField pf = new PasswordField();
            pf.setImmediate(true);
            return pf;
        } else if (fieldType.isAssignableFrom(TextField.class)) {
            TextField tf = new TextField();
            tf.setImmediate(true);
            return tf;
        } else if (fieldType.isAssignableFrom(TextArea.class)) {
            TextArea ta = new TextArea();
            ta.setImmediate(true);
            return ta;
        }

        return null;
    }

    protected Field createDefaultField(Class<?> type,
            Class<? extends Field> fieldType) {
        if (AbstractTextField.class.isAssignableFrom(fieldType)) {
            return createAbstractTextField((Class<? extends AbstractTextField>) fieldType);
        }
        return null;
    }

    /**
     * @param select
     * @param enumClass
     */
    protected void populateWithEnumData(AbstractSelect select,
            Class<? extends Enum> enumClass) {
        // TODO EnumContainer?
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
