package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.CellReference;
import com.vaadin.v7.ui.Grid.CellStyleGenerator;
import com.vaadin.v7.ui.Grid.RowReference;
import com.vaadin.v7.ui.Grid.RowStyleGenerator;

@Theme("valo")
public class GridRefreshRow extends AbstractTestUIWithLog {

    private PersonContainer container;
    private Grid grid;

    private boolean styles[] = { false, false, false };

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles()
                .add(".rowstyle td {background-color: lightgreen !important;}");
        getPage().getStyles()
                .add("td.cellstyle {background-color: lightblue !important;}");
        container = PersonContainer.createWithTestData(100);
        container.addNestedContainerBean("address");
        grid = new Grid(container);
        grid.setWidth("800px");
        grid.setRowStyleGenerator(new RowStyleGenerator() {
            @Override
            public String getStyle(RowReference row) {
                int index = container.indexOfId(row.getItemId());
                if (index < 3 && styles[index]) {
                    return "rowstyle";
                }

                return null;
            }
        });
        grid.setCellStyleGenerator(new CellStyleGenerator() {
            @Override
            public String getStyle(CellReference cell) {
                int index = container.indexOfId(cell.getItemId());
                if (index < 3 && styles[index]
                        && "email".equals(cell.getPropertyId())) {
                    return "cellstyle";
                }

                return null;
            }
        });
        addComponent(grid);

        addComponents(new HorizontalLayout(update(0), update(1), update(2)));
        Button refresh10 = new Button("Refresh 0-9", event -> grid
                .refreshRows(container.getItemIds(0, 9).toArray()));
        refresh10.setId("refresh10");
        addComponents(
                new HorizontalLayout(refresh(0), refresh(1), refresh(2),
                        new Button("Refresh non-existant",
                                event -> grid.refreshRows("foobar"))),
                refresh10);
        addComponents(new HorizontalLayout(style(0), style(1), style(2)));
    }

    private Component style(final int i) {
        final CheckBox checkBox = new CheckBox("Style for " + i);
        checkBox.addValueChangeListener(
                event -> styles[i] = checkBox.getValue());
        checkBox.setId("style" + i);
        return checkBox;
    }

    private Component update(final int i) {
        Button button = new Button("Update " + i, event -> {
            Person p = container.getIdByIndex(i);
            p.setFirstName("!" + p.getFirstName());
        });
        button.setId("update" + i);
        return button;
    }

    protected Component refresh(final int i) {
        Button button = new Button("Refresh row " + i,
                event -> grid.refreshRows(container.getIdByIndex(i)));
        button.setId("refresh" + i);
        return button;
    }

}
