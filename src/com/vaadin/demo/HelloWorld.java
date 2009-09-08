package com.vaadin.demo;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class HelloWorld extends com.vaadin.Application implements ClickListener {
    private static final int TABS_COUNT = 3;
    private TabSheet tabSheet;
    private Label[] label = new Label[TABS_COUNT];
    private Tab[] tab = new Tab[TABS_COUNT];

    @Override
    public void init() {
        setMainWindow(new Window("TabSheet Demo", createMainLayout()));
    }

    private VerticalLayout createMainLayout() {
        VerticalLayout layout = new VerticalLayout();

        tabSheet = new TabSheet();
        for (int i = 1; i <= TABS_COUNT; i++) {
            label[i - 1] = new Label("Tab " + i);
            tab[i - 1] = tabSheet.addTab(label[i - 1], "Tab " + i, null);
            tab[i - 1].setEnabled(false);
        }

        layout.addComponent(tabSheet);
        Button btn = new Button("Enable and activate tab");
        btn.addListener(this);
        layout.addComponent(btn);
        return layout;
    }

    public void buttonClick(ClickEvent event) {
        for (int i = 0; i < TABS_COUNT; i++) {
            tab[i].setEnabled(true);
        }
        tabSheet.setSelectedTab(label[0]);
    }
}
