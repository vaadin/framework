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
package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.ComboBox;

public class SubWindowOrder extends TestBase {

    private BeanItemContainer<Window> windowlist = new BeanItemContainer<>(
            Window.class);

    @Override
    protected void setup() {
        UI mainWindow = getMainWindow();
        HorizontalLayout controlpanels = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            Window dialog = new Window("Dialog " + i, layout);
            layout.setSizeUndefined();
            windowlist.addBean(dialog);
            layout.addComponent(new Label("this is dialog number " + i));
            layout.addComponent(new ControlPanel());
            mainWindow.addWindow(dialog);
        }
        controlpanels.addComponent(new ControlPanel());
        getLayout().setSizeFull();
        getLayout().addComponent(controlpanels);
        getLayout().setComponentAlignment(controlpanels, Alignment.BOTTOM_LEFT);

    }

    @Override
    protected String getDescription() {
        return "Subwindows should be rendered in the same order as they are added.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3363;
    }

    class ControlPanel extends CssLayout implements ClickListener {
        private Button bf = new Button("Bring to front");
        private Button toggleModality = new Button("Toggle modality");
        private ComboBox winSel = new ComboBox();

        public ControlPanel() {
            winSel.setCaption("Controlled window:");
            winSel.setContainerDataSource(windowlist);
            winSel.setValue(windowlist.firstItemId());
            winSel.setItemCaptionPropertyId("caption");
            addComponent(winSel);
            addComponent(bf);
            addComponent(toggleModality);
            bf.addClickListener(this);
            toggleModality.addClickListener(this);
        }

        @Override
        public void buttonClick(ClickEvent event) {
            if (event.getButton() == bf) {
                getCurWindow().bringToFront();
            } else if (event.getButton() == toggleModality) {
                getCurWindow().setModal(!getCurWindow().isModal());
            }

        }

        private Window getCurWindow() {
            return (Window) winSel.getValue();
        }
    }

}
