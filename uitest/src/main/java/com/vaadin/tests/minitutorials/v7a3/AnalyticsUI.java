/* 
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;

public class AnalyticsUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        final Analytics analytics = new Analytics(this, "UA-33036133-12");

        setContent(new Button("Track pageview", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                analytics.trackPageview("/fake/url");
            }
        }));
    }

}
