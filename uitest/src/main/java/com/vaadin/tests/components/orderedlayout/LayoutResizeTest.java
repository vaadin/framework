package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.Reindeer;

public class LayoutResizeTest extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        HorizontalSplitPanel split1 = new HorizontalSplitPanel();
        split1.setSizeFull();
        addComponent(split1);

        VerticalLayout left = new VerticalLayout();
        left.setSizeFull();
        split1.setFirstComponent(left);

        left.setSpacing(true);
        left.setMargin(true);

        left.addComponent(new Label("<h2>Layout resize test</h2>",
                ContentMode.HTML));

        Button resize = new Button("Resize to 700x400",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        event.getButton()
                                .getUI()
                                .getPage()
                                .getJavaScript()
                                .execute(
                                        "setTimeout(function() {window.resizeTo(700,400)}, 500)");
                    }
                });
        left.addComponent(resize);

        resize = new Button("Resize to 900x600", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                event.getButton()
                        .getUI()
                        .getPage()
                        .getJavaScript()
                        .execute(
                                "setTimeout(function() {window.resizeTo(900,600)}, 500)");
            }
        });
        left.addComponent(resize);

        left.addComponent(new Label(
                "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Proin vel ante a orci tempus eleifend ut et magna. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus luctus urna sed urna ultricies."));

        Table table1 = new Table();
        table1.setSizeFull();
        table1.addContainerProperty("Column", String.class, "");
        for (int i = 1; i <= 100; i++) {
            table1.addItem(new Object[] { "Value " + i }, i);
        }
        left.addComponent(table1);
        left.setExpandRatio(table1, 1);

        VerticalSplitPanel split2 = new VerticalSplitPanel();
        split2.setSizeFull();
        split1.setSecondComponent(split2);

        Table table2 = new Table();
        table2.setSizeFull();
        table2.addContainerProperty("Column 1", String.class, "");
        table2.addContainerProperty("Column 2", String.class, "");
        table2.addContainerProperty("Column 3", String.class, "");
        table2.addContainerProperty("Column 4", String.class, "");
        for (int i = 1; i <= 100; i++) {
            table2.addItem(new Object[] { "Value " + i, "Value " + i,
                    "Value " + i, "Value " + i }, i);
        }
        split2.setFirstComponent(table2);

        VerticalLayout rows = new VerticalLayout();
        rows.setWidth("100%");
        rows.setSpacing(true);
        rows.setMargin(true);
        for (int i = 1; i <= 100; i++) {
            rows.addComponent(getRow(i));
        }
        split2.setSecondComponent(rows);
    }

    private HorizontalLayout getRow(int i) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth("100%");
        row.setSpacing(true);

        Embedded icon = new Embedded(null, new ThemeResource(
                "../runo/icons/32/document.png"));
        row.addComponent(icon);
        row.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);

        Label text = new Label(
                "Row content #"
                        + i
                        + ". In pellentesque faucibus vestibulum. Nulla at nulla justo, eget luctus tortor. Nulla facilisi. Duis aliquet.");
        row.addComponent(text);
        row.setExpandRatio(text, 1);

        Button button = new Button("Edit");
        button.addStyleName(Reindeer.BUTTON_SMALL);
        row.addComponent(button);
        row.setComponentAlignment(button, Alignment.MIDDLE_LEFT);

        button = new Button("Delete");
        button.addStyleName(Reindeer.BUTTON_SMALL);
        row.addComponent(button);
        row.setComponentAlignment(button, Alignment.MIDDLE_LEFT);

        return row;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
