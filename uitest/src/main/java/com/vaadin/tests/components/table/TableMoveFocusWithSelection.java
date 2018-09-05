package com.vaadin.tests.components.table;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;

public class TableMoveFocusWithSelection extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table t = new Table();
        t.setImmediate(true);
        t.setId("test-table");
        t.setSizeFull();
        t.setSelectable(true);
        t.addContainerProperty("layout", VerticalLayout.class, null);
        t.addContainerProperty("string", String.class, null);

        for (int i = 0; i < 100; i++) {
            t.addItem(i);
            final VerticalLayout l = new VerticalLayout();
            l.setId("row-" + i);
            l.setHeight(20, Unit.PIXELS);
            l.setData(i);
            l.addLayoutClickListener(event -> {
                if (t.isMultiSelect()) {
                    Set<Object> values = new HashSet<>(
                            (Set<Object>) t.getValue());
                    values.add(l.getData());
                    t.setValue(values);
                } else {
                    t.setValue(l.getData());
                }
            });
            t.getContainerProperty(i, "layout").setValue(l);
            t.getContainerProperty(i, "string").setValue("Item #" + i);
        }
        addComponent(t);

        // Select mode
        Button toggleSelectMode = new Button(
                t.isMultiSelect() ? "Press to use single select"
                        : "Press to use multi select");
        toggleSelectMode.setId("toggle-mode");
        toggleSelectMode.addClickListener(event -> {
            t.setMultiSelect(!t.isMultiSelect());

            event.getButton()
                    .setCaption(t.isMultiSelect() ? "Press to use single select"
                            : "Press to use multi select");
        });

        addComponent(toggleSelectMode);

        Button select5210 = new Button("Select row 5-10",
                event -> t.setValue(Arrays.asList(5, 6, 7, 8, 9, 10)));
        select5210.setId("select-510");
        addComponent(select5210);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Changing selection in single select mode should move focus";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12540;
    }

}
