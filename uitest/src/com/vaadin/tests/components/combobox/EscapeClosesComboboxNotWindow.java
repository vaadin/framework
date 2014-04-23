package com.vaadin.tests.components.combobox;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Ticket #12163: when a combo box popup is open in a subwindow, escape should
 * only close it and not the window, also on Safari 6.
 */
public class EscapeClosesComboboxNotWindow extends UI {
    final Window window = new Window("Window");

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final FormLayout content = new FormLayout();
                ComboBox cb = new ComboBox();
                cb.addItem("foo");
                cb.addItem("bar");
                content.addComponent(cb);
                window.setContent(content);
                window.setCloseShortcut(KeyCode.ESCAPE);
                UI.getCurrent().addWindow(window);
            }
        });
        layout.addComponent(button);
    }

}
