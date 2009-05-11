package com.vaadin.tests.components.tabsheet;

import java.util.Date;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Button.ClickEvent;

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

        panel1 = new Panel("Panel initial caption (should also be tab caption)");
        panel1.setSizeFull();
        panel1.getLayout().setSizeFull();
        panel1.addComponent(new Label("This is a panel"));
        tabSheet.addTab(panel1);

        Button button = new Button("Update tab caption");
        button.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                tabSheet.setTabCaption(panel1, "This is a new tab caption "
                        + new Date());
            }
        });

        Button button2 = new Button("Update panel caption");
        button2.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                panel1.setCaption("This is a new panel caption " + new Date());
            }
        });

        addComponent(tabSheet);
        addComponent(button);
        addComponent(button2);
    }
}
