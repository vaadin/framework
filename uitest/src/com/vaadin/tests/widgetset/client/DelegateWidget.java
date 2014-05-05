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

package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.HTML;

public class DelegateWidget extends HTML {
    private String value1;
    private int value2;
    private Boolean value3;
    private double value4;

    public void setValue1(String value1) {
        this.value1 = value1;
        updateText();
    }

    public void setValue2(int value2) {
        this.value2 = value2;
        updateText();
    }

    public void setValue3(Boolean value3) {
        this.value3 = value3;
        updateText();
    }

    public void setValue4(double value4) {
        this.value4 = value4;
        updateText();
    }

    private void updateText() {
        setHTML(value1 + "<br />" + value2 + "<br />" + value3 + "<br />"
                + value4 + "<br />");
    }
}
