package com.vaadin.tests.components.tabsheet;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class FirstTabNotVisibleWhenTabsheetNotClipped
        extends AbstractReindeerTestUI {

    private TabSheet.Tab firstNotClippedTab;
    private TabSheet.Tab firstClippedTab;

    @Override
    protected void setup(VaadinRequest request) {
        addButton("Toggle first not clipped tab", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                firstNotClippedTab.setVisible(!firstNotClippedTab.isVisible());
            }
        });
        addComponent(createNotClippedTabSheet());

        addButton("Toggle first clipped tab", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                firstClippedTab.setVisible(!firstClippedTab.isVisible());
            }
        });
        addComponent(createClippedTabSheet());

        addComponent(new Label("VerticalLayout:"));
        addBlock(new VerticalLayout());
        addComponent(new Label("HorizontalLayout:"));
        addBlock(new HorizontalLayout());
    }

    private TabSheet createNotClippedTabSheet() {
        TabSheet notClippedTabSheet = new TabSheet();
        for (int i = 0; i < 2; i++) {
            notClippedTabSheet.addTab(createTabContent(i), "Tab " + i);
        }
        firstNotClippedTab = notClippedTabSheet.getTab(0);
        return notClippedTabSheet;
    }

    private TabSheet createClippedTabSheet() {
        TabSheet clippedTabSheet = new TabSheet();
        for (int i = 0; i < 50; i++) {
            clippedTabSheet.addTab(createTabContent(i), "Tab " + i);
        }
        firstClippedTab = clippedTabSheet.getTab(0);
        return clippedTabSheet;
    }

    private VerticalLayout createTabContent(int index) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("Tab " + index + " Content"));
        return layout;
    }

    private void addBlock(Layout layout) {
        layout.setWidth("300px");

        TabSheet tabsheet = new TabSheet();
        String[] letters = { "A", "B", "C", "D" };
        HashMap<String, TabSheet.Tab> tabMap = new HashMap<>();

        for (String letter : letters) {
            VerticalLayout vLayout = new VerticalLayout();
            vLayout.addComponent(new Label(letter + 1));
            vLayout.addComponent(new Label(letter + 2));
            vLayout.addComponent(new Label(letter + 3));

            tabsheet.addTab(vLayout);
            tabsheet.getTab(vLayout).setCaption("tab " + letter);

            tabMap.put("tab " + letter, tabsheet.getTab(vLayout));
        }

        VerticalLayout vtabLayout = new VerticalLayout();

        for (String letter : letters) {
            Button btntab = new Button("show tab " + letter);
            btntab.setId("tab " + letter);
            btntab.addClickListener(createTabListener(tabMap, tabsheet));
            vtabLayout.addComponent(btntab);
        }

        layout.addComponent(vtabLayout);
        layout.addComponent(tabsheet);
        addComponent(layout);
    }

    private Button.ClickListener createTabListener(
            final HashMap<String, TabSheet.Tab> map, final TabSheet tabsheet) {

        Button.ClickListener clickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                // id of the button is the same as the tab's caption
                String tabName = event.getComponent().getId();

                for (Map.Entry<String, TabSheet.Tab> entry : map.entrySet()) {
                    TabSheet.Tab tab = entry.getValue();

                    if (entry.getKey().equals(tabName)) {
                        tab.setVisible(true);
                        tabsheet.setSelectedTab(tab.getComponent());
                    } else {
                        tab.setVisible(false);
                    }
                }
            }
        };
        return clickListener;
    }

    @Override
    protected Integer getTicketNumber() {
        return 17096;
    }

    @Override
    protected String getTestDescription() {
        return "TabSheet should display re-shown tab if there's room for it";
    }
}
