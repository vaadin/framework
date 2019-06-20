package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a Grid with the only editable columns being the column 1 and 3. That
 * will allow us to test that Tab/Shift+Tab skips cells that are not editable.
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridEditorTabSkipsNonEditableCells extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final List<TestBean> items = new ArrayList<>();
        Grid<TestBean> grid = new Grid<TestBean>(TestBean.class);
        for (int i = 0; i < 10; i++) {
            items.add(new TestBean(i));
        }
        grid.setDataProvider(new ListDataProvider<>(items));
        grid.setWidth("100%");
        grid.setHeight("400px");
        grid.getEditor().setEnabled(true);
        grid.getColumn("col1").setEditorComponent(new TextField());
        grid.getColumn("col3").setEditorComponent(new TextField());
        final TextField disabledField = new TextField();
        disabledField.setEnabled(false);
        grid.getColumn("col5").setEditorComponent(disabledField);
        final TextField readOnlyField = new TextField();
        readOnlyField.setReadOnly(true);
        grid.getColumn("col6").setEditorComponent(readOnlyField);
        grid.setColumnOrder("col0", "col1", "col2", "col3", "col4", "col5",
                "col6");

        getLayout().addComponent(
                new Button("Set Editor Buffered Mode On", event -> {
                    grid.getEditor().cancel();
                    grid.getEditor().setBuffered(true);
                }));
        getLayout().addComponent(
                new Button("Set Editor Buffered Mode Off", event -> {
                    grid.getEditor().cancel();
                    grid.getEditor().setBuffered(false);
                }));

        getLayout().addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11573;
    }

    @Override
    protected String getTestDescription() {
        return "Pressing TAB doesn't shift the focus to non-editable cells when the Grid is in edit mode.";
    }

    public static class TestBean {
        private final int row;

        public TestBean(int row) {
            this.row = row;
        }

        public String getCol0() {
            return "col0_" + row;
        }

        public String getCol1() {
            return "col1_" + row;
        }

        public String getCol2() {
            return "col2_" + row;
        }

        public String getCol3() {
            return "col3_" + row;
        }

        public String getCol4() {
            return "col4_" + row;
        }

        public String getCol5() {
            return "col5_" + row;
        }

        public String getCol6() {
            return "col6_" + row;
        }

        public void setCol0(String value) {
        }

        public void setCol1(String value) {
        }

        public void setCol2(String value) {
        }

        public void setCol3(String value) {
        }

        public void setCol4(String value) {
        }

        public void setCol5(String value) {
        }

        public void setCol6(String value) {
        }
    }
}
