package com.vaadin.tests.components.table;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;

public class HeaderRightClickAfterDrag extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Table table = new Table();
        table.setContainerDataSource(new BeanItemContainer<>(TestBean.class));
        for (int i = 0; i < 10; i++) {
            table.addItem(new TestBean(i));
        }

        table.setPageLength(10);
        table.setColumnReorderingAllowed(true);
        table.addHeaderClickListener(event -> {
            if (MouseEventDetails.MouseButton.RIGHT.equals(event.getButton())) {
                Window window = new Window("Right-clicked:", new Label(
                        "<center>" + event.getPropertyId().toString()
                                .toUpperCase(Locale.ROOT) + "</center>",
                        ContentMode.HTML));
                window.setPositionX(event.getClientX());
                window.setPositionY(event.getClientY());
                window.setResizable(false);
                addWindow(window);
            }
        });

        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "1) Right click a column header and see a popup<br>"
                + "2) Reorder (or at least start dragging) that column<br>"
                + "3) Right click that same column header, and you should get a popup again.<br>"
                + "Before fix: no popup, unless you first left-click the header.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15167;
    }

    public class TestBean {

        private String foo, bar, baz, fiz;

        public TestBean(int i) {
            foo = "Foo " + i;
            bar = "Bar " + i;
            baz = "Baz " + i;
            fiz = "Fix " + i;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        public String getBaz() {
            return baz;
        }

        public String getFiz() {
            return fiz;
        }
    }
}
