package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabsheetScrolling extends TestBase {

    private TabSheet fixedSizeTabSheet;
    private TabSheet autoWideTabSheet;

    @Override
    protected void setup() {
        fixedSizeTabSheet = new TabSheet();
        fixedSizeTabSheet.setHeight("200px");
        fixedSizeTabSheet.setWidth("400px");

        for (int i = 0; i < 100; i++) {
            Button b = new Button("Hide this tab (" + i + ")",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            fixedSizeTabSheet.getTab(event.getButton())
                                    .setVisible(false);
                        }

                    });
            Tab t = fixedSizeTabSheet.addTab(b, "Tab " + i, null);
            if (i % 2 == 0) {
                t.setVisible(false);
            }
        }

        addComponent(fixedSizeTabSheet);

        autoWideTabSheet = new TabSheet();
        autoWideTabSheet.setHeight("200px");
        autoWideTabSheet.setWidth(null);

        for (int i = 0; i < 10; i++) {
            Button b = new Button("Hide this tab (" + i + ")",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            autoWideTabSheet.getTab(event.getButton())
                                    .setVisible(false);
                        }
                    });

            Tab t = autoWideTabSheet.addTab(b, "Tab " + i, null);
            if (i % 2 == 0) {
                t.setVisible(false);

            }
        }

        addComponent(autoWideTabSheet);

    }

    @Override
    protected String getDescription() {
        return "Two tabsheets, upper has fixed width, lower has dynamic width. Every other tab in both tabsheets are hidden (even numbered tabs). Scrolling the upper tab sheet should never display a hidden tab. Hiding a tab in the upper tabsheet should not affect scrolling. Hiding a tab in the lower tabsheet should make the tabsheet width change (auto wide).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3141;
    }

}
