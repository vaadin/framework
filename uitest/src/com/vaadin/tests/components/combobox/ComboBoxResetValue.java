package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxResetValue extends AbstractTestUI {

    protected static final String EMPTY_VALUE = "Empty value";
    protected static final String WITH_SET_NULL_SELECTION_ITEM_ID = "nullSelectionAllowedWithSetNullSelectionItemId";
    protected static final String WITHOUT_NULL_SELECTION_ITEM_ID = "nullSelectionAllowedWithoutNullSelectionItemId";
    protected static final String NULL_SELECTION_NOT_ALLOWED = "nullSelectionNotAllowed";

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox cbNullSelectionAllowedWithSetNullSelectionItemId = getComboBoxWithNullSelectionAllowedWithSetNullSelectionItemId();
        final ComboBox cbNullSelectionAllowedWithoutNullSelectionItemId = getComboBoxWithNullSelectionAllowedWithoutNullSelectionItemId();
        final ComboBox cbNullSelectionNotAllowed = getComboBoxWithNullSelectionNotAllowed();

        Button b = new Button("Reset");
        b.setImmediate(true);
        b.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                cbNullSelectionAllowedWithSetNullSelectionItemId.setValue(null);
                cbNullSelectionAllowedWithoutNullSelectionItemId.setValue(null);
                cbNullSelectionNotAllowed.setValue(null);
            }
        });
        addComponents(new HorizontalLayout(new VerticalLayout(
                cbNullSelectionAllowedWithSetNullSelectionItemId,
                cbNullSelectionAllowedWithoutNullSelectionItemId,
                cbNullSelectionNotAllowed), b));
    }

    protected ComboBox getComboBoxWithNullSelectionAllowedWithSetNullSelectionItemId() {
        ComboBox cb = new ComboBox();
        cb.setId(WITH_SET_NULL_SELECTION_ITEM_ID);
        cb.setImmediate(true);
        cb.setNullSelectionAllowed(true);

        cb.addItem(EMPTY_VALUE);
        cb.setNullSelectionItemId(EMPTY_VALUE);

        cb.addItem(1);
        cb.select(1);
        return cb;
    }

    protected ComboBox getComboBoxWithNullSelectionAllowedWithoutNullSelectionItemId() {
        ComboBox cb = new ComboBox();
        cb.setId(WITHOUT_NULL_SELECTION_ITEM_ID);
        cb.setImmediate(true);
        cb.setNullSelectionAllowed(true);

        cb.addItem(1);
        cb.select(1);
        return cb;
    }

    protected ComboBox getComboBoxWithNullSelectionNotAllowed() {
        ComboBox cb = new ComboBox();
        cb.setId(NULL_SELECTION_NOT_ALLOWED);
        cb.setImmediate(true);
        cb.setNullSelectionAllowed(false);

        cb.addItem(1);
        cb.select(1);
        return cb;
    }

    @Override
    protected Integer getTicketNumber() {
        return 13217;
    }

    @Override
    protected String getTestDescription() {
        return "Tests that reseting (setValue(null), select(null)) of combobox works correctly (removes/updates old selection, also correctly works with filtering)";
    }

}
