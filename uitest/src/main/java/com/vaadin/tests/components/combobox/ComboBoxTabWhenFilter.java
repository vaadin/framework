package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.TextField;

/**
 * A test case for typing in combo box input field fast plus then press TAB.
 * When type fast and then press tab didn't add new item. Uses SlowComboBox,
 * which has a delay in setVariables method
 */
public class ComboBoxTabWhenFilter extends AbstractReindeerTestUI {
    public static final String DESCRIPTION = "Adding new item by typing fast plus then press TAB, very quickly, should add new item and change focus.";

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
        SlowComboBox comboBox = new SlowComboBox();
        comboBox.setNullSelectionAllowed(false);
        comboBox.setImmediate(true);
        Container container = createContainer();
        comboBox.setContainerDataSource(container);
        comboBox.setNewItemsAllowed(true);
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        layout.addComponent(comboBox);
        layout.addComponent(new TextField());
    }

    private IndexedContainer createContainer() {
        IndexedContainer container = new IndexedContainer();
        for (int i = 0; i < 100000; ++i) {
            container.addItem("Item " + i);
        }
        return container;
    }

    @Override
    protected String getTestDescription() {
        return DESCRIPTION;
    }

    @Override
    protected Integer getTicketNumber() {
        return 12325;
    }

}
