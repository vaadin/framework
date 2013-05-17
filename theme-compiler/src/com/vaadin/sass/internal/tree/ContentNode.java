/*
 * Copyright 2000-2013 Vaadin Ltd.
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
 * ContentNode represents a {@literal @}content in a SCSS tree. 
 */
package com.vaadin.sass.internal.tree;

public class ContentNode extends Node {

    @Override
    public void traverse() {
        /*
         * ContentNode is basically just a placeholder for some content which
         * will be included. So for traverse of this node, it does nothing. it
         * will be replaced when traversing MixinDefNode which contains it.
         */
    }

}
