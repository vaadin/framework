package com.vaadin.tests.components.checkbox;

import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;

public class CheckBoxes2 extends AbstractFieldTest<CheckBox> implements
        ClickListener {

    // cannot extend Buttons2 because of Switch mode problems

    @Override
    protected Class<CheckBox> getTestClass() {
        return CheckBox.class;
    }

    private Command<CheckBox, Boolean> switchModeCommand = new Command<CheckBox, Boolean>() {

        public void execute(CheckBox c, Boolean value, Object data) {
            c.setSwitchMode(value);
        }
    };

    private Command<CheckBox, Boolean> clickListenerCommand = new Command<CheckBox, Boolean>() {

        public void execute(CheckBox c, Boolean value, Object data) {
            if (value) {
                c.addListener((ClickListener) CheckBoxes2.this);
            } else {
                c.removeListener((ClickListener) CheckBoxes2.this);
            }

        }
    };

    @Override
    protected void createActions() {
        super.createActions();

        createBooleanAction("Switch mode", CATEGORY_FEATURES, true,
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
