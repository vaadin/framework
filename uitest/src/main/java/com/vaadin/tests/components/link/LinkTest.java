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
package com.vaadin.tests.components.link;

import java.util.LinkedHashMap;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Link;

public class LinkTest extends AbstractComponentTest<Link> {

    private Command<Link, Resource> linkTargetCommand = new Command<Link, Resource>() {

        @Override
        public void execute(Link c, Resource value, Object data) {
            c.setResource(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createTargetSelect(CATEGORY_FEATURES);
    }

    private void createTargetSelect(String category) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<String, Resource>();
        options.put("-", null);
        options.put("https://vaadin.com", new ExternalResource(
                "https://vaadin.com"));
        options.put("32x32 theme icon", ICON_32_ATTENTION_PNG_CACHEABLE);
        options.put("linktest-target.html", new ClassResource(
                "linktest-target.html"));

        createSelectAction("Link target", category, options,
                "https://vaadin.com", linkTargetCommand, null);
    }

    @Override
    protected Class<Link> getTestClass() {
        return Link.class;
    }
}
