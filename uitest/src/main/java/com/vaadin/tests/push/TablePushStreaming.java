/**
 *
 */
package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
@Push(transport = Transport.STREAMING)
public class TablePushStreaming extends AbstractReindeerTestUI {

    private int iteration = 1;

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table t = new Table("The table");
        t.setContainerDataSource(generateContainer(10, 10, iteration++));
        t.setSizeFull();
        Runnable r = () -> {
            for (int i = 0; i < 99; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                access(() ->
                        t.setContainerDataSource(
                                generateContainer(t.getVisibleColumns().length,
                                t.size(), iteration++)));
            }
        };
        Thread tr = new Thread(r);
        tr.start();

        setContent(t);
    }

    /**
     * @param iter
     * @since
     * @return
     */
    private Container generateContainer(int rows, int cols, int iter) {
        IndexedContainer ic = new IndexedContainer();
        for (int col = 1; col <= cols; col++) {
            ic.addContainerProperty("Property" + col, String.class, "");
        }

        for (int row = 0; row < rows; row++) {
            Item item = ic.addItem("row" + row);
            for (int col = 1; col <= cols; col++) {
                item.getItemProperty("Property" + col).setValue(
                        "Row " + row + " col " + col + "(" + iter + ")");
            }
        }
        return ic;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test that pushes Table data at a high pace to detect possible problems in the streaming protocol";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
