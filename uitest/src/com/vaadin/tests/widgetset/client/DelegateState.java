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

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

public class DelegateState extends AbstractComponentState {
    @DelegateToWidget
    public String value1;

    @DelegateToWidget("setValue2")
    public int renamedValue2;

    private Boolean value3;

    private double renamedValue4;

    @DelegateToWidget
    public void setValue3(Boolean value3) {
        this.value3 = value3;
    }

    public Boolean getValue3() {
        return value3;
    }

    @DelegateToWidget("setValue4")
    public void setRenamedValue4(double renamedValue4) {
        this.renamedValue4 = renamedValue4;
    }

    public double getRenamedValue4() {
        return renamedValue4;
    }
}
