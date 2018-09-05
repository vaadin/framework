package com.vaadin.tests.components.grid;

import java.time.LocalDate;

import com.vaadin.data.Binder.Binding;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;

public class DateFieldHeaderScrollWithFrozen extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Pojo> grid = new Grid<>();

        Column<Pojo, String> col0 = grid.addColumn(s -> {
            return "col0";
        }).setWidth(100).setCaption("col0").setResizable(true)
                .setEditorComponent(new TextField("h0"), Pojo::setCol0);

        Column<Pojo, String> col1 = grid.addColumn(s -> {
            return "col1";
        }).setWidth(100).setCaption("col1")
                .setEditorComponent(new TextField("h1"), Pojo::setCol1);

        Column<Pojo, CssLayout> col2 = grid.addColumn(s -> {
            CssLayout group = new CssLayout(new DateField());
            group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
            return group;
        }).setWidth(200).setCaption("col2")
                .setRenderer(new ComponentRenderer());

        Column<Pojo, String> col3 = grid.addColumn(s -> {
            return "col3";
        }).setWidth(999).setCaption("col3")
                .setEditorComponent(new TextField("h3"), Pojo::setCol3);

        Binding<Pojo, LocalDate> dateFieldBinding = grid.getEditor().getBinder()
                .forField(new DateField()).bind(Pojo::getCol2, Pojo::setCol2);
        col2.setEditorBinding(dateFieldBinding);

        HeaderRow headerRow = grid.addHeaderRowAt(1);
        headerRow.getCell(col0).setComponent(new Label("h0"));
        headerRow.getCell(col1).setComponent(new Label("h1"));
        headerRow.getCell(col2).setComponent(new DateField());
        headerRow.getCell(col3).setComponent(new Label("h3"));

        grid.setItems(new Pojo(), new Pojo(), new Pojo());

        grid.setFrozenColumnCount(2);

        grid.getEditor().setEnabled(true);
        grid.addItemClickListener(e -> {
            grid.getEditor().editRow(e.getRowIndex());
        });

        getLayout().addComponents(grid);
    }

    class Pojo {
        String col0, col1, col3;
        LocalDate col2;

        public String getCol0() {
            return col0;
        }

        public void setCol0(String col0) {
            this.col0 = col0;
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public LocalDate getCol2() {
            return col2;
        }

        public void setCol2(LocalDate col2) {
            this.col2 = col2;
        }

        public String getCol3() {
            return col3;
        }

        public void setCol3(String col3) {
            this.col3 = col3;
        }
    }

}
