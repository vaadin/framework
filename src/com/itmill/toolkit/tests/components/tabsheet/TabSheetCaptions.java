package com.itmill.toolkit.tests.components.tabsheet;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TabSheet;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class TabSheetCaptions extends TestBase {

    Panel panel1;

    @Override
    protected String getDescription() {
        return "Updating the tabsheet tab text should not change the caption of the component. Click on the button to change the tab text. This must update the tab and not touch the Panel's caption.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2846;
    }

    @Override
    protected void setup() {
        final TabSheet tabSheet = new TabSheet();

        panel1 = new Panel("tab 1");
        panel1.setSizeFull();
        panel1.getLayout().setSizeFull();
        panel1.addComponent(new Label("This is first panel"));
        tabSheet.addTab(panel1);

        Button button = new Button("Update tab caption");
        button.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                tabSheet.setTabCaption(panel1, "This is a new caption");
            }
        });

        addComponent(tabSheet);
        addComponent(button);
    }
}
