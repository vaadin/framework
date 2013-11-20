/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.tests.components.table;

import com.vaadin.annotations.Push;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

@Push
public class TableRemovedQuicklySendsInvalidRpcCalls extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Button("Blink a table", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                blinkTable();
            }
        }));
    }

    private void blinkTable() {
        final Table table = new Table();
        table.setPageLength(5);
        table.addContainerProperty(new Object(), String.class, null);

        for (int i = 0; i < 50; i++) {
            table.addItem(new Object[] { "Row" }, new Object());
        }

        table.addItemClickListener(new ItemClickListener() {
            private int i;

            @Override
            public void itemClick(ItemClickEvent event) {
                /*
                 * Ignore implementation. This is only an easy way to make the
                 * client-side update table's variables (by furiously clicking
                 * on the table row.
                 * 
                 * This way, we get variable changes queued. The push call will
                 * then remove the Table, while the variable changes being still
                 * in the queue, leading to the issue as described in the
                 * ticket.
                 */
                System.out.println("clicky " + (++i));
            }
        });

        System.out.println("adding component");
        addComponent(table);

        new Thread() {
            @Override
            public void run() {
                getSession().lock();
                try {
                    Thread.sleep(500);
                    access(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("removing component");
                            removeComponent(table);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    getSession().unlock();
                }
            };
        }.start();
    }

    @Override
    protected String getTestDescription() {
        return "Adding and subsequently quickly removing a table "
                + "should not leave any pending RPC calls waiting "
                + "in a Timer. Issue can be reproduced by "
                + "1) pressing the button 2) clicking furiously "
                + "on a row in the table.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12337;
    }
}
