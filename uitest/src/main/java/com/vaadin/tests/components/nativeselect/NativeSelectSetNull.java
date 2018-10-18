package com.vaadin.tests.components.nativeselect;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Button;

public class NativeSelectSetNull extends AbstractTestUI {
    public static String EMPTY_SELECTION_TEXT = "Empty Selection";

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<Integer> select = new NativeSelect<>("Native Selection");

        // Add some items
        select.setItems(1, 2, 3, 45, 6);
        select.setEmptySelectionAllowed(true);
        select.setEmptySelectionCaption(EMPTY_SELECTION_TEXT);

        Button changeSelect = new Button("Set value to 3",
                e -> select.setValue(3));
        changeSelect.setId("changeSelect");
        Button setNull = new Button("Set value to null",
                e -> select.setValue(null));
        setNull.setId("setNull");
        Button clear = new Button("Clear", e -> select.clear());
        clear.setId("clear");

        Button disable = new Button("Disable", e -> select
                .setEmptySelectionAllowed(!select.isEmptySelectionAllowed()));
        disable.setId("disable");

        addComponent(select);
        addComponents(changeSelect, setNull, clear, disable);
    }
}
