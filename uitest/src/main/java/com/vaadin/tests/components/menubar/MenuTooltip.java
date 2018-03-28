package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Test to see if tooltips on menu items obscure other items on the menu.
 *
 * @author Vaadin Ltd
 */
public class MenuTooltip extends AbstractReindeerTestUI {
    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {

        addComponent(buildMenu());

        getTooltipConfiguration().setOpenDelay(2000);

    }

    private Component buildMenu() {
        MenuBar menu = new MenuBar();
        MenuItem item = menu.addItem("Menu", null);

        item.addItem("Item 1", null).setDescription("TOOLTIP 1");
        item.addItem("Item 2", null).setDescription("TOOLTIP 2");
        item.addItem("Item 3", null).setDescription("TOOLTIP 3");
        item.addItem("Item 4", null).setDescription("TOOLTIP 4");

        return menu;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Menu item tooltips should not abscure other menu items";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13914;
    }
}
