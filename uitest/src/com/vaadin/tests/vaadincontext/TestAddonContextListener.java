/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.tests.vaadincontext;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.vaadin.server.AddonContextEvent;
import com.vaadin.server.AddonContextListener;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.BootstrapResponse;
import com.vaadin.ui.UI;

public class TestAddonContextListener implements AddonContextListener {
    @Override
    public void contextCreated(AddonContextEvent event) {
        event.getAddonContext().addBootstrapListener(new BootstrapListener() {
            @Override
            public void modifyBootstrapFragment(
                    BootstrapFragmentResponse response) {
                if (shouldModify(response)) {
                    Element heading = new Element(Tag.valueOf("div"), "")
                            .text("Added by modifyBootstrapFragment");
                    response.getFragmentNodes().add(0, heading);
                }
            }

            private boolean shouldModify(BootstrapResponse response) {
                Class<? extends UI> uiClass = response.getUiClass();
                boolean shouldModify = uiClass == BootstrapModifyUI.class;
                return shouldModify;
            }

            @Override
            public void modifyBootstrapPage(BootstrapPageResponse response) {
                if (shouldModify(response)) {
                    response.getDocument().body().child(0)
                            .before("<div>Added by modifyBootstrapPage</div>");
                }
            }
        });
    }

    @Override
    public void contextDestroyed(AddonContextEvent event) {
        // Nothing to do
    }

}
