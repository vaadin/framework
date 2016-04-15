package com.vaadin.tests.components.combobox;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ComboBoxInPopup extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeUndefined();
        final Window w = new Window();
        w.setContent(layout);
        layout.addComponent(createComboBox());
        Button close = new Button("Close window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                w.close();
            }
        });
        close.setClickShortcut(KeyCode.ESCAPE, null);
        layout.addComponent(close);

        getLayout().getUI().addWindow(w);

    }

    private Component createComboBox() {
        ComboBox cb = new ComboBox("A combo box");

        cb.addItem("Yes");
        cb.addItem("No");
        cb.addItem("Maybe");
        return cb;
    }

    @Override
    protected String getDescription() {
        return "Escape is a shortcut for the close button. Pressing escape when the popup is open should cause only the popup to close, not the window.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6978;
    }

}
