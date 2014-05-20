package com.vaadin.tests.components.table;

import java.text.DecimalFormat;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableToggleVisibility extends AbstractTestCase {

    private static final int[] LENGTHS = new int[] { 20, 22, 10 };

    @Override
    public void init() {

        DecimalFormat format = new DecimalFormat("000");
        Table[] tables = new Table[3];
        Button[] buttons = new Button[3];

        VerticalLayout leftComponent = new VerticalLayout();
        leftComponent.setMargin(true);
        leftComponent.setSpacing(true);

        // Toolbar with buttons to hide or show lists

        HorizontalLayout toolBar = new HorizontalLayout();
        toolBar.setSpacing(true);
        toolBar.setMargin(true);

        leftComponent.addComponent(toolBar);
        leftComponent.setExpandRatio(toolBar, 0.0f);

        // List of trucks -----------------------

        tables[0] = new Table("Trucks");
        tables[0].addContainerProperty("Brand", String.class, null);
        tables[0].addContainerProperty("Model", String.class, null);
        tables[0].addContainerProperty("License Plate", String.class, null);

        for (int i = 1; i < LENGTHS[0]; i++) {
            tables[0].addItem(
                    new Object[] { "MAN", "XYZ", "1-ABC-" + format.format(i) },
                    Integer.valueOf(i));
        }
        tables[0].setPageLength(LENGTHS[0]);

        tables[0].setWidth("100%");
        tables[0].setHeight("100%");
        tables[0].setSelectable(true);

        leftComponent.addComponent(tables[0]);
        leftComponent.setExpandRatio(tables[0], 1.0f);

        // List of trailers ----------------------

        tables[1] = new Table("Trailers");
        tables[1].addContainerProperty("Type", String.class, null);
        tables[1].addContainerProperty("License Plate", String.class, null);
        for (int i = 1; i < LENGTHS[1]; i++) {
            tables[1].addItem(
                    new Object[] { "Cooler", "1-QQQ-" + format.format(i) },
                    Integer.valueOf(i));
        }
        tables[1].setPageLength(LENGTHS[1]);

        tables[1].setWidth("100%");
        tables[1].setHeight("100%");
        tables[1].setSelectable(true);

        leftComponent.addComponent(tables[1]);
        leftComponent.setExpandRatio(tables[1], 1.0f);

        // List of drivers ------------------------

        tables[2] = new Table("Drivers");
        tables[2].addContainerProperty("First Name", String.class, null);
        tables[2].addContainerProperty("Last Name", String.class, null);
        tables[2].addContainerProperty("HR ID", String.class, null);
        for (int i = 1; i < LENGTHS[2]; i++) {
            tables[2].addItem(
                    new Object[] { "King", "Vabis", "HR-" + format.format(i) },
                    Integer.valueOf(i));
        }
        tables[2].setPageLength(LENGTHS[2]);

        tables[2].setWidth("100%");
        tables[2].setHeight("100%");
        tables[2].setSelectable(true);

        leftComponent.addComponent(tables[2]);
        leftComponent.setExpandRatio(tables[2], 1.0f);

        leftComponent.setWidth("100%");

        HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setFirstComponent(leftComponent);

        VerticalLayout rightComponent = new VerticalLayout();
        rightComponent.setMargin(true);
        rightComponent.addComponent(new Label("Left blank!"));
        split.setSecondComponent(rightComponent);

        split.setSizeFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.addComponent(split);
        mainLayout.setExpandRatio(split, 1.0f);

        LegacyWindow mainWindow = new LegacyWindow("Visibilitybug Application",
                mainLayout);
        mainWindow.setSizeFull();

        setMainWindow(mainWindow);

        // complete toolbar

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new ToggleButton(tables[i]);
            toolBar.addComponent(buttons[i]);
        }

    }

    // Button to switch the visibility of a table.

    private static class ToggleButton extends Button {

        private Table table;

        private ToggleButton(Table table) {
            this.table = table;

            setCaption("- " + table.getCaption());

            addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {

                    boolean wasVisible = ToggleButton.this.table.isVisible();

                    ToggleButton.this.table.setVisible(!wasVisible);
                    setCaption((wasVisible ? "+ " : "- ")
                            + ToggleButton.this.table.getCaption());
                    setDescription((wasVisible ? "Show " : "Hide ")
                            + "the list with "
                            + ToggleButton.this.table.getCaption());

                }
            });
        }

    }

    @Override
    protected String getDescription() {
        return "Test for hiding and showing tables. Click a button to show/hide one of the tables. The tables are all 100% wide and should be rendered the same way after being hidden and shown again.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6494;
    }

}
