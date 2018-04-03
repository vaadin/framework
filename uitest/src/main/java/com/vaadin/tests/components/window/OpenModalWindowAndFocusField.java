package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.TextArea;

public class OpenModalWindowAndFocusField extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button button = new Button("Open modal and focus textarea");
        button.setId("openFocus");
        button.addClickListener(event -> open(true));
        addComponent(button);

        button = new Button("Only open modal");
        button.setId("open");
        button.addClickListener(event -> open(false));
        addComponent(button);
    }

    private void open(boolean focus) {
        Window wind = new Window();
        wind.setModal(true);
        TextArea ta = new TextArea();
        wind.setContent(ta);
        addWindow(wind);
        if (focus) {
            ta.focus();
        }
    }
}
