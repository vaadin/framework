package com.vaadin.tests.components.textfield;

import com.vaadin.data.HasValue.ValueChange;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasValueChangeMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TextFieldsValueChangeMode extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        log.setNumberLogRows(false);
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(createFields(TextField.class));
        hl.addComponent(createFields(TextArea.class));
        hl.addComponent(createFields(RichTextArea.class));
        addComponent(hl);
    }

    private Component createFields(Class<?> fieldClass) {
        VerticalLayout vl = new VerticalLayout();
        String id = fieldClass.getSimpleName().toLowerCase();
        try {
            AbstractField<String> f = (AbstractField<String>) fieldClass
                    .newInstance();
            f.setId(id + "-default");
            f.setCaption(f.getId());
            f.addValueChangeListener(this::logValueChange);

            vl.addComponent(f);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            AbstractField<String> eager = (AbstractField<String>) fieldClass
                    .newInstance();
            eager.setId(id + "-eager");
            eager.setCaption(eager.getId());

            eager.addValueChangeListener(this::logValueChange);
            ((HasValueChangeMode) eager)
                    .setValueChangeMode(ValueChangeMode.EAGER);
            vl.addComponent(eager);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            AbstractField<String> timeout = (AbstractField<String>) fieldClass
                    .newInstance();
            timeout.setId(id + "-timeout");
            timeout.setCaption(timeout.getId());
            timeout.addValueChangeListener(this::logValueChange);
            ((HasValueChangeMode) timeout)
                    .setValueChangeMode(ValueChangeMode.TIMEOUT);
            ((HasValueChangeMode) timeout).setValueChangeTimeout(1000);
            vl.addComponent(timeout);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return vl;
    }

    private void logValueChange(ValueChange<String> listener) {
        AbstractField<String> field = (AbstractField<String>) listener
                .getConnector();
        String msg = "Value change event for " + field.getCaption()
                + ", new value: '" + listener.getValue() + "'";
        if (field instanceof AbstractTextField) {
            msg += " Cursor at index:"
                    + ((AbstractTextField) field).getCursorPosition();
        }
        log(msg);

    }

    @Override
    protected String getTestDescription() {
        return "Simple TextChangeEvent test cases.";
    }

    /**
     * "Autosuggest"
     *
     * Known issue is timing if suggestion comes while typing more content. IMO
     * we will not support this kind of features in default TextField, but
     * hopefully make it easily extendable to perfect suggest feature. MT
     * 2010-10
     *
     */
    private class VaadinDeveloperNameField extends TextField {
        private String[] names = new String[] { "Matti Tahvonen",
                "Marc Englund", "Joonas Lehtinen", "Jouni Koivuviita",
                "Marko Grönroos", "Artur Signell" };

        public VaadinDeveloperNameField() {
            setCaption("Start typing 'old' Vaadin developers.");
            addValueChangeListener(listener -> {
                boolean atTheEndOfText = listener.getValue()
                        .length() == getCursorPosition();
                String match = findMatch(listener.getValue());
                if (match != null) {
                    setStyleName("match");
                    String curText = listener.getValue();
                    int matchlenght = curText.length();
                    // autocomplete if caret is at the end of the text
                    if (atTheEndOfText) {
                        suggest(match, matchlenght);
                    }
                } else {
                    setStyleName("nomatch");
                }
            });
            setStyleName("nomatch");
        }

        @Override
        public void attach() {
            super.attach();
            TestUtils.injectCSS(getUI(), ".match { background:green ;} "
                    + ".nomatch {background:red;}");
        }

        private void suggest(String match, int matchlenght) {
            setValue(match);
            setSelection(matchlenght, match.length() - matchlenght);
        }

        private String findMatch(String currentTextContent) {
            if (currentTextContent.length() > 0) {
                for (int i = 0; i < names.length; i++) {
                    if (names[i].startsWith(currentTextContent)) {
                        return names[i];
                    }
                }
            }
            return null;
        }
    }

}
