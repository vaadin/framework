/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.book;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

public class TabSheetExample extends CustomComponent implements
        Button.ClickListener, TabSheet.SelectedTabChangeListener {
    TabSheet tabsheet = new TabSheet();
    Button tab1 = new Button("Push this button");
    Label tab2 = new Label("Contents of Second Tab");
    Label tab3 = new Label("Contents of Third Tab");

    TabSheetExample() {
        setCompositionRoot(tabsheet);

        // Listen for changes in tab selection.
        tabsheet.addListener(this);

        // First tab contains a button, for which we
        // listen button click events.
        tab1.addListener(this);
        
        // This will cause a selectedTabChange() call.
        tabsheet.addTab(tab1, "First Tab", null);

        // A tab that is initially invisible.
        tabsheet.addTab(tab2, "Second Tab", null);
        tabsheet.getTab(tab2).setVisible(false);

        // A tab that is initially disabled.
        tabsheet.addTab(tab3, "Third tab", null);
        tabsheet.getTab(tab3).setEnabled(false);
    }

    public void buttonClick(ClickEvent event) {
        // Enable the invisible and disabled tabs.
    	tabsheet.getTab(tab2).setVisible(true);
    	tabsheet.getTab(tab3).setEnabled(true);

        // Change selection automatically to second tab.
        tabsheet.setSelectedTab(tab2);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        // Cast to a TabSheet. This isn't really necessary in
    	// this example, as we have only one TabSheet component,
    	// but would be useful if there were multiple TabSheets.
        final TabSheet source = (TabSheet) event.getSource();

        if (source == tabsheet) {
            // If the first tab was selected.
            if (source.getSelectedTab() == tab1) {
            	// The 2. and 3. tabs may not have been set yet.
            	if (tabsheet.getTab(tab2) != null
            		&& tabsheet.getTab(tab3) != null) {
            		tabsheet.getTab(tab2).setVisible(false);
            		tabsheet.getTab(tab3).setEnabled(false);
            	}
            }
        }
    }
}
