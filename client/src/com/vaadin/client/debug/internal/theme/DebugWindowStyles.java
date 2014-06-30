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
package com.vaadin.client.debug.internal.theme;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.DataResource.DoNotEmbed;

public interface DebugWindowStyles extends ClientBundle {

    @Source({ "debugwindow.css" })
    @NotStrict
    public CssResource css();

    // Can't embed because IE8 doesn't support datauri for fonts (images only)
    @Source("font.eot")
    @DoNotEmbed
    DataResource iconFontEot();

    // Can't embed because GWT compiler doesn't know the mimetype for these
    // (ends up as content/unknown)
    @Source("font.ttf")
    @DoNotEmbed
    DataResource iconFontTtf();

    @Source("font.woff")
    @DoNotEmbed
    DataResource iconFontWoff();

    @Source("font.svg")
    @DoNotEmbed
    DataResource iconFontSvg();

}