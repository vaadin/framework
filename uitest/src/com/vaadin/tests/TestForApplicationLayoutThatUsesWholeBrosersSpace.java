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

package com.vaadin.tests;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class TestForApplicationLayoutThatUsesWholeBrosersSpace extends
        LegacyApplication {

    LegacyWindow main = new LegacyWindow("Windowing test");

    VerticalLayout rootLayout;

    VerticalSplitPanel firstLevelSplit;

    @Override
    public void init() {
        setMainWindow(main);

        rootLayout = new VerticalLayout();
        main.setContent(rootLayout);

        rootLayout.addComponent(new Label("header"));

        firstLevelSplit = new VerticalSplitPanel();

        final HorizontalSplitPanel secondSplitPanel = new HorizontalSplitPanel();
        secondSplitPanel.setFirstComponent(new Label("left"));

        final VerticalLayout topRight = new VerticalLayout();
        topRight.addComponent(new Label("topright header"));

        final Table t = TestForTablesInitialColumnWidthLogicRendering
                .getTestTable(4, 100);
        t.setSizeFull();
        topRight.addComponent(t);
        topRight.setExpandRatio(t, 1);

        topRight.addComponent(new Label("topright footer"));

        secondSplitPanel.setSecondComponent(topRight);

        final VerticalLayout el = new VerticalLayout();
        el.addComponent(new Label("B��"));

        firstLevelSplit.setFirstComponent(secondSplitPanel);
        firstLevelSplit.setSecondComponent(el);

        rootLayout.addComponent(firstLevelSplit);
        rootLayout.setExpandRatio(firstLevelSplit, 1);

        rootLayout.addComponent(new Label("footer"));

    }

}
