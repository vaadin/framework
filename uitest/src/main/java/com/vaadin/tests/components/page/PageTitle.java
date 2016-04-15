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
package com.vaadin.tests.components.page;

import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;

@Title("bar")
public class PageTitle extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String title = request.getParameter("title");
        if (title != null) {
            getPage().setTitle(title);
        }

    }

    @Override
    protected String getTestDescription() {
        return "Sets the title according to a given ?title parameter. By default the ApplicationServletRunner will set the title to the fully qualified class name";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13430;
    }

}
