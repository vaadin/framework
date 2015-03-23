package com.vaadin.tests.components.table;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class ZoomScrollbar extends TestBase implements Action.Handler,
        ItemClickListener {

    private Table table;
    private boolean actionHandlerHasActions = false;

    @Override
    public void setup() {
        createTable();
        addComponent(table);

    }

    private void createTable() {
        table = new Table("elektronische Adressen 5");
        table.addContainerProperty("Adresstyp", String.class, null);
        table.addContainerProperty("Verwendung", String.class, null);
        table.addContainerProperty("NummernAdresse", Integer.class, null);

        for (int i = 0; i < 2; i++) {
            table.addItem(new Object[] { "Bayern AG Aktien", "Copernicus",
                    new Integer(i + 100) }, new Integer(1));
        }

        table.setPageLength(0);
        table.setWidth("100%");
    }

    @Override
    protected String getDescription() {
        return "When zooming in the browser, there is an unnecessary horizontal scrollbar which covers the tablecontent.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15164;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        getMainWindow()
                .showNotification("Click using " + event.getButtonName());
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        if (!actionHandlerHasActions) {
            return null;
        }

        return new Action[] { new Action("test") };
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        getMainWindow().showNotification("Action: " + action.getCaption());
    }
}
