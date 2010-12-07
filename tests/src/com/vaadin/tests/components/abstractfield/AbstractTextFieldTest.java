package com.vaadin.tests.components.abstractfield;

import java.util.LinkedHashMap;

import com.vaadin.ui.AbstractTextField;

public abstract class AbstractTextFieldTest<T extends AbstractTextField>
        extends AbstractFieldTest<T> {

    private Command<T, Integer> maxlengthCommand = new Command<T, Integer>() {

        public void execute(T c, Integer value, Object data) {
            c.setMaxLength(value);
        }
    };
    private Command<T, Boolean> nullSelectionAllowedCommand = new Command<T, Boolean>() {

        public void execute(T c, Boolean value, Object data) {
            c.setNullSettingAllowed(value);

        }
    };
    private Command<T, String> nullRepresentationCommand = new Command<T, String>() {

        public void execute(T c, String value, Object data) {
            c.setNullRepresentation(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();

        createSetTextValueAction(CATEGORY_ACTIONS);

        createNullSettingAllowedAction(CATEGORY_FEATURES);
        createNullRepresentationAction(CATEGORY_FEATURES);
        createMaxLengthAction(CATEGORY_FEATURES);
    }

    private void createNullSettingAllowedAction(String category) {
        createBooleanAction("Null selection allowed", category, true,
                nullSelectionAllowedCommand);
    }

    private void createNullRepresentationAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put("null", "null");
        options.put("This is empty", "This is empty");
        options.put("- Nothing -", "- Nothing -");
        createSelectAction("Null representation", category, options, "null",
                nullRepresentationCommand);
    }

    private void createMaxLengthAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(100);
        options.put("-", -1);
        createSelectAction("Max length", category, options, "-",
                maxlengthCommand);

    }
}
