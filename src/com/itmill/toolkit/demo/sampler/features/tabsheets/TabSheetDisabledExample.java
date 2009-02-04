package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class TabSheetDisabledExample extends VerticalLayout implements
        TabSheet.SelectedTabChangeListener, Button.ClickListener {
    private static final ThemeResource icon1 = new ThemeResource(
            "icons/action_save.gif");
    private static final ThemeResource icon2 = new ThemeResource(
            "icons/comment_yellow.gif");
    private static final ThemeResource icon3 = new ThemeResource(
            "icons/icon_info.gif");

    private TabSheet t;
    private Button toggleEnabled;
    private Button toggleVisible;
    private VerticalLayout l1;
    private VerticalLayout l2;
    private VerticalLayout l3;

    public TabSheetDisabledExample() {
        setSpacing(true);

        // Tab 1 content
        l1 = new VerticalLayout();
        l1.setMargin(true);
        l1.addComponent(new Label("There are no previously saved actions."));
        // Tab 2 content
        l2 = new VerticalLayout();
        l2.setMargin(true);
        l2.addComponent(new Label("There are no saved notes."));
        // Tab 3 content
        l3 = new VerticalLayout();
        l3.setMargin(true);
        l3.addComponent(new Label("There are currently no issues."));

        t = new TabSheet();
        t.setHeight("200px");
        t.setWidth("400px");
        t.addTab(l1, "Saved actions", icon1);
        t.addTab(l2, "Notes", icon2);
        t.addTab(l3, "Issues", icon3);
        t.addListener(this);

        toggleEnabled = new Button("Disable 'Notes' tab");
        toggleEnabled.addListener(this);

        toggleVisible = new Button("Hide 'Issues' tab");
        toggleVisible.addListener(this);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(toggleEnabled);
        hl.addComponent(toggleVisible);

        addComponent(t);
        addComponent(hl);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        String c = t.getTabCaption(event.getTabSheet().getSelectedTab());
        getWindow().showNotification("Selected tab: " + c);
    }

    public void buttonClick(ClickEvent event) {
        if (toggleEnabled.equals(event.getButton())) {
            // toggleEnabled clicked
            l2.setEnabled(!l2.isEnabled());
            toggleEnabled.setCaption((l2.isEnabled() ? "Disable" : "Enable")
                    + " 'Notes' tab");

        } else {
            // toggleVisible clicked
            l3.setVisible(!l3.isVisible());
            toggleVisible.setCaption((l3.isVisible() ? "Hide" : "Show")
                    + " 'Issues' tab");

        }
        t.requestRepaint();
    }
}
