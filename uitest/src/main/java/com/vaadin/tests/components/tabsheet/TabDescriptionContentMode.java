package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabDescriptionContentMode extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        Tab firstTab = tabSheet.addTab(new Label());
        firstTab.setCaption("First tab");
        firstTab.setDescription("First tab description", ContentMode.TEXT);

        Tab secondTab = tabSheet.addTab(new Label());
        secondTab.setCaption("Second tab");
        secondTab.setDescription("Second tab\ndescription",
                ContentMode.PREFORMATTED);

        Tab thirdTab = tabSheet.addTab(new Label());
        thirdTab.setCaption("Third tab");
        thirdTab.setDescription("<b>Third tab description</b>",
                ContentMode.HTML);

        Tab fourthTab = tabSheet.addTab(new Label());
        fourthTab.setCaption("Fourth tab");
        fourthTab.setDescription("Fourth tab description");

        Button changeFourthTabDescription = new Button(
                "Change fourth tab description");
        changeFourthTabDescription.addClickListener(
                event -> fourthTab.setDescription(
                        "Fourth tab description, changed",
                        ContentMode.TEXT));

        addComponents(tabSheet, changeFourthTabDescription);
    }
}
