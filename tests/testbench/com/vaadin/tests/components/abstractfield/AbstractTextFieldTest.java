package com.vaadin.tests.components.abstractfield;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;

public abstract class AbstractTextFieldTest<T extends AbstractTextField>
        extends AbstractFieldTest<T> implements TextChangeListener {

    private Command<T, Integer> maxlengthCommand = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object data) {
            c.setMaxLength(value);
        }
    };
    private Command<T, Boolean> nullSelectionAllowedCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setNullSettingAllowed(value);

        }
    };
    private Command<T, String> nullRepresentationCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            c.setNullRepresentation(value);
        }
    };

    private Command<T, String> inputPromptCommand = new Command<T, String>() {
        @Override
        public void execute(T c, String value, Object data) {
            c.setInputPrompt(value);
        }
    };

    private Command<T, Boolean> textChangeListenerCommand = new Command<T, Boolean>() {
        @Override
        public void execute(T c, Boolean value, Object data) {
            if (value) {
                c.addListener((TextChangeListener) AbstractTextFieldTest.this);
            } else {
                c.removeListener((TextChangeListener) AbstractTextFieldTest.this);
            }
        }
    };

    private Command<T, Integer> colsCommand = new Command<T, Integer>() {
        @Override
        public void execute(T c, Integer value, Object data) {
            c.setColumns(value);
        }
    };

    private Command<T, TextChangeEventMode> textChangeEventModeCommand = new Command<T, TextChangeEventMode>() {
        @Override
        public void execute(T c, TextChangeEventMode value, Object data) {
            c.setTextChangeEventMode(value);
        }
    };

    private Command<T, Integer> textChangeTimeoutCommand = new Command<T, Integer>() {
        @Override
        public void execute(T c, Integer value, Object data) {
            c.setTextChangeTimeout(value);
        }
    };

    private Command<T, Range> selectionRangeCommand = new Command<T, Range>() {
        @Override
        public void execute(T c, Range value, Object data) {
            c.setSelectionRange(value.getStart(),
                    value.getEnd() - value.getStart());

        }
    };
    private Command<T, Object> selectAllCommand = new Command<T, Object>() {
        @Override
        public void execute(T c, Object value, Object data) {
            c.selectAll();
        }
    };

    private Command<T, Integer> setCursorPositionCommand = new Command<T, Integer>() {

        @Override
        public void execute(T c, Integer value, Object data) {
            c.setCursorPosition(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();

        createSetTextValueAction(CATEGORY_ACTIONS);

        createNullSettingAllowedAction(CATEGORY_FEATURES);
        createNullRepresentationAction(CATEGORY_FEATURES);
        createMaxLengthAction(CATEGORY_FEATURES);

        createInputPromptAction(CATEGORY_FEATURES);
        createColsAction(CATEGORY_STATE);

        createTextChangeListener(CATEGORY_LISTENERS);
        createTextChangeEventModeAction(CATEGORY_FEATURES);
        createTextChangeEventTimeoutAction(CATEGORY_FEATURES);

        createSetTextValueAction(CATEGORY_ACTIONS);
        createCursorPositionAction(CATEGORY_ACTIONS);
        createSelectionRangeAction(CATEGORY_ACTIONS);

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
        LinkedHashMap<String, TextChangeEventMode> options = new LinkedHashMap<String, AbstractTextField.TextChangeEventMode>();
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

    private void createColsAction(String category) {
        LinkedHashMap<String, Integer> options = createIntegerOptions(20);
        createSelectAction("Columns", category, options, "0", colsCommand);
    }

    private void createInputPromptAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        options.put("-", null);
        options.put("Enter a value", "Enter a value");
        options.put("- Click here -", "- Click here -");
        createSelectAction("Input prompt", category, options, "-",
                inputPromptCommand);

    }

    @Override
    public void textChange(TextChangeEvent event) {
        AbstractTextField tf = (AbstractTextField) event.getComponent();
        log("TextChangeEvent: text='" + event.getText() + "', cursor position="
                + event.getCursorPosition() + " (field cursor pos: "
                + tf.getCursorPosition() + ")");

    }

}
