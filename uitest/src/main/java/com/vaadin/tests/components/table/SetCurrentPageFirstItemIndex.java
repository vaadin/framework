package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.v7.ui.Table.ColumnHeaderMode;

public class SetCurrentPageFirstItemIndex extends AbstractReindeerTestUI {

    private int index = 5;

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        setContent(vl);

        final Table imageTable = new Table();
        vl.addComponent(imageTable);

        imageTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
        imageTable.setPageLength(1);
        imageTable.addGeneratedColumn("image", new ImageGenerator());
        imageTable.setWidth(500, Unit.PIXELS);

        for (int i = 1; i <= 25; i++) {
            imageTable.addItem(new Integer(i));
        }

        imageTable.setCurrentPageFirstItemIndex(index);

        vl.addComponent(new Button("Click", event -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (index != 5) {
                index = 5;
                imageTable.setCurrentPageFirstItemIndex(index);
            } else {
                index = 20;
                imageTable.setCurrentPageFirstItemIndex(index);
            }
        }));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Field lastRequestedFirstvisible should be updated out of timer.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 10666;
    }

    public class ImageGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId,
                Object columnId) {
            return "" + itemId;
        }
    }

}
