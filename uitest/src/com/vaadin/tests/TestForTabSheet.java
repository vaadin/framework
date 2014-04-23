package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

public class TestForTabSheet extends CustomComponent implements
        Button.ClickListener, TabSheet.SelectedTabChangeListener {
    TabSheet tabsheet = new TabSheet();
    Button tab1_root = new Button("Push this button");
    Label tab2_root = new Label("Contents of Second Tab");
    Label tab3_root = new Label("Contents of Third Tab");

    TestForTabSheet() {
        setCompositionRoot(tabsheet);

        tabsheet.addListener(this);

        /* Listen for button click events. */
        tab1_root.addListener(this);
        tabsheet.addTab(tab1_root, "First Tab", null);

        /* A tab that is initially disabled. */
        tab2_root.setEnabled(false);
        tabsheet.addTab(tab2_root, "Second Tab", null);

        /* A tab that is initially disabled. */
        tab3_root.setEnabled(false);
        tabsheet.addTab(tab3_root, "Third tab", null);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        System.out.println("tab2=" + tab2_root.isEnabled() + " tab3="
                + tab3_root.isEnabled());
        tab2_root.setEnabled(true);
        tab3_root.setEnabled(true);
    }

    @Override
    public void selectedTabChange(SelectedTabChangeEvent event) {
        /*
         * Cast to a TabSheet. This isn't really necessary in this example, as
         * we have only one TabSheet component, but would be useful if there
         * were multiple TabSheets.
         */
        TabSheet source = (TabSheet) event.getSource();
        if (source == tabsheet) {
            /* If the first tab was selected. */
            if (source.getSelectedTab() == tab1_root) {
                System.out.println("foo");
                tab2_root.setEnabled(false);
                tab3_root.setEnabled(false);
            }
        }
    }
}
