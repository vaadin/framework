/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * 
 */
package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@Push(transport = Transport.STREAMING)
public class TablePushStreaming extends AbstractTestUI {

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
        Runnable r = new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 99; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    access(new Runnable() {

                        @Override
                        public void run() {
                            t.setContainerDataSource(generateContainer(
                                    t.getVisibleColumns().length, t.size(),
                                    iteration++));
                        }

                    });
                }

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
