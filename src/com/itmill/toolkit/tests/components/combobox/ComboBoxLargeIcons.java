package com.itmill.toolkit.tests.components.combobox;

import java.util.Date;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxLargeIcons extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    @Override
    protected String getDescription() {
        return "All items in the Combobox has large icons. The size of the dropdown should fit the contents, also when changing pages. The size of the shadow behind the dropdown must also be correctly sized. Note that the image URL change for every restart to keep the browser from using cached images.";
    }

    @Override
    protected void setup() {
        ComboBox cb = new ComboBox();
        cb.addContainerProperty("icon", Resource.class, null);
        cb.setItemIconPropertyId("icon");
        getLayout().addComponent(cb);
        cb.setNullSelectionAllowed(false);
        String[] icons = new String[] { "folder-add", "folder-delete",
                "arrow-down", "arrow-left", "arrow-right", "arrow-up",
                "document-add", "document-delete", "document-doc",
                "document-edit", "document-image", "document-pdf",
                "document-ppt", "document-txt", "document-web", "document-xls",
                "document" };
        for (String icon : icons) {
            Item item = cb.addItem(new Object());
            item.getItemProperty("icon").setValue(
                    new ThemeResource("icons/64/" + icon + ".png?"
                            + new Date().getTime()));
        }

    }
}
