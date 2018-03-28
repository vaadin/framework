package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TextField;

/**
 * Test UI for $v-textfield-bevel value in TextField component.
 *
 * @author Vaadin Ltd
 */
@Theme("tests-valo-textfield-bevel")
public class TextFieldBevel extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextField field = new TextField();
        addComponent(field);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14634;
    }

    @Override
    protected String getTestDescription() {
        return "Set v-bevel to 'false' should unset 'v-textfield-bevel' value.";
    }

    @Theme("valo")
    public static class ValoDefaultTextFieldBevel extends TextFieldBevel {

    }

}
