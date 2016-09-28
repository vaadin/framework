package com.vaadin.v7.tests.core;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;

public class SpecialCharactersEncodingUI extends AbstractTestUI {

    public static String textWithZwnj = "\ufeffछुट्‌याउनेछन्  क्ष  क्‌ष  क्‍ष";

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menubar = new MenuBar();
        menubar.setId("menubar");
        addComponent(menubar);
        menubar.addItem(textWithZwnj, null);

        Label label = new Label(textWithZwnj);
        label.setId("label");
        addComponent(label);

        TextField f = new TextField("Textfield", textWithZwnj);
        f.setId("textfield");
        addComponent(f);

    }

}