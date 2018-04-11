/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.flash.FlashState;
import com.vaadin.ui.declarative.DesignContext;

/**
 * A component for displaying Adobe® Flash® content.
 *
 * @author Vaadin Ltd.
 * @since 7.0
 */
@SuppressWarnings("serial")
public class Flash extends AbstractEmbedded {

    /**
     * Creates a new empty Flash component.
     */
    public Flash() {

    }

    /**
     * Creates a new empty Flash component with the given caption.
     *
     * @param caption
     *            The caption for the component
     */
    public Flash(String caption) {
        setCaption(caption);
    }

    /**
     * Creates a new Flash component with the given caption and content.
     *
     * @param caption
     *            The caption for the component
     * @param source
     *            A Resource representing the Flash content that should be
     *            displayed
     */
    public Flash(String caption, Resource source) {
        this(caption);
        setSource(source);
    }

    @Override
    protected FlashState getState() {
        return (FlashState) super.getState();
    }

    @Override
    protected FlashState getState(boolean markAsDirty) {
        return (FlashState) super.getState(markAsDirty);
    }

    /**
     * This attribute specifies the base path used to resolve relative URIs
     * specified by the classid, data, and archive attributes. When absent, its
     * default value is the base URI of the current document.
     *
     * @param codebase
     *            The base path
     */
    public void setCodebase(String codebase) {
        if (codebase != getState().codebase || (codebase != null
                && !codebase.equals(getState().codebase))) {
            getState().codebase = codebase;
            requestRepaint();
        }
    }

    /**
     * Returns the codebase.
     *
     * @see #setCodebase(String)
     * @since 7.4.1
     * @return Current codebase.
     */
    public String getCodebase() {
        return getState(false).codebase;
    }

    /**
     * This attribute specifies the content type of data expected when
     * downloading the object specified by classid. This attribute is optional
     * but recommended when classid is specified since it allows the user agent
     * to avoid loading information for unsupported content types. When absent,
     * it defaults to the value of the type attribute.
     *
     * @param codetype
     *            the codetype to set.
     */
    public void setCodetype(String codetype) {
        if (codetype != getState().codetype || (codetype != null
                && !codetype.equals(getState().codetype))) {
            getState().codetype = codetype;
            requestRepaint();
        }
    }

    /**
     * Returns the current codetype.
     *
     * @see #setCodetype(String)
     * @since 7.4.1
     * @return Current codetype.
     */
    public String getCodetype() {
        return getState(false).codetype;
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
        if (archive != getState().archive
                || (archive != null && !archive.equals(getState().archive))) {
            getState().archive = archive;
            requestRepaint();
        }
    }

    /**
     * Returns current archive.
     *
     * @see #setArchive(String)
     * @since 7.4.1
     * @return Current archive.
     */
    public String getArchive() {
        return getState(false).archive;
    }

    /**
     * Sets standby.
     *
     * @param standby
     *            Standby string.
     */
    public void setStandby(String standby) {
        if (standby != getState().standby
                || (standby != null && !standby.equals(getState().standby))) {
            getState().standby = standby;
            requestRepaint();
        }
    }

    /**
     * Returns standby.
     *
     * @since 7.4.1
     * @return Standby string.
     */
    public String getStandby() {
        return getState(false).standby;
    }

    /**
     * Sets an object parameter. Parameters are optional information, and they
     * are passed to the instantiated object. Parameters are are stored as name
     * value pairs. This overrides the previous value assigned to this
     * parameter.
     *
     * @param name
     *            the name of the parameter.
     * @param value
     *            the value of the parameter.
     */
    public void setParameter(String name, String value) {
        if (getState().embedParams == null) {
            getState().embedParams = new HashMap<>();
        }
        getState().embedParams.put(name, value);
        requestRepaint();
    }

    /**
     * Gets the value of an object parameter. Parameters are optional
     * information, and they are passed to the instantiated object. Parameters
     * are are stored as name value pairs.
     *
     * @return the Value of parameter or null if not found.
     */
    public String getParameter(String name) {
        return getState(false).embedParams != null
                ? getState(false).embedParams.get(name)
                : null;
    }

    /**
     * Removes an object parameter from the list.
     *
     * @param name
     *            the name of the parameter to remove.
     */
    public void removeParameter(String name) {
        if (getState().embedParams == null) {
            return;
        }
        getState().embedParams.remove(name);
        requestRepaint();
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);

        // Parameters, in alphabetic order
        List<String> paramNames = new ArrayList<>();
        for (String param : getParameterNames()) {
            paramNames.add(param);
        }

        Collections.sort(paramNames);
        for (String param : paramNames) {
            design.appendElement("parameter").attr("name", param).attr("value",
                    getParameter(param));
        }
    }

    /**
     * Returns an iterable with declared parameter names.
     *
     * @see #setParameter(String, String)
     * @see #getParameter(String)
     * @since 7.4.1
     * @return An iterable with declared parameter names.
     */
    public Iterable<String> getParameterNames() {
        Map<String, String> map = getState(false).embedParams;
        if (map == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(map.keySet());
        }
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        for (Element paramElement : design.getElementsByTag("parameter")) {
            setParameter(paramElement.attr("name"), paramElement.attr("value"));
        }
    }

}
