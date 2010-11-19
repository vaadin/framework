package com.vaadin.tests.components.abstractfield;

import java.util.LinkedHashMap;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractField;

public abstract class AbstractFieldTest<T extends AbstractField> extends
        AbstractComponentTest<T> implements ValueChangeListener,
        ReadOnlyStatusChangeListener, FocusListener, BlurListener {

    @Override
    protected void createActions() {
        super.createActions();
        createBooleanAction("Required", CATEGORY_STATE, false, requiredCommand);
        createRequiredErrorSelect(CATEGORY_DECORATIONS);

        createValueChangeListener(CATEGORY_LISTENERS);
        createReadOnlyStatusChangeListener(CATEGORY_LISTENERS);
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

        if (FocusNotifier.class.isAssignableFrom(getTestClass())) {
            createFocusListener(CATEGORY_LISTENERS);
        }

        if (BlurNotifier.class.isAssignableFrom(getTestClass())) {
            createBlurListener(CATEGORY_LISTENERS);
        }

    }

    private void createValueChangeListener(String category) {

        createBooleanAction("Value change listener", category, false,
                valueChangeListenerCommand);
    }

    private void createReadOnlyStatusChangeListener(String category) {

        createBooleanAction("Read only status change listener", category,
                false, readonlyStatusChangeListenerCommand);
    }

    private void createFocusListener(String category) {
        createBooleanAction("Focus listener", category, false,
                focusListenerCommand);

    }

    private void createBlurListener(String category) {
        createBooleanAction("Blur listener", category, false,
                blurListenerCommand);

    }

    protected Command<T, Boolean> valueChangeListenerCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ValueChangeListener) AbstractFieldTest.this);
            } else {
                c.removeListener((ValueChangeListener) AbstractFieldTest.this);
            }
        }
    };
    protected Command<T, Boolean> readonlyStatusChangeListenerCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((ReadOnlyStatusChangeListener) AbstractFieldTest.this);
            } else {
                c.removeListener((ReadOnlyStatusChangeListener) AbstractFieldTest.this);
            }
        }
    };
    protected Command<T, Boolean> focusListenerCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            if (value) {
                ((FocusNotifier) c).addListener(AbstractFieldTest.this);
            } else {
                ((FocusNotifier) c).removeListener(AbstractFieldTest.this);
            }
        }
    };
    protected Command<T, Boolean> blurListenerCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            if (value) {
                ((BlurNotifier) c).addListener(AbstractFieldTest.this);
            } else {
                ((BlurNotifier) c).removeListener(AbstractFieldTest.this);
            }
        }
    };

    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        Object o = event.getProperty().getValue();

        // Distinguish between null and 'null'
        String value = "null";
        if (o != null) {
            value = "'" + o.toString() + "'";
        }

        log(event.getClass().getSimpleName() + ", new value: " + value);
    };

    public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
        log(event.getClass().getSimpleName());
    }

    public void focus(FocusEvent event) {
        log(event.getClass().getSimpleName());
    }

    public void blur(BlurEvent event) {
        log(event.getClass().getSimpleName());
    }
}
