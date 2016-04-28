package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TabSheet;

public class Ticket2180 extends LegacyApplication {

    private LegacyWindow mainWindow;
    private TabSheet tabSheet;

    @Override
    public void init() {
        mainWindow = new LegacyWindow("Tabsheet should cause scrollbars");
        setMainWindow(mainWindow);
        // mainWindow.getLayout().setSizeFull();
        tabSheet = new TabSheet();
        // tabSheet.setWidth("100%");
        Button button = new Button("Blah");
        button.setWidth("100%");
        Label label1 = new Label("Lorem ipsum");
        Label label2 = new Label("Lorem");
        Label label3 = new Label(
                "Lorema jsdfhak sjdfh kajsdh fkajhd kfjah dkfjah ksfdjh kajsfh kj 1 2 3 4 5 6 7 8 9 10");

        label3.setWidth("800px");
        tabSheet.addTab(label1, "Tab 1", null);
        tabSheet.addTab(label2, "Tab 2", null);
        tabSheet.addTab(label3, "Tab 3", null);
        tabSheet.addTab(new Label("a"), "Tab 4", null);
        tabSheet.addTab(new Label("a"), "Tab 5", null);
        tabSheet.addTab(new Label("a"), "Tab 6", null);
        // mainWindow.addComponent(new Label("123"));
        mainWindow.addComponent(tabSheet);
        mainWindow.addComponent(button);
        // mainWindow.addComponent(new Label("abc"));
    }

}
