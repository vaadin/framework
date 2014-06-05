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
package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class SplitPanels extends VerticalLayout implements View {
    public SplitPanels() {
        setMargin(true);

        Label h1 = new Label("Split Panels");
        h1.addStyleName("h1");
        addComponent(h1);

        addComponent(new Label(
                "Outlines are just to show the areas of the SplitPanels. They are not part of the actual component style."));

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        row.setMargin(new MarginInfo(true, false, false, false));
        addComponent(row);

        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setCaption("Default style");
        sp.setWidth("400px");
        sp.setHeight(null);
        sp.setFirstComponent(getContent());
        sp.setSecondComponent(getContent());
        row.addComponent(sp);

        VerticalSplitPanel sp2 = new VerticalSplitPanel();
        sp2.setCaption("Default style");
        sp2.setWidth("300px");
        sp2.setHeight("200px");
        sp2.setFirstComponent(getContent());
        sp2.setSecondComponent(getContent());
        row.addComponent(sp2);

        sp = new HorizontalSplitPanel();
        sp.setCaption("Large style");
        sp.setWidth("300px");
        sp.setHeight("200px");
        sp.addStyleName("large");
        sp.setFirstComponent(getContent());
        sp.setSecondComponent(getContent());
        row.addComponent(sp);

        sp2 = new VerticalSplitPanel();
        sp2.setCaption("Large style");
        sp2.setWidth("300px");
        sp2.setHeight("200px");
        sp2.addStyleName("large");
        sp2.setFirstComponent(getContent());
        sp2.setSecondComponent(getContent());
        row.addComponent(sp2);
    }

    VerticalLayout getContent() {
        return new VerticalLayout() {
            {
                setMargin(true);
                addComponent(new Label(
                        "Fictum,  deserunt mollit anim laborum astutumque!"));
            }
        };
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
