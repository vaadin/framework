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

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

public class Tabsheets extends VerticalLayout implements View {
    public Tabsheets() {
        setMargin(true);

        Label h1 = new Label("Tabs");
        h1.addStyleName("h1");
        addComponent(h1);

        final VerticalLayout wrap = new VerticalLayout();
        wrap.setSpacing(true);
        addComponent(wrap);

        final CheckBox closable = new CheckBox("Closable");
        closable.setImmediate(true);
        wrap.addComponent(closable);

        final CheckBox overflow = new CheckBox("Overflow");
        overflow.setImmediate(true);
        wrap.addComponent(overflow);

        final CheckBox icon = new CheckBox("Icons");
        icon.setImmediate(true);
        wrap.addComponent(icon);

        ValueChangeListener update = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                wrap.removeAllComponents();

                wrap.addComponents(closable, overflow, icon);

                wrap.addComponent(getTabSheet("Normal", null,
                        closable.getValue(), overflow.getValue(),
                        icon.getValue()));
                wrap.addComponent(getTabSheet("Centered tabs", "centered-tabs",
                        closable.getValue(), overflow.getValue(),
                        icon.getValue()));
                wrap.addComponent(getTabSheet("Equal-width tabs",
                        "equal-width-tabs", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
                wrap.addComponent(getTabSheet("Icons on top + padded tabbar",
                        "icons-on-top padded-tabbar", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
                wrap.addComponent(getTabSheet("Only selected tab is closable",
                        "selected-closable", closable.getValue(),
                        overflow.getValue(), icon.getValue()));
            }
        };
        closable.addValueChangeListener(update);
        overflow.addValueChangeListener(update);
        icon.addValueChangeListener(update);

        // Generate initial view
        icon.setValue(true);
    }

    TabSheet getTabSheet(String caption, String style, boolean closable,
            boolean scrolling, boolean icon) {
        TabSheet ts = new TabSheet();
        ts.addStyleName(style);
        ts.setCaption(caption);

        for (int i = 0; i < (scrolling ? 10 : 3); i++) {
            String tabcaption = ValoThemeTest.nextString(true);
            if (i == 0 && icon) {
                tabcaption = null;
            }
            Tab t = ts.addTab(new Label(" "), tabcaption);
            t.setClosable(closable);

            if (icon) {
                t.setIcon(TestIcon.get(i == 2));
            }
        }
        return ts;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
