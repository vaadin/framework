package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class TextChangeEvents extends TestBase {
    Log l = new Log(10);

    @Override
    protected void setup() {

        TextField tf = new TextField("Default");

        TextChangeListener inputEventListener = new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                l.log("Text change event for  "
                        + event.getComponent().getCaption()
                        + ", text content currently:'" + event.getText()
                        + "' Cursor at index:" + event.getCursorPosition());
            }
        };

        tf.addListener(inputEventListener);

        getLayout().addComponent(tf);

        TextField eager = new TextField("Eager");
        eager.addListener(inputEventListener);
        eager.setTextChangeEventMode(TextChangeEventMode.EAGER);
        getLayout().addComponent(eager);

        TextField to = new TextField("Timeout 3s");
        to.addListener(inputEventListener);
        to.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
        to.setTextChangeTimeout(3000);
        getLayout().addComponent(to);

        TextArea ta = new TextArea("Default text area");
        ta.addListener(inputEventListener);
        getLayout().addComponent(ta);

        TextArea tat = new TextArea("Timeout 3s");
        tat.addListener(inputEventListener);
        tat.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
        tat.setTextChangeTimeout(3000);
        getLayout().addComponent(tat);

        VaadinDeveloperNameField vd = new VaadinDeveloperNameField();
        vd.addListener(inputEventListener);
        getLayout().addComponent(vd);

        getLayout().addComponent(l);
    }

    @Override
    protected String getDescription() {
        return "Simple TextChangeEvent test cases.";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
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
            implements TextChangeListener {
        private String[] names = new String[] { "Matti Tahvonen",
                "Marc Englund", "Joonas Lehtinen", "Jouni Koivuviita",
                "Marko Grönroos", "Artur Signell" };

        public VaadinDeveloperNameField() {
            setCaption("Start typing 'old' Vaadin developers.");
            addListener((TextChangeListener) this);
            setStyleName("nomatch");
        }

        @Override
        public void attach() {
            super.attach();
            TestUtils.injectCSS(getUI(), ".match { background:green ;} "
                    + ".nomatch {background:red;}");
        }

        @Override
        public void textChange(TextChangeEvent event) {
            boolean atTheEndOfText = event.getText()
                    .length() == getCursorPosition();
            String match = findMatch(event.getText());
            if (match != null) {
                setStyleName("match");
                String curText = event.getText();
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
            setSelectionRange(matchlenght, match.length() - matchlenght);
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
