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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class HtmlInTabCaption extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);
        TabSheet ts = new TabSheet();
        ts.setCaption("TabSheet - no <u>html</u> in tab captions");
        ts.setCaptionAsHtml(true);
        ts.addTab(new Label(), "<font color='red'>red</font>");
        ts.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(ts);

        ts = new TabSheet();
        ts.setCaption("TabSheet - <b>html</b> in tab captions");
        ts.setCaptionAsHtml(false);
        ts.setTabCaptionsAsHtml(true);
        ts.addTab(new Label(), "<font color='red'>red</font>");
        ts.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(ts);

        Accordion acc = new Accordion();
        acc.setCaption("Accordion - no <u>html</u> in tab captions");
        acc.setCaptionAsHtml(true);
        acc.addTab(new Label(), "<font color='red'>red</font>");
        acc.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(acc);

        acc = new Accordion();
        acc.setCaption("Accordion - <b>html</b> in tab captions");
        acc.setCaptionAsHtml(false);
        acc.setTabCaptionsAsHtml(true);
        acc.addTab(new Label(), "<font color='red'>red</font>");
        acc.addTab(new Label(), "<font color='blue'>blue</font>");
        addComponent(acc);

    }

    @Override
    protected Integer getTicketNumber() {
        return 14609;
    }

}
