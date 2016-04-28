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

package com.vaadin.tests.minitutorials.v7b1;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.ui.AbstractComponent;

public class Addition extends AbstractComponent {
    private int term1;
    private int term2;
    private boolean needsRecalculation = false;

    public void setTerm1(int value1) {
        term1 = value1;
        needsRecalculation = true;

        // Mark the component as dirty to ensure beforeClientResponse will be
        // invoked
        markAsDirty();
    }

    public void setTerm2(int value2) {
        term2 = value2;
        needsRecalculation = true;

        // Mark the component as dirty to ensure beforeClientResponse will be
        // invoked
        markAsDirty();
    }

    private int calculateSum() {
        return term1 + term2;
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        if (needsRecalculation) {
            needsRecalculation = false;
            // This could be an expensive operation that we don't want to do
            // every time setTerm1 or setTerm2 is invoked.
            getState().sum = calculateSum();
        }
    }

    @Override
    protected AddResultState getState() {
        return (AddResultState) super.getState();
    }
}

class AddResultState extends AbstractComponentState {
    public int sum;
}
