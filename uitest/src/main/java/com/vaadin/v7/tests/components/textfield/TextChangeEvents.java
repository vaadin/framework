package com.vaadin.v7.tests.components.textfield;

import com.vaadin.v7.event.FieldEvents.TextChangeEvent;
import com.vaadin.v7.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.textfield.ValueChangeMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.TestUtils;
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

        tf.addValueChangeListener(listener -> {
            l.log("Text change event for  " + tf.getCaption()
                    + ", text content currently:'" + listener.getValue()
                    + "' Cursor at index:" + tf.getCursorPosition());
        });

        getLayout().addComponent(tf);

        TextField eager = new TextField("Eager");
        eager.addValueChangeListener(listener -> {
            l.log("Text change event for  " + eager.getCaption()
                    + ", text content currently:'" + listener.getValue()
                    + "' Cursor at index:" + eager.getCursorPosition());
        });
        eager.setValueChangeMode(ValueChangeMode.EAGER);
        getLayout().addComponent(eager);

        TextField to = new TextField("Timeout 3s");
        to.addValueChangeListener(listener -> {
            l.log("Text change event for  " + to.getCaption()
                    + ", text content currently:'" + listener.getValue()
                    + "' Cursor at index:" + to.getCursorPosition());
        });
        to.setValueChangeMode(ValueChangeMode.TIMEOUT);
        to.setValueChangeTimeout(3000);
        getLayout().addComponent(to);

        TextArea ta = new TextArea("Default text area");
        ta.addValueChangeListener(listener -> {
            l.log("Text change event for  " + ta.getCaption()
                    + ", text content currently:'" + listener.getValue()
                    + "' Cursor at index:" + ta.getCursorPosition());
        });
        getLayout().addComponent(ta);

        VaadinDeveloperNameField vd = new VaadinDeveloperNameField();
        vd.addValueChangeListener(listener -> {
            l.log("Text change event for  " + vd.getCaption()
                    + ", text content currently:'" + listener.getValue()
                    + "' Cursor at index:" + vd.getCursorPosition());
        });
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
