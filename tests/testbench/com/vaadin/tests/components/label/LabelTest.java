package com.vaadin.tests.components.label;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Label;

public class LabelTest extends AbstractComponentTest<Label> implements
        ValueChangeListener {

    private Command<Label, Object> setValueCommand = new Command<Label, Object>() {

        public void execute(Label c, Object value, Object data) {
            c.setValue(value);
        }
    };

    private Command<Label, Boolean> valueChangeListenerCommand = new Command<Label, Boolean>() {
        public void execute(Label c, Boolean value, Object data) {
            if (value) {
                c.addListener(LabelTest.this);
            } else {
                c.removeListener(LabelTest.this);

            }
        }
    };

    private Command<Label, Integer> contentModeCommand = new Command<Label, Integer>() {
        public void execute(Label c, Integer value, Object data) {
            c.setContentMode(value);
        }
    };

    @Override
    protected Class<Label> getTestClass() {
        return Label.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createContentModeSelect(CATEGORY_FEATURES);
        createValueSelect(CATEGORY_FEATURES);
        createValueChangeListener(CATEGORY_LISTENERS);
    }

    private void createValueSelect(String category) {
        String subCategory = "Set text value";
        createCategory(subCategory, category);
        List<String> values = new ArrayList<String>();
        values.add("Test");
        values.add("A little longer value");
        values.add("A very long value with very much text. All in all it is 74 characters long");
        values.add("<b>Bold</b>");
        values.add("<div style=\"height: 70px; width: 15px; border: 1px dashed red\">With border</div>");

        createClickAction("(empty string)", subCategory, setValueCommand, "");
        createClickAction("(null)", subCategory, setValueCommand, null);
        for (String value : values) {
            createClickAction(value, subCategory, setValueCommand, value);
        }
    }

    @SuppressWarnings("deprecation")
    private void createContentModeSelect(String category) {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<String, Integer>();
        options.put("Text", Label.CONTENT_TEXT);
        options.put("Preformatted", Label.CONTENT_PREFORMATTED);
        options.put("Raw", Label.CONTENT_RAW);
        options.put("UIDL", Label.CONTENT_UIDL);
        options.put("XHTML", Label.CONTENT_XHTML);
        options.put("XML", Label.CONTENT_XML);

        createSelectAction("Content mode", category, options, "Text",
                contentModeCommand);
    }

    private void createValueChangeListener(String category) {
        createBooleanAction("Value change listener", category, false,
                valueChangeListenerCommand);
    }

    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        Object o = event.getProperty().getValue();

        // Distinguish between null and 'null'
        String value = "null";
        if (o != null) {
            value = "'" + o.toString() + "'";
        }

        log(event.getClass().getSimpleName() + ", new value: " + value);
    };

}
