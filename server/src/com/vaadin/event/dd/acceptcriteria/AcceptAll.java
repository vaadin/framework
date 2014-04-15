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
/**
 * 
 */
package com.vaadin.event.dd.acceptcriteria;

import com.vaadin.event.dd.DragAndDropEvent;

/**
 * Criterion that accepts all drops anywhere on the component.
 * <p>
 * Note! Class is singleton, use {@link #get()} method to get the instance.
 * 
 * 
 * @since 6.3
 * 
 */
public final class AcceptAll extends ClientSideCriterion {

    private static final long serialVersionUID = 7406683402153141461L;
    private static AcceptCriterion singleton = new AcceptAll();

    private AcceptAll() {
    }

    public static AcceptCriterion get() {
        return singleton;
    }

    @Override
    public boolean accept(DragAndDropEvent dragEvent) {
        return true;
    }
}
