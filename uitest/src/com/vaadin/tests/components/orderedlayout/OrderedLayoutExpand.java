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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class OrderedLayoutExpand extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        ThreeColumnLayout layout = new ThreeColumnLayout(
                createLabelWithUndefinedSize("first component"),
                createLabelWithUndefinedSize("second component"),
                createLabelWithUndefinedSize("third component"));

        addComponent(layout);
        setWidth("600px");

        if (UI.getCurrent().getPage().getWebBrowser().isChrome()) {
            getPage().getStyles().add("body { zoom: 1.10; }");
        }
    }

    @Override
    protected String getTestDescription() {
        StringBuilder sb = new StringBuilder(
                "You should not see 'Aborting layout after 100 passes.' error with debug mode and ");
        if (UI.getCurrent().getPage().getWebBrowser().isChrome()) {
            sb.append(" Zoom is ");
            sb.append("1.10");
        } else {
            sb.append(" zoom level 95%.");
        }
        return sb.toString();
    }

    @Override
    protected Integer getTicketNumber() {
        return 13359;
    }

    private Label createLabelWithUndefinedSize(String caption) {
        Label label = new Label(caption);
        label.setSizeUndefined();
        return label;
    }

    private static class ThreeColumnLayout extends HorizontalLayout {
        public ThreeColumnLayout(Component leftComponent,
                Component centerComponent, Component rightComponent) {
            setWidth("100%");
            setMargin(true);
            Component left = createLeftHolder(leftComponent);
            this.addComponent(left);
            setComponentAlignment(left, Alignment.MIDDLE_LEFT);
            setExpandRatio(left, 1f);
            Component center = createCenterHolder(centerComponent);
            this.addComponent(center);
            setComponentAlignment(center, Alignment.MIDDLE_CENTER);
            setExpandRatio(center, 0f);
            Component right = createRightHolder(rightComponent);
            this.addComponent(right);
            setComponentAlignment(right, Alignment.MIDDLE_RIGHT);
            setExpandRatio(right, 1f);
        }

        private Component createLeftHolder(Component c) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(c);
            hl.setWidth(100, Unit.PERCENTAGE);
            return hl;
        }

        private Component createCenterHolder(Component c) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(c);
            hl.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
            return hl;
        }

        private Component createRightHolder(Component c) {
            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(c);
            hl.setWidth(100, Unit.PERCENTAGE);
            return hl;
        }
    }
}
