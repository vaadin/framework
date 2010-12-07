package com.vaadin.tests.components.textfield;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.abstractfield.AbstractTextFieldTest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
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

    private Command<TextField, Integer> colsCommand = new Command<TextField, Integer>() {
        public void execute(TextField c, Integer value, Object data) {
            c.setColumns(value);
        }
    };

    private Command<TextField, String> inputPromptCommand = new Command<TextField, String>() {
        public void execute(TextField c, String value, Object data) {
            c.setInputPrompt(value);
        }
    };

    private Command<TextField, Boolean> textChangeListenerCommand = new Command<TextField, Boolean>() {
        public void execute(TextField c, Boolean value, Object data) {
            if (value) {
                c.addListener((TextChangeListener) TextFieldTest.this);
            } else {
                c.removeListener((TextChangeListener) TextFieldTest.this);
            }
        }
    };

    private Command<TextField, TextChangeEventMode> textChangeEventModeCommand = new Command<TextField, TextChangeEventMode>() {
        public void execute(TextField c, TextChangeEventMode value, Object data) {
            c.setTextChangeEventMode(value);
        }
    };

    private Command<TextField, Integer> textChangeTimeoutCommand = new Command<TextField, Integer>() {
        public void execute(TextField c, Integer value, Object data) {
            c.setTextChangeTimeout(value);
        }
    };

    private Command<TextField, Range> selectionRangeCommand = new Command<TextField, Range>() {
        public void execute(TextField c, Range value, Object data) {
            c.setSelectionRange(value.getStart(),
                    value.getEnd() - value.getStart());

        }
    };
    private Command<TextField, Object> selectAllCommand = new Command<TextField, Object>() {
        public void execute(TextField c, Object value, Object data) {
            c.selectAll();
        }
    };

    private Command<TextField, Integer> setCursorPositionCommand = new Command<TextField, Integer>() {

        public void execute(TextField c, Integer value, Object data) {
            c.setCursorPosition(value);
        }
    };

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void createActions() {
        super.createActions();
        createSecretAction(CATEGORY_STATE);
        createWordwrapAction(CATEGORY_STATE);
        createInputPromptAction(CATEGORY_FEATURES);
        createRowsAction(CATEGORY_STATE);
        createColsAction(CATEGORY_STATE);

        createTextChangeListener(CATEGORY_LISTENERS);
        createTextChangeEventModeAction(CATEGORY_FEATURES);
        createTextChangeEventTimeoutAction(CATEGORY_FEATURES);

        createSetTextValueAction(CATEGORY_ACTIONS);
        createCursorPositionAction(CATEGORY_ACTIONS);
        createSelectionRangeAction(CATEGORY_ACTIONS);
    }

    public class Range {
        private int start;
        private int end;

        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return start + "-" + end;
        }
    }

    private void createSelectionRangeAction(String category) {
        List<Range> options = new ArrayList<Range>();
        options.add(new Range(0, 10));
        options.add(new Range(0, 1));
        options.add(new Range(0, 2));
        options.add(new Range(1, 2));
        options.add(new Range(2, 5));
        options.add(new Range(5, 10));

        createCategory("Select range", category);

        createClickAction("All", "Select range", selectAllCommand, null);
        for (Range range : options) {
            createClickAction(range.toString(), "Select range",
                    selectionRangeCommand, range);
        }

    }

    private void createCursorPositionAction(String category) {
        String subCategory = "Set cursor position";
        createCategory(subCategory, category);
        for (int i = 0; i < 20; i++) {
            createClickAction(String.valueOf(i), subCategory,
                    setCursorPositionCommand, Integer.valueOf(i));
        }

    }

    private void createTextChangeEventTimeoutAction(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("0", 0);
        options.put("100ms", 100);
        options.put("500ms", 500);
        options.put("1s", 1000);
        options.put("2s", 2000);
        options.put("5s", 5000);

        createSelectAction("TextChange timeout", category, options, "0",
                textChangeTimeoutCommand);
    }

    private void createTextChangeEventModeAction(String category) {
        LinkedHashMap<String, TextChangeEventMode> options = new LinkedHashMap<String, TextField.TextChangeEventMode>();
        for (TextChangeEventMode m : TextChangeEventMode.values()) {
            options.put(m.toString(), m);
        }

        createSelectAction("TextChange event mode", category, options,
                TextChangeEventMode.EAGER.toString(),
                textChangeEventModeCommand);

    }

    private void createTextChangeListener(String category) {
        createBooleanAction("Text change listener", category, false,
                textChangeListenerCommand);

    }

    private void createRowsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Rows", category, options, "0", rowsCommand);
    }

    private void createColsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Columns", category, options, "0", colsCommand);
    }

    private void createSecretAction(String category) {
        createBooleanAction("Secret", category, false, secretCommand);
    }

    private void createWordwrapAction(String category) {
        createBooleanAction("Wordwrap", category, false, wordwrapCommand);
    }

    private void createInputPromptAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put("Enter a value", "Enter a value");
        options.put("- Click here -", "- Click here -");
        createSelectAction("Input prompt", category, options, "-",
                inputPromptCommand);

    }

    public void textChange(TextChangeEvent event) {
        TextField tf = (TextField) event.getComponent();
        log("TextChangeEvent: text='" + event.getText() + "', cursor position="
                + event.getCursorPosition() + " (field cursor pos: "
                + tf.getCursorPosition() + ")");

    }

}
