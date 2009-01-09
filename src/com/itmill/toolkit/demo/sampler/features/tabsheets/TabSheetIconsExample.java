package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class TabSheetIconsExample extends VerticalLayout implements
        TabSheet.SelectedTabChangeListener {

    private TabSheet t;

    public TabSheetIconsExample() {
        Label l1 = new Label("There are no previously saved actions.");
        Label l2 = new Label("There are no saved notes.");
        Label l3 = new Label("There are currently no issues.");

        ThemeResource i1 = new ThemeResource("icons/action_save.gif");
        ThemeResource i2 = new ThemeResource("icons/comment_yellow.gif");
        ThemeResource i3 = new ThemeResource("icons/icon_info.gif");

        t = new TabSheet();
        t.setHeight(400, UNITS_PIXELS);
        t.setWidth(400, UNITS_PIXELS);
        t.addTab(l1, "Saved actions", i1);
        t.addTab(l2, "Notes", i2);
        t.addTab(l3, "Issues", i3);
        t.addListener(this);

        addComponent(t);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        String c = t.getTabCaption(event.getTabSheet().getSelectedTab());
        getWindow().showNotification("Selected tab: " + c);
    }
}
