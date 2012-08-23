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

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;

public class BootstrapModifyRoot extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getTestDescription() {
        return "There should be two additional divs in the HTML of the bootstrap page for this Root";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9274);
    }

}
