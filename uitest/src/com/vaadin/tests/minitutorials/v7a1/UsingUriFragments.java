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

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20URI%20fragments
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class UsingUriFragments extends UI {

    private VerticalLayout layout;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Label label = new Label("Hello, your fragment is "
                + getPage().getUriFragment());
        layout.addComponent(label);

        // React to fragment changes
        getPage().addListener(new UriFragmentChangedListener() {
            @Override
            public void uriFragmentChanged(UriFragmentChangedEvent source) {
                handleFragment(source.getUriFragment());
            }
        });

        // Handle the fragment received in the initial request
        handleFragment(getPage().getUriFragment());

        layout.addComponent(new Button("Show and set fragment",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        handleFragment(getPage().getUriFragment());
                        getPage().setUriFragment("customFragment");
                    }
                }));
    }

    private void handleFragment(String uriFragment) {
        layout.addComponent(new Label("Got new fragment: " + uriFragment));
    }

}
