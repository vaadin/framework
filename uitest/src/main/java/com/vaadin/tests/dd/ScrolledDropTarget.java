/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.dd;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.v7.ui.AbstractSelect.VerticalLocationIs;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.TableDragMode;

public class ScrolledDropTarget extends TestBase {
    private final Log log = new Log(5);

    @Override
    protected void setup() {

        Table table = new Table();
        table.addContainerProperty("A", String.class, "");
        for (int i = 0; i < 100; i++) {
            table.addItem(new Object[] { Integer.toString(i) },
                    Integer.valueOf(i));
        }

        table.setDragMode(TableDragMode.ROW);
        table.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return VerticalLocationIs.MIDDLE;
            }

            @Override
            public void drop(DragAndDropEvent event) {
                AbstractSelectTargetDetails targetDetails = (AbstractSelectTargetDetails) event
                        .getTargetDetails();
                VerticalDropLocation dropLocation = targetDetails
                        .getDropLocation();
                log.log("Drop at " + dropLocation + " relative to "
                        + targetDetails.getItemIdOver());
            }
        });

        addComponent(table);
        addComponent(new Button("Scroll body", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getMainWindow().executeJavaScript(
                        "document.body.style.overflow = 'auto';"
                                + "document.body.style.height = '200%';"
                                + "window.scrollTo(0,18)");
            }
        }));
        addComponent(log);
    }

    @Override
    protected String getDescription() {
        return "Vertical location for drags should work even when the browser window is scrolled";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6021);
    }

}
