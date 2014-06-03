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

package com.vaadin.client.debug.internal;

public enum Icon {

    SEARCH("&#xf002;"), //
    OK("&#xf00c;"), //
    REMOVE("&#xf00d;"), //
    CLOSE("&#xf011;"), //
    CLEAR("&#xf014;"), //
    RESET_TIMER("&#xf017;"), //
    MINIMIZE("&#xf066;"), //
    WARNING("&#xf071;"), //
    INFO("&#xf05a;"), //
    ERROR("&#xf06a;"), //
    HIGHLIGHT("&#xf05b;"), //
    LOG("&#xf0c9;"), //
    OPTIMIZE("&#xf0d0;"), //
    HIERARCHY("&#xf0e8;"), //
    // TODO create more appropriate icon
    SELECTOR("&#x2263;"), //
    MENU("&#xf013;"), //
    NETWORK("&#xf0ec;"), //
    ANALYZE("&#xf0f0;"), //
    SCROLL_LOCK("&#xf023;"), //
    DEVMODE_OFF("&#xf10c;"), //
    DEVMODE_SUPER("&#xf111;"), //
    DEVMODE_ON("&#xf110;"), //
    // BAN_CIRCLE("&#xf05e;"), //
    MAXIMIZE("&#xf065;"), //
    RESET("&#xf021;"), //
    PERSIST("&#xf02e"), //
    TESTBENCH("&#xe600"), //
    ;

    private String id;

    private Icon(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "<i data-icon=\"" + id + "\"></i>";
    }

    public String getId() {
        return id;
    }

}
