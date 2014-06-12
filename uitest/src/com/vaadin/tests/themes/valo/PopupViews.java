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
package com.vaadin.tests.themes.valo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.VerticalLayout;

public class PopupViews extends VerticalLayout implements View {
    public PopupViews() {
        setMargin(true);

        Label h1 = new Label("Popup Views");
        h1.addStyleName("h1");
        addComponent(h1);

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        addComponent(row);

        PopupView pv = new PopupView(new Content() {
            @Override
            public Component getPopupComponent() {
                return new VerticalLayout() {
                    {
                        setMargin(true);
                        setWidth("300px");
                        addComponent(new Label(
                                "Fictum,  deserunt mollit anim laborum astutumque! Magna pars studiorum, prodita quaerimus."));
                    }
                };
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "Click to view";
            }
        });
        row.addComponent(pv);
        pv.setHideOnMouseOut(true);
        pv.setCaption("Hide on mouse-out");

        pv = new PopupView(new Content() {
            int count = 0;

            @Override
            public Component getPopupComponent() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                return new VerticalLayout() {
                    {
                        setMargin(true);
                        addComponent(new Label(
                                "<h3>Thanks for waiting!</h3><p>You've opened this popup <b>"
                                        + ++count + " time"
                                        + (count > 1 ? "s" : " only")
                                        + "</b>.</p>", ContentMode.HTML));
                    }
                };
            }

            @Override
            public String getMinimizedValueAsHTML() {
                return "Show slow loading content";
            }
        });
        row.addComponent(pv);
        pv.setHideOnMouseOut(false);
        pv.setCaption("Hide on click-outside");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
