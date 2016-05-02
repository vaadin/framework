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
package com.vaadin.tests.components.table;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Test for ensuring page doesn't jump up to the Table selection on IE with
 * these steps:
 * 
 * <p>
 * 1. refresh page <br>
 * 2. click within URL bar <br>
 * 3. click a table row to select it <br>
 * 4. click within one of the text fields <br>
 * 5. scroll down <br>
 * 6. click the button
 * </p>
 * The problem is that IE for some reason does not fire a blur event for the
 * table at step 4, leading to table thinking it is focused when it is updated
 * in step 6.
 * 
 * @author Vaadin Ltd
 */
@Theme(ValoTheme.THEME_NAME)
public class TableJumpUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        BeanItemContainer<TestObj> container = new BeanItemContainer<TestObj>(
                TestObj.class);
        for (int i = 0; i < 2; i++) {
            container.addBean(new TestObj(i));
        }

        final Table table = new Table();
        table.setPageLength(2);
        table.setContainerDataSource(container);
        table.setSelectable(true);
        addComponent(table);

        // After the table we have a lot of textfields so that we have to scroll
        // down to the button
        for (int i = 0; i < 40; i++) {
            TextField tf = new TextField();
            tf.setValue(String.valueOf(i));
            final int j = i;
            tf.addFocusListener(new FocusListener() {
                @Override
                public void focus(FocusEvent event) {
                    log("Tf " + j + " focus");
                }
            });
            tf.addBlurListener(new BlurListener() {
                @Override
                public void blur(BlurEvent event) {
                    log("Tf " + j + " Blur");
                }
            });
            addComponent(tf);
        }

        addButton("refresh row cache", new ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                table.refreshRowCache();
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Page shouldn't scroll up to Table selection when the button is clicked.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19676;
    }

    public static class TestObj {
        int i;
        String text;

        public TestObj(final int i) {
            this.i = i;
            text = "Object " + i;
        }

        public int getI() {
            return i;
        }

        public void setI(final int i) {
            this.i = i;
        }

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }

    }

}