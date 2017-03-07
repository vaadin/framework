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
package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.IdTestLabel;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
@Theme("tests-tickets")
public class IdOverrideTest extends AbstractReindeerTestUI {

    @Override
    protected String getTestDescription() {
        return "Id shouldn't get overridden unless specifically re-set.<br>"
                + "First two are custom labels with a default id, third is an ordinary label for comparison.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10179;
    }

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        final IdTestLabel idTestLabel = new IdTestLabel("default id");
        idTestLabel.setSizeUndefined();
        addComponent(idTestLabel);

        final IdTestLabel idTestLabelWithId = new IdTestLabel("set id");
        idTestLabelWithId.setSizeUndefined();
        idTestLabelWithId.setId("set10179");
        addComponent(idTestLabelWithId);

        final Label label = new Label("no id");
        label.setSizeUndefined();
        addComponent(label);

        Button button = new Button();
        button.setCaption("Toggle");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (idTestLabelWithId.getId() == null) {
                    idTestLabelWithId.setId("set10179");
                    idTestLabelWithId.setValue("set id");
                    idTestLabel.setValue("default id");
                    label.setValue("no id");
                } else {
                    idTestLabelWithId.setId(null);
                    idTestLabelWithId.setValue("removed id");
                    idTestLabel.setValue("still default id");
                    label.setValue("still no id");
                }
            }
        });
        button.setId("toggle");
        addComponent(button);
    }

}
