package com.vaadin.tests.components.abstractfield;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public abstract class AbstractFieldTest<T extends AbstractField> extends
        AbstractComponentTest<T> implements ValueChangeListener,
        ReadOnlyStatusChangeListener {

    private boolean sortValueChanges = true;

    @Override
    protected void createActions() {
        super.createActions();
        createBooleanAction("Required", CATEGORY_STATE, false, requiredCommand);
        createRequiredErrorSelect(CATEGORY_DECORATIONS);
        if (FocusNotifier.class.isAssignableFrom(getTestClass())) {
            createFocusListener(CATEGORY_LISTENERS);
        }

        if (BlurNotifier.class.isAssignableFrom(getTestClass())) {
            createBlurListener(CATEGORY_LISTENERS);
        }

        createValueChangeListener(CATEGORY_LISTENERS);
        createReadOnlyStatusChangeListener(CATEGORY_LISTENERS);

        // * invalidcommitted
        // * commit()
        // * discard()
        // * writethrough
        // * readthrough
        // * addvalidator
        // * isvalid
        // * invalidallowed
        // * error indicator
        //
        // * tabindex
        // * validation visible
        // * ShortcutListener

    }

    @Override
    protected void populateSettingsMenu(MenuItem settingsMenu) {
        super.populateSettingsMenu(settingsMenu);

        if (AbstractField.class.isAssignableFrom(getTestClass())) {
            MenuItem abstractField = settingsMenu
                    .addItem("AbstractField", null);
            abstractField.addItem("Show value", new MenuBar.Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    for (T a : getTestComponents()) {
                        log(a.getClass().getSimpleName() + " value: "
                                + getValue(a));
                    }
                }
            });

            MenuItem sortValueChangesItem = abstractField.addItem(
                    "Show sorted value changes", new MenuBar.Command() {
                        @Override
                        public void menuSelected(MenuItem selectedItem) {
                            sortValueChanges = selectedItem.isChecked();
                            log("Show sorted value changes: "
                                    + sortValueChanges);
                        }
                    });
            sortValueChangesItem.setCheckable(true);
            sortValueChangesItem.setChecked(true);
        }
    }

    private void createRequiredErrorSelect(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put(TEXT_SHORT, TEXT_SHORT);
        options.put("Medium", TEXT_MEDIUM);
        options.put("Long", TEXT_LONG);
        options.put("Very long", TEXT_VERY_LONG);
        createSelectAction("Required error message", category, options, "-",
                requiredErrorMessageCommand);

    }

    private void createValueChangeListener(String category) {

        createBooleanAction("Value change listener", category, false,
                valueChangeListenerCommand);
    }

    private void createReadOnlyStatusChangeListener(String category) {

        createBooleanAction("Read only status change listener", category,
                false, readonlyStatusChangeListenerCommand);
    }

    protected Command<T, Boolean> valueChangeListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ValueChangeListener) AbstractFieldTest.this);
            } else {
                c.removeListener((ValueChangeListener) AbstractFieldTest.this);
            }
        }
    };
    protected Command<T, Boolean> readonlyStatusChangeListenerCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ReadOnlyStatusChangeListener) AbstractFieldTest.this);
            } else {
                c.removeListener((ReadOnlyStatusChangeListener) AbstractFieldTest.this);
            }
        }
    };

    protected Command<T, Object> setValueCommand = new Command<T, Object>() {

        @Override
        public void execute(T c, Object value, Object data) {
            c.setValue(value);
        }
    };

    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        log(event.getClass().getSimpleName() + ", new value: "
                + getValue(event.getProperty()));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String getValue(Property property) {
        Object o = property.getValue();
        if (o instanceof Collection && sortValueChanges) {
            // Sort collections to avoid problems with values printed in
            // different order
            try {
                ArrayList<Comparable> c = new ArrayList<Comparable>(
                        (Collection) o);
                Collections.sort(c);
                o = c;
            } catch (Exception e) {
                // continue with unsorted if sorting fails for some reason
                log("Exception while sorting value: " + e.getMessage());
            }
        }

        // Distinguish between null and 'null'
        String value = "null";
        if (o != null) {
            if (o instanceof Date) {
                Date d = (Date) o;
                // Dec 31, 2068 23:09:26.531
                String pattern = "MMM d, yyyy HH:mm:ss.SSS";
                SimpleDateFormat format = new SimpleDateFormat(pattern,
                        new Locale("en", "US"));
                value = format.format(d);
            } else {
                value = "'" + o.toString() + "'";
            }
        }

        return value;

    }

    @Override
    public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
        log(event.getClass().getSimpleName());
    }

    protected void createSetTextValueAction(String category) {
        String subCategory = "Set text value";
        createCategory(subCategory, category);
        List<String> values = new ArrayList<String>();
        values.add("Test");
        values.add("A little longer value");
        values.add("A very long value with very much text. All in all it is 74 characters long");

        createClickAction("(empty string)", subCategory, setValueCommand, "");
        createClickAction("(null)", subCategory, setValueCommand, null);
        for (String value : values) {
            createClickAction(value, subCategory, setValueCommand, value);
        }
    }

}
