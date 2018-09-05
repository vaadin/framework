package com.vaadin.tests.components.table;

import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

/**
 * Test to see if selecting and deselecting a table row after select range has
 * been removed.
 *
 * @since 7.1.13
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
public class AddSelectionToRemovedRange extends AbstractReindeerTestUI {

    @Override
    @SuppressWarnings("unchecked")
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        final Table table = new Table();
        table.setMultiSelect(true);
        table.setSelectable(true);
        table.addContainerProperty("value", String.class, "");

        for (int i = 0; i < 100; i++) {
            table.addItem(i);
            table.getContainerProperty(i, "value").setValue("value " + i);
        }

        layout.addComponent(table);

        Button button = new Button("Remove");
        button.addClickListener(event -> {
            Set<Integer> selected = (Set<Integer>) table.getValue();

            for (Integer item : selected) {
                if (null == item) {
                    new Notification("ERROR",
                            "Table value has null in Set of selected items!",
                            Type.ERROR_MESSAGE).show(getPage());
                }
                table.removeItem(item);
            }
        });

        layout.addComponent(button);

    }

    @Override
    protected String getTestDescription() {
        return "Verify that VScrollTable does not return bad ranges when ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13353;
    }

}
