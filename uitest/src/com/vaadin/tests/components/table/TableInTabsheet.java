package com.vaadin.tests.components.table;

import java.net.MalformedURLException;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class TableInTabsheet extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vPrinc = new VerticalLayout();
        vPrinc.setStyleName(Reindeer.LAYOUT_BLUE);

        vPrinc.addComponent(title());
        vPrinc.addComponent(page());
        vPrinc.addComponent(new Label("Dvlop Tecnologia."));
        setContent(vPrinc);
    }

    private VerticalLayout title() {

        VerticalLayout vP = new VerticalLayout();
        vP.setStyleName(Reindeer.LAYOUT_BLACK);
        Label tit = new Label("<h1> Tab/Table Test</h1>", ContentMode.HTML);
        vP.addComponent(tit);
        return vP;

    }

    private VerticalLayout page() {

        VerticalLayout vP = new VerticalLayout();
        vP.setStyleName(Reindeer.LAYOUT_BLUE);
        TabSheet t = new TabSheet();
        t.setWidth(1000, Unit.PIXELS);

        HorizontalLayout hP = new HorizontalLayout();
        t.addTab(Ranking(), "Ranking");
        try {

            t.addTab(GDocs(""), "Dez 2011");
            t.addTab(GDocs(""), "Jan 2012");
            t.addTab(GDocs(""), "Abr 2012");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hP.addComponent(t);
        vP.addComponent(hP);
        return vP;

    }

    private AbsoluteLayout Ranking() {

        AbsoluteLayout vT = new AbsoluteLayout();
        vT.setHeight(500, Unit.PIXELS);
        vT.setWidth(900, Unit.PIXELS);
        vT.setStyleName(Reindeer.LAYOUT_BLUE);

        final Table table = new Table("Ranking Oficial");

        table.addContainerProperty("Atleta", String.class, null);
        table.addContainerProperty("P", String.class, null);
        table.addContainerProperty("Dez/11", Integer.class, null);
        table.setColumnAlignment("Dez/11", Align.CENTER);
        table.addContainerProperty("Jan/12", Integer.class, null);
        table.setColumnAlignment("Jan/12", Align.CENTER);
        table.addContainerProperty("Abr/12", String.class, null);
        table.addContainerProperty("Total", Integer.class, null);
        table.setColumnAlignment("Total", Align.CENTER);

        table.addItem(new Object[] { "Araujo", "D.1", 8, 8, " ", 16 }, 1);
        table.addItem(new Object[] { "Claudio", "D.2", 2, 10, " ", 12 }, 2);
        table.setPageLength(12);

        vT.addComponent(table, "left: 50px; top: 50px;");
        return vT;

    }

    private VerticalLayout GDocs(String end) throws MalformedURLException {

        VerticalLayout vT = new VerticalLayout();
        vT.setHeight(500, Unit.PIXELS);
        vT.setWidth(900, Unit.PIXELS);

        return vT;

    }

    @Override
    protected String getTestDescription() {
        return "Chaning to a different tab and then back to the first tab should properly render the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8714);
    }

}
