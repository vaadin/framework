package com.vaadin.tests.components.textfield;

import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class TextChangeEvents extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        TextField textField = new TextField("Default");

        ValueChangeListener<String> listener = event -> log(
                "Text change event for  " + event.getComponent().getCaption()
                        + ", text content currently:'" + event.getValue()
                        + "' Cursor at index:"
                        + ((AbstractTextField) event.getSource())
                                .getCursorPosition());

        textField.addValueChangeListener(listener);
        addComponent(textField);

        TextField eager = new TextField("Eager");
        eager.addValueChangeListener(listener);
        eager.setValueChangeMode(ValueChangeMode.EAGER);
        addComponent(eager);

        TextField timeout = new TextField("Timeout 3s");
        timeout.addValueChangeListener(listener);
        timeout.setValueChangeMode(ValueChangeMode.EAGER);
        timeout.setValueChangeTimeout(3000);
        addComponent(timeout);

        TextArea textArea = new TextArea("Default text area");
        textArea.addValueChangeListener(listener);
        addComponent(textArea);

        TextArea textAreaTimeout = new TextArea("Timeout 3s");
        textAreaTimeout.addValueChangeListener(listener);
        textAreaTimeout.setValueChangeMode(ValueChangeMode.TIMEOUT);
        textAreaTimeout.setValueChangeTimeout(3000);
        addComponent(textAreaTimeout);

        VaadinDeveloperNameField vd = new VaadinDeveloperNameField();
        vd.addValueChangeListener(listener);
        addComponent(vd);
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
    private class VaadinDeveloperNameField extends TextField
            implements HasValue.ValueChangeListener<String> {
        private String[] names = new String[] { "Matti Tahvonen",
                "Marc Englund", "Joonas Lehtinen", "Jouni Koivuviita",
                "Marko Grönroos", "Artur Signell" };

        public VaadinDeveloperNameField() {
            setCaption("Start typing 'old' Vaadin developers.");
            addValueChangeListener(this);
            setStyleName("nomatch");
        }

        @Override
        public void attach() {
            super.attach();
            TestUtils.injectCSS(getUI(), ".match { background:green ;} "
                    + ".nomatch {background:red;}");
        }

        @Override
        public void valueChange(HasValue.ValueChangeEvent<String> event) {
            boolean atTheEndOfText = event.getValue()
                    .length() == getCursorPosition();
            String match = findMatch(event.getValue());
            if (match != null) {
                setStyleName("match");
                String curText = event.getValue();
                int matchlenght = curText.length();
                // autocomplete if garret is at the end of the text
                if (atTheEndOfText) {
                    suggest(match, matchlenght);
                }
            } else {
                setStyleName("nomatch");
            }
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
