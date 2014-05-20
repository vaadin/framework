package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Window;

public class ModalWindowNativeSelect extends TestBase {

    @Override
    protected void setup() {
        NativeSelect ns = new NativeSelect();

        Window modalWindow = new Window();
        modalWindow.setModal(true);
        modalWindow.center();

        addComponent(ns);
        getMainWindow().addWindow(modalWindow);

    }

    @Override
    protected String getDescription() {
        return "The native select should be behind the modality curtain and user should not be able to interact with it";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4261;
    }

}
