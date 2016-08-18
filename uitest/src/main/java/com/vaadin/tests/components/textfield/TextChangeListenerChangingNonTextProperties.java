package com.vaadin.tests.components.textfield;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.v7.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.v7.ui.TextField;

public class TextChangeListenerChangingNonTextProperties extends TestBase {

    int index = 0;
    String[] styles = { "red", "green", "blue", "cyan", "magenta" };

    private String getNextStyle() {
        return styles[++index % styles.length];
    }

    @Override
    protected void setup() {
        final TextField tf2 = new TextField("Updates width") {
            @Override
            public void attach() {
                super.attach();
                TestUtils.injectCSS(getUI(), ".red { background:red;} "
                        + ".green { background:green;} .blue { background:blue;} .cyan { background:cyan;} .magenta { background:magenta;}");
            }
        };
        tf2.setTextChangeEventMode(TextChangeEventMode.EAGER);
        tf2.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(TextChangeEvent event) {
                tf2.setStyleName(getNextStyle());
            }

        });
        tf2.setImmediate(true);

        addComponent(tf2);
    }

    @Override
    protected String getDescription() {
        return "The color (style name) of field changes on each text change event. This should not disturb typing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6588);
    }

}
