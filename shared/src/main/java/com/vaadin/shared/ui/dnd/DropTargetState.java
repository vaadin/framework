/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.shared.ui.dnd;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.dnd.criteria.Criterion;

/**
 * State class containing parameters for DropTargetExtension.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class DropTargetState extends SharedState {
    /**
     * {@code DataTransfer.dropEffect} parameter for the drag event.
     */
    public DropEffect dropEffect;

    /**
     * Criteria script to allow drop event on the element.
     */
    public String criteriaScript;

    /**
     * List of criteria to compare against the payload.
     */
    public List<Criterion> criteria = new ArrayList<>();

    /**
     * Declares whether any or all of the given criteria should match the
     * payload.
     */
    public Criterion.Match criteriaMatch = Criterion.Match.ANY;
}
