package com.vaadin.tests.components.caption;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;

public class EmptyCaptions extends TestBase {

    @Override
    protected void setup() {
        LegacyTextField tf;

        tf = new LegacyTextField(null, "Null caption");
        addComponent(tf);

        tf = new LegacyTextField("", "Empty caption");
        addComponent(tf);

        tf = new LegacyTextField(" ", "Space as caption");
        addComponent(tf);

        tf = new LegacyTextField(null, "Null caption, required");
        tf.setRequired(true);
        addComponent(tf);
        tf = new LegacyTextField("", "Empty caption, required");
        tf.setRequired(true);
        addComponent(tf);
        tf = new LegacyTextField(" ", "Space as caption, required");
        tf.setRequired(true);
        addComponent(tf);

        tf = new LegacyTextField(null, "Null caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

        tf = new LegacyTextField("", "Empty caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

        tf = new LegacyTextField(" ", "Space as caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

    }

    @Override
    protected String getDescription() {
        return "Null caption should never use space while a non-null caption always should use space.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3846;
    }

}
