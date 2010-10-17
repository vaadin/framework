package com.vaadin.tests.components.button;

import com.vaadin.tests.components.abstractfield.AbstractFieldTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Buttons2 extends AbstractFieldTestCase<Button> implements
        ClickListener {

    private Command<Button, Boolean> switchModeCommand = new Command<Button, Boolean>() {

        public void execute(Button c, Boolean value, Object data) {
            c.setSwitchMode(value);
        }
    };

    private Command<Button, Boolean> clickListenerCommand = new Command<Button, Boolean>() {

        public void execute(Button c, Boolean value, Object data) {
            if (value) {
                c.addListener((ClickListener) Buttons2.this);
            } else {
                c.removeListener((ClickListener) Buttons2.this);
            }

        }
    };

    @Override
    protected Class<Button> getTestClass() {
        return Button.class;
    }

    @Override
    protected void createActions() {
        super.createActions();

        createFocusListener(CATEGORY_LISTENERS);
        createBlurListener(CATEGORY_LISTENERS);

        createBooleanAction("Switch mode", CATEGORY_FEATURES, false,
                switchModeCommand);
        addClickListener(CATEGORY_LISTENERS);
    }

    private void addClickListener(String category) {
        createBooleanAction("Click listener", category, false,
                clickListenerCommand);

    }

    public void buttonClick(ClickEvent event) {
        log(event.getClass().getSimpleName());
    }
}
