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
package com.vaadin.tests.components.gridlayout;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;

public class GridLayoutExtraSpacing extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getUI().getPage()
                .getStyles()
                .add(".v-gridlayout {background: red;} .v-csslayout {background: white;}");

        final GridLayout gl = new GridLayout(4, 4);

        final CheckBox cb = new CheckBox("spacing");
        cb.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gl.setSpacing(cb.getValue());
            }
        });
        cb.setValue(true);
        addComponent(cb);

        final CheckBox cb2 = new CheckBox("hide empty rows/columns");
        cb2.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gl.setHideEmptyRowsAndColumns(cb2.getValue());
            }
        });
        addComponent(cb2);
        gl.setWidth("1000px");
        gl.setHeight("500px");

        CssLayout ta = new CssLayout();
        ta.setSizeFull();
        // Only on last row
        gl.addComponent(ta, 0, 3, 3, 3);

        gl.setRowExpandRatio(3, 1);
        addComponent(gl);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
