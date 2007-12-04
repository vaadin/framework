/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.TabSheet.SelectedTabChangeEvent;

public class TabSheetExample extends CustomComponent implements
        Button.ClickListener, TabSheet.SelectedTabChangeListener {
    TabSheet tabsheet = new TabSheet();
    Button tab1 = new Button("Push this button");
    Label tab2 = new Label("Contents of Second Tab");
    Label tab3 = new Label("Contents of Third Tab");

    TabSheetExample() {
        setCompositionRoot(tabsheet);

        /* Listen for changes in tab selection. */
        tabsheet.addListener(this);

        /* First tab contains a button, for which we listen button click events. */
        tab1.addListener(this);
        tabsheet.addTab(tab1, "First Tab", null);

        /* A tab that is initially invisible. */
        tab2.setVisible(false);
        tabsheet.addTab(tab2, "Second Tab", null);

        /* A tab that is initially disabled. */
        tab3.setEnabled(false);
        tabsheet.addTab(tab3, "Third tab", null);
    }

    public void buttonClick(ClickEvent event) {
        /* Enable the invisible and disabled tabs. */
        tab2.setVisible(true);
        tab3.setEnabled(true);

        /* Change selection automatically to second tab. */
        tabsheet.setSelectedTab(tab2);
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        /*
         * Cast to a TabSheet. This isn't really necessary in this example, as
         * we have only one TabSheet component, but would be useful if there
         * were multiple TabSheets.
         */
        final TabSheet source = (TabSheet) event.getSource();
        if (source == tabsheet) {
            /* If the first tab was selected. */
            if (source.getSelectedTab() == tab1) {
                tab2.setVisible(false);
                tab3.setEnabled(false);
            }
        }
    }
}
