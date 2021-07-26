/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.WidgetUtil;

/**
 * Widget class for the Flash component.
 *
 * @author Vaadin Ltd
 *
 * @deprecated No modern browsers support Flash content anymore.
 */
@Deprecated
public class VFlash extends HTML {

    /** Default classname for this widget. */
    public static final String CLASSNAME = "v-flash";

    /** @see #setSource(String) */
    protected String source;
    /** @see #setAlternateText(String) */
    protected String altText;
    /** @see #setClassId(String) */
    protected String classId;
    /** @see #setCodebase(String) */
    protected String codebase;
    /** @see #setCodetype(String) */
    protected String codetype;
    /** @see #setStandby(String) */
    protected String standby;
    /** @see #setArchive(String) */
    protected String archive;
    /** @see #setEmbedParams(Map) */
    protected Map<String, String> embedParams = new HashMap<>();
    /** Determines whether {@link #rebuildIfNeeded()} does anything. */
    protected boolean needsRebuild = false;
    /** @see #setWidth(String) */
    protected String width;
    /** @see #setHeight(String) */
    protected String height;

    private int slotOffsetHeight = -1;
    private int slotOffsetWidth = -1;

    /**
     * Default constructor.
     */
    public VFlash() {
        setStyleName(CLASSNAME);
    }

    /**
     * Set the resource representing the Flash content that should be displayed.
     *
     * @param source
     *            the resource URL
     */
    public void setSource(String source) {
        if (this.source != source) {
            this.source = source;
            needsRebuild = true;
        }
    }

    /**
     * Sets this component's alternate text that can be presented instead of the
     * component's normal content for accessibility purposes.
     *
     * @param altText
     *            a short, human-readable description of this component's
     *            content
     */
    public void setAlternateText(String altText) {
        if (this.altText != altText) {
            this.altText = altText;
            needsRebuild = true;
        }
    }

    /**
     * Set the class id that is required for ActiveX to recognize the flash.
     * This is a predefined value which ActiveX recognizes and must be the given
     * value.
     *
     * @param classId
     *            the classId
     */
    public void setClassId(String classId) {
        if (this.classId != classId) {
            this.classId = classId;
            needsRebuild = true;
        }
    }

    /**
     * This attribute specifies the base path used to resolve relative URIs
     * specified by the classid, data, and archive attributes. The default value
     * is the base URI of the current document.
     *
     * @param codebase
     *            The base path
     *
     * @see #setClassId(String)
     * @see #setArchive(String)
     */
    public void setCodebase(String codebase) {
        if (this.codebase != codebase) {
            this.codebase = codebase;
            needsRebuild = true;
        }
    }

    /**
     * This attribute specifies the content type of data expected when
     * downloading the object specified by classid. This attribute is optional
     * but recommended when classid is specified since it allows the user agent
     * to avoid loading information for unsupported content types. The default
     * value is the value of the type attribute.
     *
     * @param codetype
     *            the codetype to set.
     */
    public void setCodetype(String codetype) {
        if (this.codetype != codetype) {
            this.codetype = codetype;
            needsRebuild = true;
        }
    }

    /**
     * Sets standby.
     *
     * @param standby
     *            the standby text
     */
    public void setStandby(String standby) {
        if (this.standby != standby) {
            this.standby = standby;
            needsRebuild = true;
        }
    }

    /**
     * This attribute may be used to specify a space-separated list of URIs for
     * archives containing resources relevant to the object, which may include
     * the resources specified by the classid and data attributes. Preloading
     * archives will generally result in reduced load times for objects.
     * Archives specified as relative URIs should be interpreted relative to the
     * codebase attribute.
     *
     * @param archive
     *            Space-separated list of URIs with resources relevant to the
     *            object
     */
    public void setArchive(String archive) {
        if (this.archive != archive) {
            this.archive = archive;
            needsRebuild = true;
        }
    }

    /**
     * Call this after changing values of widget. It will rebuild embedding
     * structure if needed.
     */
    public void rebuildIfNeeded() {
        if (needsRebuild) {
            needsRebuild = false;
            this.setHTML(createFlashEmbed());
        }
    }

    @Override
    public void setWidth(String width) {
        // explicitly not calling super here
        if (this.width != width) {
            this.width = width;
            needsRebuild = true;
        }
    }

    @Override
    public void setHeight(String height) {
        // explicitly not calling super here
        if (this.height != height) {
            this.height = height;
            needsRebuild = true;
        }
    }

    /**
     * Sets the map of object parameters. Parameters are optional information,
     * and they are passed to the instantiated object. Parameters are are stored
     * as name value pairs. Calling this method for a second time overrides the
     * previously given map.
     *
     * @param params
     *            the parameter map
     */
    public void setEmbedParams(Map<String, String> params) {
        if (params == null) {
            if (!embedParams.isEmpty()) {
                embedParams.clear();
                needsRebuild = true;
            }
            return;
        }

        if (!embedParams.equals(params)) {
            embedParams = new HashMap<>(params);
            needsRebuild = true;
        }
    }

    /**
     * Set dimensions of the containing layout slot so that the size of the
     * embed object can be calculated from percentages if needed.
     *
     * Triggers embed resizing if percentage sizes are in use.
     *
     * @since 7.7.8
     * @param slotOffsetHeight
     *            offset height of the layout slot
     * @param slotOffsetWidth
     *            offset width of the layout slot
     */
    public void setSlotHeightAndWidth(int slotOffsetHeight,
            int slotOffsetWidth) {
        this.slotOffsetHeight = slotOffsetHeight;
        this.slotOffsetWidth = slotOffsetWidth;
        if (hasPercentageHeight() || hasPercentageWidth()) {
            resizeEmbedElement();
        }

    }

