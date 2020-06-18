package com.vaadin.tests.components.combobox;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxReadOnlyPopup extends AbstractReindeerTestUI {

    static final String[] ITEMS = { "First", "Second", "Third" };

    @Override
    protected void setup(VaadinRequest request) {
        String boxLabel = String
                .format("Press 'Q' to toggle ComboBox's read-only mode");
        final ComboBox<String> comboBox = new ComboBox<>(boxLabel);

        comboBox.setItems(ITEMS);
        comboBox.setSelectedItem(ITEMS[0]);

        ShortcutListener shortcutListener = new ShortcutListener("", null,
                ShortcutAction.KeyCode.Q) {
            @Override
            public void handleAction(Object sender, Object target) {
                comboBox.setReadOnly(!comboBox.isReadOnly());
            }
        };
        comboBox.addShortcutListener(shortcutListener);

        addComponents(comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox that turns to read-only mode while expanded "
                + "should have its popup set to hidden.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12021;
    }

}
