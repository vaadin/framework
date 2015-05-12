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
package com.vaadin.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.WidgetUtil;

public class VFlash extends HTML {

    public static final String CLASSNAME = "v-flash";

    protected String source;
    protected String altText;
    protected String classId;
    protected String codebase;
    protected String codetype;
    protected String standby;
    protected String archive;
    protected Map<String, String> embedParams = new HashMap<String, String>();
    protected boolean needsRebuild = false;
    protected String width;
    protected String height;

    public VFlash() {
        setStyleName(CLASSNAME);
    }

    public void setSource(String source) {
        if (this.source != source) {
            this.source = source;
            needsRebuild = true;
        }
    }

    public void setAlternateText(String altText) {
        if (this.altText != altText) {
            this.altText = altText;
            needsRebuild = true;
        }
    }

    public void setClassId(String classId) {
        if (this.classId != classId) {
            this.classId = classId;
            needsRebuild = true;
        }
    }

    public void setCodebase(String codebase) {
        if (this.codebase != codebase) {
            this.codebase = codebase;
            needsRebuild = true;
        }
    }

    public void setCodetype(String codetype) {
        if (this.codetype != codetype) {
            this.codetype = codetype;
            needsRebuild = true;
        }
    }

    public void setStandby(String standby) {
        if (this.standby != standby) {
            this.standby = standby;
            needsRebuild = true;
        }
    }

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
        // super.setWidth(height);

        if (this.width != width) {
            this.width = width;
            needsRebuild = true;
        }
    }

    @Override
    public void setHeight(String height) {
        // super.setHeight(height);

        if (this.height != height) {
            this.height = height;
            needsRebuild = true;
        }
    }

    public void setEmbedParams(Map<String, String> params) {
        if (params == null) {
            if (!embedParams.isEmpty()) {
                embedParams.clear();
                needsRebuild = true;
            }
            return;
        }

        if (!embedParams.equals(params)) {
            embedParams = new HashMap<String, String>(params);
            needsRebuild = true;
        }
    }

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
            html.append("classid=\"" + WidgetUtil.escapeAttribute(classId)
                    + "\" ");
        } else {
            html.append("classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\" ");
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
            html.append("codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\" ");
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
            html.append("standby=\"" + WidgetUtil.escapeAttribute(standby)
                    + "\" ");
        }

        // Archive
        if (archive != null) {
            html.append("archive=\"" + WidgetUtil.escapeAttribute(archive)
                    + "\" ");
        }

        // End object tag
        html.append(">");

        // Ensure we have an movie parameter
        if (embedParams.get("movie") == null) {
            embedParams.put("movie", source);
        }

        // Add parameters to OBJECT
        for (String name : embedParams.keySet()) {
            html.append("<param ");
            html.append("name=\"" + WidgetUtil.escapeAttribute(name) + "\" ");
            html.append("value=\""
                    + WidgetUtil.escapeAttribute(embedParams.get(name)) + "\" ");
            html.append("/>");
        }

        // Build inner EMBED tag
        html.append("<embed ");
        html.append("src=\"" + WidgetUtil.escapeAttribute(source) + "\" ");
        html.append("width=\"" + WidgetUtil.escapeAttribute(width) + "\" ");
        html.append("height=\"" + WidgetUtil.escapeAttribute(height) + "\" ");
        html.append("type=\"application/x-shockwave-flash\" ");

        // Add the parameters to the Embed
        for (String name : embedParams.keySet()) {
            html.append(WidgetUtil.escapeAttribute(name));
            html.append("=");
            html.append("\""
                    + WidgetUtil.escapeAttribute(embedParams.get(name)) + "\"");
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

}
