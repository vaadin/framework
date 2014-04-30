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