package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabsheetScrollIntoView extends AbstractTestUI {

    public static final String BTN_SELECT_LAST_TAB = "showAndSelectLastTab";

    private TabSheet tabSheetInSplitPanel;
    private HorizontalSplitPanel panel = new HorizontalSplitPanel();

    @Override
    protected void setup(VaadinRequest request) {
        panel.setHeight("200px");
        tabSheetInSplitPanel = new TabSheet();
        tabSheetInSplitPanel.setWidth(100, Unit.PERCENTAGE);
        for (int i = 0; i < 100; i++) {
            tabSheetInSplitPanel.addTab(new Label("Tab " + i), "Tab " + i);
        }

        Layout buttonLayout = new VerticalLayout();

        buttonLayout
                .addComponent(new Button("Hide TabSheet", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSplitPosition(100, Unit.PERCENTAGE);
                        panel.removeComponent(tabSheetInSplitPanel);
                    }
                }));

        Button showLast = new Button("Show TabSheet and select last tab",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSecondComponent(tabSheetInSplitPanel);
                        panel.setSplitPosition(250, Unit.PIXELS);
                        tabSheetInSplitPanel.setSelectedTab(
                                tabSheetInSplitPanel.getComponentCount() - 1);
                    }
                });
        showLast.setId(BTN_SELECT_LAST_TAB);
        buttonLayout.addComponent(showLast);

        buttonLayout.addComponent(new Button(
                "Show TabSheet and select first tab", new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        panel.setSecondComponent(tabSheetInSplitPanel);
                        panel.setSplitPosition(250, Unit.PIXELS);
                        tabSheetInSplitPanel.setSelectedTab(0);
                    }
                }));

        panel.setFirstComponent(buttonLayout);
        panel.setSplitPosition(100, Unit.PERCENTAGE);
        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking \"Show TabSheet and select last tab\" should scroll to the last tab and not disable tab scrolling.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 20052;
    }
}
