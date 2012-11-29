/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.extensions;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.popupview.ReopenPopupView;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;

public class BrowserPopupExtensionTest extends AbstractTestUI {

    public static class ShowParamsUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
            setContent(new Label("Query: "
                    + getPage().getLocation().getRawQuery() + ", Fragment: "
                    + getPage().getLocation().getFragment()));
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
        components.add(Button.class);
        components.add(NativeButton.class);
        components.add(Link.class);
        components.add(CssLayout.class);
        components.add(Label.class);
        addComponents(components, "/statictestfiles/static.html");

        Button uiClassButton = new Button("Open UI class");
        new BrowserWindowOpener(ReopenPopupView.class).extend(uiClassButton);
        addComponent(uiClassButton);

        Button uiWithPath = new Button("Open UI class with path");
        new BrowserWindowOpener(ReopenPopupView.class, "foobar")
                .extend(uiWithPath);
        addComponent(uiWithPath);

        Button withPopupFeaturesButton = new Button(
                "Open with features and fragment");
        BrowserWindowOpener featuresPopup = new BrowserWindowOpener(
                "/statictestfiles/static.html#originalfragment");
        featuresPopup.setFeatures("width=400,height=400");
        featuresPopup.extend(withPopupFeaturesButton);
        featuresPopup.setUriFragment("myFragment");
        addComponent(withPopupFeaturesButton);

        Button withParametersButton = new Button("Open UI with parameters");
        BrowserWindowOpener parametersOpener = new BrowserWindowOpener(
                ShowParamsUI.class);
        parametersOpener.setUriFragment("myfragment");
        parametersOpener.setParameter("my&param", "my=param#value");
        parametersOpener.extend(withParametersButton);
        addComponent(withParametersButton);
    }

    public void addComponents(List<Class<? extends Component>> components,
            String URL) {
        final HorizontalLayout hl = new HorizontalLayout();
        for (Class<? extends Component> cls : components) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                c.setId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(URL);
                c.setWidth("100px");
                c.setHeight("100px");
                hl.addComponent(c);

                new BrowserWindowOpener(URL).extend(c);

                if (c instanceof Button) {
                    ((Button) c).addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Could not instatiate " + cls.getName());
            }
        }
        addComponent(hl);
    }

    @Override
    protected String getTestDescription() {
        return "Test for " + BrowserWindowOpener.class.getSimpleName()
                + " features";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9513);
    }

}
