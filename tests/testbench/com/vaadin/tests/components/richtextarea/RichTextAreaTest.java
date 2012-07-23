package com.vaadin.tests.components.richtextarea;

import java.util.LinkedHashMap;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaTest extends AbstractFieldTest<RichTextArea> {

    @Override
    protected Class<RichTextArea> getTestClass() {
        return RichTextArea.class;
    }

    private Command<RichTextArea, Boolean> nullSelectionAllowedCommand = new Command<RichTextArea, Boolean>() {

        @Override
        public void execute(RichTextArea c, Boolean value, Object data) {
            c.setNullSettingAllowed(value);

        }
    };
    private Command<RichTextArea, String> nullRepresentationCommand = new Command<RichTextArea, String>() {

        @Override
        public void execute(RichTextArea c, String value, Object data) {
            c.setNullRepresentation(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();

        createSetTextValueAction(CATEGORY_ACTIONS);

        createNullSettingAllowedAction(CATEGORY_FEATURES);
        createNullRepresentationAction(CATEGORY_FEATURES);
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

}
