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
package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class SetCurrentPageFirstItemIndex extends AbstractTestUI {

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

        vl.addComponent(new Button("Click", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
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
        public Object generateCell(Table source, Object itemId, Object columnId) {
            return "" + itemId;
        }
    }

}
