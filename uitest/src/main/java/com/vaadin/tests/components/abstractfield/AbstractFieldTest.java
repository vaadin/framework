package com.vaadin.tests.components.abstractfield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public abstract class AbstractFieldTest<T extends AbstractField<V>, V>
        extends AbstractComponentTest<T> {

    private boolean sortValueChanges = true;

    protected Registration valueChangeListenerRegistration;

    @Override
    protected void createActions() {
        super.createActions();

        createBooleanAction("Required", CATEGORY_STATE, false, requiredCommand);

        createValueChangeListener(CATEGORY_LISTENERS);

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
        // * validation visible
        // * ShortcutListener

    }

    @Override
    protected void populateSettingsMenu(MenuItem settingsMenu) {
        super.populateSettingsMenu(settingsMenu);

        if (AbstractField.class.isAssignableFrom(getTestClass())) {
            MenuItem abstractField = settingsMenu.addItem("AbstractField",
                    null);
            abstractField.addItem("Show value", new MenuBar.Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    for (T a : getTestComponents()) {
                        log(a.getClass().getSimpleName() + " value: "
                                + formatValue(a.getValue()));
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

    private void createValueChangeListener(String category) {

        createBooleanAction("Value change listener", category, false,
                valueChangeListenerCommand);
    }

    protected Command<T, Boolean> valueChangeListenerCommand = new Command<T, Boolean>() {

        private ValueChangeListener<V> valueChangeListener = new ValueChangeListener<V>() {

            @Override
            public void valueChange(ValueChangeEvent<V> event) {
                log(event.getClass().getSimpleName() + ", new value: "
                        + formatValue(event.getValue()));
            }
        };

        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                if (valueChangeListenerRegistration == null) {
                    valueChangeListenerRegistration = c
                            .addValueChangeListener(valueChangeListener);
                }
            } else {
                if (valueChangeListenerRegistration != null) {
                    valueChangeListenerRegistration.remove();
                    valueChangeListenerRegistration = null;
                }
            }
        }
    };

    protected Command<T, V> setValueCommand = new Command<T, V>() {

        @Override
        public void execute(T c, V value, Object data) {
            c.setValue(value);
        }
    };

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String formatValue(Object o) {
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
            if (o instanceof LocalDate) {
                LocalDate date = (LocalDate) o;
                // Dec 31, 2068
                String pattern = "MMM d, yyyy";
                DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern,
                        Locale.ENGLISH);
                value = format.format(date);
            } else {
                value = "'" + o.toString() + "'";
            }
        }

        return value;

    }

}
