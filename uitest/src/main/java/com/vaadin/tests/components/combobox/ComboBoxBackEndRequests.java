package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.combobox.ComboBoxState;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.LoggingItemDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxBackEndRequests extends AbstractTestUI {

    public static final String PAGE_LENGTH_REQUEST_PARAMETER = "pageLength";
    public static final String ITEMS_REQUEST_PARAMETER = "items";
    public static final int DEFAULT_NUMBER_OF_ITEMS = 200;
    public static final int DEFAULT_PAGE_LENGTH = new ComboBoxState().pageLength;

    @Override
    protected void setup(VaadinRequest request) {
        int pageLength = DEFAULT_PAGE_LENGTH;
        int items = DEFAULT_NUMBER_OF_ITEMS;
        if (request.getParameter(PAGE_LENGTH_REQUEST_PARAMETER) != null) {
            pageLength = Integer.parseInt(request
                    .getParameter(PAGE_LENGTH_REQUEST_PARAMETER).toString());
        }
        if (request.getParameter(ITEMS_REQUEST_PARAMETER) != null) {
            items = Integer.parseInt(
                    request.getParameter(ITEMS_REQUEST_PARAMETER).toString());
        }

        ComboBox<String> cb = new ComboBox<>();
        cb.setPageLength(pageLength);
        VerticalLayout logContainer = new VerticalLayout();
        logContainer.setSpacing(false);

        CheckBox textInputAllowed = new CheckBox("textInputAllowed",
                cb.isTextInputAllowed());
        textInputAllowed.addValueChangeListener(
                event -> cb.setTextInputAllowed(textInputAllowed.getValue()));

        CheckBox emptySelectionAllowed = new CheckBox("emptySelectionAllowed",
                cb.isEmptySelectionAllowed());
        emptySelectionAllowed.addValueChangeListener(event -> cb
                .setEmptySelectionAllowed(emptySelectionAllowed.getValue()));

        CheckBox scrollToSelectedItem = new CheckBox("scrollToSelectedItem",
                cb.isScrollToSelectedItem());
        scrollToSelectedItem.addValueChangeListener(event -> cb
                .setScrollToSelectedItem(scrollToSelectedItem.getValue()));

        VerticalLayout options = new VerticalLayout(textInputAllowed,
                emptySelectionAllowed, scrollToSelectedItem,
                new Button("Swap DataProvider",
                        event -> cb.setDataProvider(new LoggingItemDataProvider(
                                500, logContainer))),
                new Button("Clear logs",
                        event -> logContainer.removeAllComponents()));

        cb.setDataProvider(new LoggingItemDataProvider(items, logContainer));
        Panel panel = new Panel(logContainer);
        addComponent(new HorizontalLayout(cb, panel, options));
    }

    @Override
    protected Integer getTicketNumber() {
        return 8496;
    }

}