    /**
     * Creates the embed String.
     *
     * @return the embed String
     */
    protected String createFlashEmbed() {
        /*
         * To ensure cross-browser compatibility we are using the twice-cooked
         * method to embed flash i.e. we add a OBJECT tag for IE ActiveX and
         * inside it a EMBED for all other browsers.
         */
        StringBuilder html = new StringBuilder();

        // Start the object tag
        html.append("<object ");

        /*
         * Add classid required for ActiveX to recognize the flash. This is a
         * predefined value which ActiveX recognizes and must be the given
         * value. More info can be found on
         * http://kb2.adobe.com/cps/415/tn_4150.html. Allow user to override
         * this by setting his own classid.
         */
        if (classId != null) {
            html.append(
                    "classid=\"" + WidgetUtil.escapeAttribute(classId) + "\" ");
        } else {
            html.append(
                    "classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" ");
        }

        /*
         * Add codebase required for ActiveX and must be exactly this according
         * to http://kb2.adobe.com/cps/415/tn_4150.html to work with the above
         * given classid. Again, see more info on
         * http://kb2.adobe.com/cps/415/tn_4150.html. Limiting Flash version to
         * 6.0.0.0 and above. Allow user to override this by setting his own
         * codebase
         */
        if (codebase != null) {
            html.append("codebase=\"" + WidgetUtil.escapeAttribute(codebase)
                    + "\" ");
        } else {
            html.append(
                    "codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\" ");
        }

        // Add width and height
        html.append("width=\"" + WidgetUtil.escapeAttribute(width) + "\" ");
        html.append("height=\"" + WidgetUtil.escapeAttribute(height) + "\" ");
        html.append("type=\"application/x-shockwave-flash\" ");

        // Codetype
        if (codetype != null) {
            html.append("codetype=\"" + WidgetUtil.escapeAttribute(codetype)
                    + "\" ");
        }

        // Standby
        if (standby != null) {
            html.append(
                    "standby=\"" + WidgetUtil.escapeAttribute(standby) + "\" ");
        }

        // Archive
        if (archive != null) {
            html.append(
                    "archive=\"" + WidgetUtil.escapeAttribute(archive) + "\" ");
        }

        // End object tag
        html.append('>');

        // Ensure we have an movie parameter
        if (embedParams.get("movie") == null) {
            embedParams.put("movie", source);
        }

        // Add parameters to OBJECT
        for (String name : embedParams.keySet()) {
            html.append("<param ");
            html.append("name=\"" + WidgetUtil.escapeAttribute(name) + "\" ");
            html.append("value=\""
                    + WidgetUtil.escapeAttribute(embedParams.get(name))
                    + "\" ");
            html.append("/>");
        }

        // Build inner EMBED tag
        html.append("<embed ");
        html.append("src=\"" + WidgetUtil.escapeAttribute(source) + "\" ");
        if (hasPercentageWidth() && slotOffsetWidth >= 0) {
            html.append("width=\"" + getRelativePixelWidth() + "\" ");
        } else {
            html.append("width=\"" + WidgetUtil.escapeAttribute(width) + "\" ");
        }

        if (hasPercentageHeight() && slotOffsetHeight >= 0) {
            html.append("height=\"" + getRelativePixelHeight() + "px\" ");
        } else {
            html.append(
                    "height=\"" + WidgetUtil.escapeAttribute(height) + "\" ");
        }

        html.append("type=\"application/x-shockwave-flash\" ");

        // Add the parameters to the Embed
        for (String name : embedParams.keySet()) {
            html.append(WidgetUtil.escapeAttribute(name));
            html.append('=');
            html.append("\"" + WidgetUtil.escapeAttribute(embedParams.get(name))
                    + "\"");
        }

        // End embed tag
        html.append("></embed>");

        if (altText != null) {
            html.append("<noembed>");
            html.append(altText);
            html.append("</noembed>");
        }

        // End object tag
        html.append("</object>");

        return html.toString();
    }

    private void resizeEmbedElement() {
        // find <embed> element
        com.google.gwt.dom.client.Element objectElem = getElement()
                .getFirstChildElement();
        com.google.gwt.dom.client.Element objectChild = objectElem
                .getFirstChildElement();
        while (!"EMBED".equalsIgnoreCase(objectChild.getTagName())) {
            objectChild = objectChild.getNextSiblingElement();
            if (objectChild == null) {
                return;
            }
        }
        // update height & width from slot offset, if percentage size is given
        if (hasPercentageHeight() && slotOffsetHeight >= 0) {
            objectChild.setAttribute("height", getRelativePixelHeight());
        }
        if (hasPercentageWidth() && slotOffsetWidth >= 0) {
            objectChild.setAttribute("width", getRelativePixelWidth());
        }

    }

    private String getRelativePixelWidth() {
        float relative = WidgetUtil.parseRelativeSize(width);
        int widthInPixels = (int) (relative / 100) * slotOffsetWidth;
        return widthInPixels + "px";
    }

    private String getRelativePixelHeight() {
        float relative = WidgetUtil.parseRelativeSize(height);
        int heightInPixels = (int) (relative / 100) * slotOffsetHeight;
        return heightInPixels + "px";
    }

    private boolean hasPercentageHeight() {
        return ((height != null) && (height.indexOf('%') > 0));
    }

    private boolean hasPercentageWidth() {
        return ((width != null) && (width.indexOf('%') > 0));
    }

}
