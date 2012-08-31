package com.vaadin.tests.components.select;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Select;

public class SelectIconPlacement extends TestBase {
    private static final long serialVersionUID = 1L;

    private Select mySelect;

    @Override
    protected void setup() {
        for (String width : new String[] { null, "200px" }) {
            String icon = "error.png";
            if (width == null) {
                icon = "bullet.png";
            }
            mySelect = new Select("Width: " + (width == null ? "auto" : width));
            String bar = "Only item";
            mySelect.addItem(bar);
            mySelect.setItemIcon(bar, new ThemeResource("common/icons/" + icon
                    + "?w=" + width));
            mySelect.select(bar);
            mySelect.setWidth(width);
            addComponent(mySelect);
        }

    }

    @Override
    protected String getDescription() {
        return "A select with item icons pushes the caption of that item to the right to make room for the icon. It works fine in all browsers except IE8.<br/>"
                + "Upon component render the icon and caption is on top of each others, and it corrects itself when you open the dropdown. <br/><br/>";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3991;
    }

}
