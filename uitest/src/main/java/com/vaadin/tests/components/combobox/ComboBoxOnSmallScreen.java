package com.vaadin.tests.components.combobox;

import com.vaadin.server.ClassResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.ComboBox;

/**
 * Test UI for issue #11929 where ComboBox suggestion popup hides the ComboBox
 * itself obscuring the text input field.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxOnSmallScreen extends AbstractReindeerTestUI {

    private static final String PID = "captionPID";

    @Override
    protected void setup(VaadinRequest request) {
        addComponents(createComboBox());
    }

    @Override
    protected String getTestDescription() {
        return "Combobox hides what you are typing on small screen";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11929;
    }

    private ComboBox createComboBox() {
        ComboBox cb = new ComboBox();
        cb.addContainerProperty(PID, String.class, "");
        cb.setItemCaptionPropertyId(PID);

        Object selectId = null;

        for (int i = 1; i < 22; ++i) {
            final String v = "Item #" + i;
            Object itemId = cb.addItem();

            if (i == 9) {
                selectId = itemId;
            }

            Item item = cb.getItem(itemId);
            item.getItemProperty(PID).setValue(v);
            int flagIndex = i % 3;
            cb.setItemIcon(itemId, new ClassResource(flagIndex == 0
                    ? "fi_small.png" : flagIndex == 1 ? "fi.gif" : "se.gif"));
        }

        cb.select(selectId);

        return cb;
    }
}
