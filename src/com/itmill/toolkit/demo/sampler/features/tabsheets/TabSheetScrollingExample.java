package com.itmill.toolkit.demo.sampler.features.tabsheets;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class TabSheetScrollingExample extends VerticalLayout implements
        TabSheet.SelectedTabChangeListener {

    private static final ThemeResource icon1 = new ThemeResource(
            "icons/action_save.gif");
    private static final ThemeResource icon2 = new ThemeResource(
            "icons/comment_yellow.gif");
    private static final ThemeResource icon3 = new ThemeResource(
            "icons/icon_info.gif");

    private TabSheet t;

    public TabSheetScrollingExample() {
        // Tab 1 content
        VerticalLayout l1 = new VerticalLayout();
        l1.setMargin(true);
        l1.addComponent(new Label("There are no previously saved actions."));
        // Tab 2 content
        VerticalLayout l2 = new VerticalLayout();
        l2.setMargin(true);
        l2.addComponent(new Label("There are no saved notes."));
        // Tab 3 content
        VerticalLayout l3 = new VerticalLayout();
        l3.setMargin(true);
        l3.addComponent(new Label("There are currently no issues."));
        // Tab 4 content
        VerticalLayout l4 = new VerticalLayout();
        l4.setMargin(true);
        l4.addComponent(new Label("There are no comments."));
        // Tab 5 content
        VerticalLayout l5 = new VerticalLayout();
        l5.setMargin(true);
        l5.addComponent(new Label("There is no new feedback."));

        t = new TabSheet();
        t.setHeight("200px");
        t.setWidth("400px");
        t.addTab(l1, "Saved actions", icon1);
        t.addTab(l2, "Notes", icon2);
        t.addTab(l3, "Issues", icon3);
        t.addTab(l4, "Comments", icon2);
        t.addTab(l5, "Feedback", icon2);
        t.addListener(this);

        addComponent(t);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        String c = t.getTabCaption(event.getTabSheet().getSelectedTab());
        getWindow().showNotification("Selected tab: " + c);
    }
}
