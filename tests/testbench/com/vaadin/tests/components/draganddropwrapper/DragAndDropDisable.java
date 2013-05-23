/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class DragAndDropDisable extends TestBase {

    @Override
    protected void setup() {

        final Panel p = new Panel("Drag here");
        p.setWidth("200px");
        p.setHeight("200px");

        final DragAndDropWrapper dnd = new DragAndDropWrapper(p);
        addComponent(dnd);

        final CheckBox enabled = new CheckBox("Enabed", true);
        addComponent(enabled);
        enabled.setImmediate(true);
        enabled.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                dnd.setEnabled(enabled.booleanValue());
            }
        });

        dnd.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                final WrapperTransferable tr = (WrapperTransferable) event
                        .getTransferable();

                final Html5File[] files = tr.getFiles();
                if (files != null) {
                    for (final Html5File html5File : files) {
                        p.addComponent(new Label(html5File.getFileName()));
                    }

                } else {
                    p.addComponent(new Label("No files"));
                }
            }
        });

    }

    @Override
    protected String getDescription() {
        return "DragAndDropWrapper must be disableable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11801;
    }

}
