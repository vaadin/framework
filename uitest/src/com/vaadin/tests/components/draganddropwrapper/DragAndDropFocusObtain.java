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
package com.vaadin.tests.components.draganddropwrapper;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for text area inside {@link DragAndDropWrapper}: text area should
 * obtain focus on click.
 * 
 * @author Vaadin Ltd
 */
public class DragAndDropFocusObtain extends AbstractTestUIWithLog {

    private FocusListener focusListener = new FocusListener() {

        @Override
        public void focus(FocusEvent event) {
            log("Field '" + event.getComponent().getCaption() + "' focused");

        }
    };
    private ValueChangeListener listener = new ValueChangeListener() {

        @Override
        public void valueChange(ValueChangeEvent event) {
            AbstractField f = (AbstractField) event.getProperty();
            log("Value of " + f.getCaption() + " changed to " + f.getValue());
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        VerticalLayout dndLayout = createLayout();
        VerticalLayout normalLayout = createLayout();
        DragAndDropWrapper wrapper = new DragAndDropWrapper(dndLayout);
        wrapper.setDragStartMode(DragStartMode.COMPONENT);
        wrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                log("Dropped " + event.getTransferable().getSourceComponent()
                        + " on " + event.getTargetDetails().getTarget());

            }
        });
        hl.addComponent(wrapper);
        hl.addComponent(normalLayout);
        addComponent(hl);

    }

    private VerticalLayout createLayout() {
        VerticalLayout dndLayout = new VerticalLayout();

        TextArea area = new TextArea("Text area 1");
        area.setValue("text");
        area.addValueChangeListener(listener);
        area.addFocusListener(focusListener);
        dndLayout.addComponent(area);

        area = new TextArea("Text area 2");
        area.setValue("text");
        area.addValueChangeListener(listener);
        area.addFocusListener(focusListener);
        dndLayout.addComponent(area);

        TextField field = new TextField("Text field 1");
        field.setValue("text");
        field.addValueChangeListener(listener);
        field.addFocusListener(focusListener);
        dndLayout.addComponent(field);

        field = new TextField("Text field 2");
        field.setValue("text");
        field.addValueChangeListener(listener);
        field.addFocusListener(focusListener);
        dndLayout.addComponent(field);

        return dndLayout;
    }

    @Override
    protected String getTestDescription() {
        return "Text fields/areas inside Drag and Drop Wrappers should get focus inside DnD wrapper on click.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12838;
    }
}