package com.vaadin.tests.components.textfield;

import java.util.LinkedHashMap;

import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.AbstractTextFieldTest;
import com.vaadin.ui.TextField;

public class TextFieldTest extends AbstractTextFieldTest<TextField> implements
        TextChangeListener {

    private Command<TextField, Boolean> secretCommand = new Command<TextField, Boolean>() {
        @SuppressWarnings("deprecation")
        public void execute(TextField c, Boolean value, Object data) {
            c.setSecret(value);
        }
    };

    private Command<TextField, Boolean> wordwrapCommand = new Command<TextField, Boolean>() {
        @SuppressWarnings("deprecation")
        public void execute(TextField c, Boolean value, Object data) {
            c.setWordwrap(value);
        }
    };

    private Command<TextField, Integer> rowsCommand = new Command<TextField, Integer>() {
        @SuppressWarnings("deprecation")
        public void execute(TextField c, Integer value, Object data) {
            c.setRows(value);
        }
    };

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createSecretAction(CATEGORY_FEATURES);
        createWordwrapAction(CATEGORY_FEATURES);
        createRowsAction(CATEGORY_FEATURES);
    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "0", rowsCommand);
    }

    private void createSecretAction(String category) {
        createBooleanAction("Secret", category, false, secretCommand);
    }

    private void createWordwrapAction(String category) {
        createBooleanAction("Wordwrap", category, false, wordwrapCommand);
    }

}
