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
package com.vaadin.tests.resources;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class NonExistingFileResource extends TestBase {

    @Override
    protected void setup() {
        Button existing = createButton("WEB-INF/web.xml");
        Button nonExisting = createButton("WEB-INF/web2.xml");
        addComponent(existing);
        addComponent(nonExisting);

    }

    private Button createButton(final String filename) {
        Button b = new Button("Download " + filename);
        b.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                FileResource res = new FileResource(
                        new File(VaadinService.getCurrent().getBaseDirectory()
                                + "/" + filename));
                getMainWindow().open(res);

            }
        });
        return b;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
